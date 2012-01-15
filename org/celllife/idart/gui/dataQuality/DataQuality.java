package org.celllife.idart.gui.dataQuality;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.dataQualityexports.DataQualityInterface;
import org.celllife.idart.gui.dataQualityexports.InaccurateEpisodes;
import org.celllife.idart.gui.dataQualityexports.LTFU;
import org.celllife.idart.gui.dataQualityexports.PatientDataQuality;
import org.celllife.idart.gui.dataQualityexports.RegimenBreakdowns;
import org.celllife.idart.gui.dataQualityexports.UnusedPrescriptions;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.SafeSaveDialog;
import org.celllife.idart.misc.SafeSaveDialog.FileType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
/**
 */
public class DataQuality extends GenericOthersGui {

	private ListViewer list1;

	private Vector<DataQualityInterface> reportnames;

	/***************************************************************************
	 * Default Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public DataQuality(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		activate();
	}

	/**
	 * This method initializes newPrintBlankLabel
	 */
	@Override
	protected void createShell() {
		buildShell("Data Quality Report", new Rectangle(0, 0, 600, 500));

	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		buildCompHeader(" Data Quality ", iDartImage.PATIENTINFOLABEL);
	}

	/**
	 * This method initializes compOptions
	 * 
	 */
	@Override
	protected void createCompOptions() {

		// Patient ID
		Composite grpScrInfo = new Composite(getShell(), SWT.NONE);
		grpScrInfo.setBounds(25, 70, 550, 300);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 20;
		grpScrInfo.setLayout(gridLayout);
		
		Composite labelParent = new Composite(grpScrInfo, SWT.BORDER);
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = fillLayout.marginWidth = 10;
		labelParent.setLayout(fillLayout);
		labelParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label lblSelectPatient = new Label(labelParent, SWT.WRAP);
		String infoTxt = Messages.getString("dataquality.label.patient");
		lblSelectPatient.setText(infoTxt);
		lblSelectPatient.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		reportnames = new Vector<DataQualityInterface>();
		reportnames.add(new PatientDataQuality());
		reportnames.add(new UnusedPrescriptions());
		reportnames.add(new LTFU(2));
		reportnames.add(new LTFU(1));
		reportnames.add(new InaccurateEpisodes(1));
		reportnames.add(new InaccurateEpisodes(2));
		reportnames.add(new InaccurateEpisodes(3));
		reportnames.add(new InaccurateEpisodes(4));
		reportnames.add(new InaccurateEpisodes(5));
		reportnames.add(new RegimenBreakdowns());
		
		list1 = new ListViewer(grpScrInfo, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		list1.getList().setLayoutData(gridData);
		list1.setContentProvider(new ArrayContentProvider());
		list1.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof DataQualityInterface) {
					DataQualityInterface report = (DataQualityInterface) element;
					return report.getMessage();
				}
				return null;
			}
		});

		list1.setInput(reportnames);
		grpScrInfo.layout(true);
	}

	/**
	 * This method initialises compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		Button btnRunData = new Button(getCompButtons(), SWT.PUSH);
		btnRunData.setText("Run Data Quality Report");
		btnRunData.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnRunData
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						
						IStructuredSelection selection = (IStructuredSelection) list1
								.getSelection();
						DataQualityInterface selectedElement = (DataQualityInterface) selection
								.getFirstElement();
						DataQualityInterface dqr = selectedElement;

						cmdRunDataQualityReport(dqr);
					}
				});
		btnRunData
				.setToolTipText(Messages.getString("dataquality.button.tooltip"));
		btnRunData.setEnabled(true);

		Button btnClose = new Button(getCompButtons(), SWT.PUSH);
		btnClose.setText("Close");
		btnClose.setToolTipText(Messages.getString("dataquality.button.close.tooltip"));
		btnClose
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						cmdCloseSelected();
					}
				});
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}


	private void cmdRunDataQualityReport(DataQualityInterface dqr) {
		SafeSaveDialog dlg = new SafeSaveDialog(getShell(), FileType.CSV);
		dlg.setFileName(dqr.getFileName());
		String fileName = dlg.open();

		if (fileName == null || fileName.isEmpty())
			return;

		dqr.setFileName(fileName);
		dqr.getData();
		
		try {
			new ProgressMonitorDialog(getShell()).run(true, true, dqr);

			if (dqr.isReportSuccessfullyCompleted()) {
				showMessage(MessageDialog.INFORMATION, "Report Complete",
						"Report completed");
			}
			else {
				showMessage(MessageDialog.WARNING,"File Open", dqr.alreadyUsed());
			}

			Program.launch(fileName);
		} catch (InvocationTargetException e1) {
			getLog().error("Error running data quality report: " + dqr.getClass(), e1);
		} catch (InterruptedException e1) {
			// do nothing
		}
	}

	/**
	 * This method is called when the user pressed the "Close" button It
	 * disposes the current shell.
	 */
	private void cmdCloseSelected() {
		closeShell(true);
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

}
