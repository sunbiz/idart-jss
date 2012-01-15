package org.celllife.idart.gui.patient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.Criteria;
import org.hibernate.Query;


public class StudyAlerts  extends GenericFormGui {
	
	
	private Table tblAlerts;

	private Group grpAlerts;

	public StudyAlerts(Shell shell) {
		super(shell, HibernateUtil.getNewSession());
	}

	private static Logger log = Logger.getLogger(AddPatientToStudy.class);
	
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("d MMM yyyy ' at ' HH:mm a"); //$NON-NLS-1$

	@Override
	protected void clearForm() {
		
		
		MessageBox m = new MessageBox(getShell(), SWT.ICON_WARNING
				| SWT.YES | SWT.NO);
		m.setText(Messages.getString("StudyAlerts.deleteWarningTitle"));
		m
		.setMessage(Messages.getString("StudyAlerts.deleteWarning"));

		if (m.open() == SWT.YES) {
			tblAlerts.removeAll();
			String hql = "update Alerts set Void = TRUE";
			Query query = hSession.createQuery(hql);
			query.executeUpdate();
			hSession.flush();
			
		}
		
	}

	@Override
	protected void cmdCancelWidgetSelected() {
		closeShell(true);
	}

	@Override
	protected void cmdClearWidgetSelected() {
		clearForm();
	}

	@Override
	protected void cmdSaveWidgetSelected() {
		
	}

	@Override
	protected void createCompButtons() {
		buildCompButtons();
		
	}

	private void createTable() {

			tblAlerts = new Table(grpAlerts, SWT.FULL_SELECTION);
			tblAlerts.setHeaderVisible(true);
			tblAlerts.setLinesVisible(true);
			tblAlerts.setBounds(new Rectangle(5, 10,780, 300));
			tblAlerts.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

			TableColumn tblClmNumber = new TableColumn(tblAlerts, SWT.NONE);
			tblClmNumber.setWidth(250);
			tblClmNumber.setText("Date");

			TableColumn tblClmDrugName = new TableColumn(tblAlerts, SWT.NONE);
			tblClmDrugName.setWidth(530);
			tblClmDrugName.setText("Alert Message");
			
			populateTable();

	}
	
	
	@SuppressWarnings("unchecked")
	private void populateTable() {
//		getHSession().get);
		String hql = "select alert from  Alerts as alert where alert.Void = FALSE order by alert.alertDate";
		List<Alerts> result = hSession.createQuery(hql).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		if (result == null) return;

		if (result.size()==0) {
			btnClear.setEnabled(false);
		}else{
			
			for (Alerts alert: result){
				TableItem ti = new TableItem(tblAlerts,SWT.NONE);
				ti.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
				String date = timeFormat.format(alert.getAlertDate());
				ti.setText(0,date);
				
				ti.setText(1,alert.getAlertMessage());
		
			}
			
		}
		
		
		
	}

	@Override
	protected void createCompHeader() {
		String headerTxt = "Study Alerts";
		iDartImage icoImage = iDartImage.PACKAGESARRIVE;
		buildCompHeader(headerTxt, icoImage);
	}

	@Override
	protected void createContents() {
		grpAlerts = new Group(getShell(), SWT.BORDER);
		grpAlerts.setBounds(new Rectangle(5, 137, 790, 369));
		createTable();
	}

	@Override
	protected void enableFields(boolean enable) {
		
	}

	@Override
	protected boolean fieldsOk() {
		return false;
	}

	@Override
	protected boolean submitForm() {
		return false;
	}

	@Override
	protected void createShell() {
		String shellTxt = Messages.getString("StudyAlerts.title"); //$NON-NLS-1$;
		Rectangle bounds = new Rectangle(25, 0, 800, 630);
		buildShell(shellTxt, bounds);
	}

	@Override
	protected void setLogger() {
		super.setLog(log);
	}
	
	
	protected void buildCompButtons() {

		Composite myCmp = new Composite(getShell(), SWT.NONE);

		RowLayout rowlyt = new RowLayout();
		rowlyt.justify = true;
		rowlyt.pack = false;
		rowlyt.spacing = 10;
		myCmp.setLayout(rowlyt);

		RowData rowD = new RowData(170, 30);

		setCompButtons(new Composite(myCmp, SWT.NONE));
		getCompButtons().setLayout(rowlyt);


		
		// btnClear
		btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear.setText(Messages.getString("genericformgui.button.clear.text")); //$NON-NLS-1$
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear.setToolTipText(Messages
				.getString("genericformgui.button.clear.tooltip")); //$NON-NLS-1$


		// btnCancel
		btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setText(Messages.getString("common.button.close.text")); //$NON-NLS-1$
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setToolTipText(Messages
				.getString("genericformgui.button.cancel.tooltip")); //$NON-NLS-1$


		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdClearWidgetSelected();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});

		Control[] buttons = getCompButtons().getChildren();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setLayoutData(rowD);
		}

		getCompButtons().pack();
		Rectangle b = getShell().getBounds();
		myCmp.setBounds(0, b.height - 79, b.width, 40);
	}

}
