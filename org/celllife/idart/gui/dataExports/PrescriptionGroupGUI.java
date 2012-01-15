package org.celllife.idart.gui.dataExports;

import model.manager.exports.ExportColumn;
import model.manager.exports.PrescriptionColumnsGroup;
import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.IColumnEnum;
import model.manager.exports.columns.PrescriptionDetailsEnum;

import org.celllife.idart.gui.dataExports.listViewerUtils.ExportListViewerContentProvider;
import org.celllife.idart.gui.dataExports.listViewerUtils.ExportListViewerLabelProvider;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PrescriptionGroupGUI implements iDataExport {

	private Composite compTblModifiers;
	private CCombo columnModifier;
	private Text txtModifierNum;
	private int ypos;
	private CheckboxTableViewer tblViewerPrescriptionAttributes;
	private Label lblHead;
	private Button rdBtnRecent;
	private Button rdBtnOldest;
	private Button rdBtnFirstAndLast;
	private Group grpSelectOption;
	private Label lblNumRecentPrescriptions;
	private Label lblNumOldestPrescriptions;
	private Text txtNumRecent;
	private Text txtNumOldest;
	private final Object[] prescriptionFields;
	private PrescriptionColumnsGroup prescriptionGroup;

	public PrescriptionGroupGUI() {
		prescriptionFields = PrescriptionDetailsEnum.values();
	}

	@Override
	public void createView(Composite compDetails) {

		ypos = 0;

		tblViewerPrescriptionAttributes = CheckboxTableViewer.newCheckList(
				compDetails, SWT.BORDER);
		tblViewerPrescriptionAttributes.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(0, ypos, 250, 240));
		tblViewerPrescriptionAttributes.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblViewerPrescriptionAttributes
		.setContentProvider(new ExportListViewerContentProvider());
		tblViewerPrescriptionAttributes
		.setLabelProvider(new ExportListViewerLabelProvider());
		tblViewerPrescriptionAttributes.setInput(prescriptionFields);

		ypos = 0;
		compTblModifiers = new Composite(compDetails, SWT.NONE);
		compTblModifiers.setBounds(new Rectangle(0, 250, 250, 100));

		lblHead = new Label(compTblModifiers, SWT.LEFT);
		lblHead.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblHead.setBounds(new Rectangle(4, 2, 150, 15));
		lblHead.setText("I want to see:");

		grpSelectOption = new Group(compTblModifiers, SWT.NONE);
		grpSelectOption.setBounds(new Rectangle(5, 17, 238, 75));
		grpSelectOption.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		int row1Y = 8;
		int row2Y = 28;
		int row3Y = 48;
		lblNumRecentPrescriptions = new Label(grpSelectOption, SWT.LEFT);
		lblNumRecentPrescriptions.setText("prescription(s)");
		lblNumRecentPrescriptions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblNumRecentPrescriptions.setBounds(143, row1Y + 6, 70, 16);

		lblNumOldestPrescriptions = new Label(grpSelectOption, SWT.LEFT);
		lblNumOldestPrescriptions.setText("prescription(s)");
		lblNumOldestPrescriptions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblNumOldestPrescriptions.setBounds(143, row2Y + 6, 70, 16);

		rdBtnRecent = new Button(grpSelectOption, SWT.RADIO);
		rdBtnRecent.setBounds(new Rectangle(5, row1Y, 95, 25));
		rdBtnRecent.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnRecent.setText("most recent");
		rdBtnRecent.setSelection(true);

		rdBtnOldest = new Button(grpSelectOption, SWT.RADIO);
		rdBtnOldest.setBounds(new Rectangle(5, row2Y, 95, 25));
		rdBtnOldest.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnOldest.setText("oldest");

		rdBtnFirstAndLast = new Button(grpSelectOption, SWT.RADIO);
		rdBtnFirstAndLast.setBounds(new Rectangle(5, row3Y, 155, 25));
		rdBtnFirstAndLast.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnFirstAndLast.setText("first and last prescriptions");

		txtNumRecent = new Text(grpSelectOption, SWT.NONE | SWT.BORDER);
		txtNumRecent.setText("1");
		txtNumRecent.setBounds(108, row1Y + 3, 30, 18);
		txtNumRecent.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNumRecent
		.addFocusListener(new org.eclipse.swt.events.FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				rdBtnRecent.setSelection(true);
				rdBtnOldest.setSelection(false);
				rdBtnFirstAndLast.setSelection(false);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});

		txtNumOldest = new Text(grpSelectOption, SWT.NONE | SWT.BORDER);
		txtNumOldest.setText("1");
		txtNumOldest.setBounds(108, row2Y + 5, 30, 18);
		txtNumOldest.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNumOldest
		.addFocusListener(new org.eclipse.swt.events.FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				rdBtnRecent.setSelection(false);
				rdBtnOldest.setSelection(true);
				rdBtnFirstAndLast.setSelection(false);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});

	}

	public void cmbcolumnModifierSelected() {
		String selection = columnModifier.getText();
		if (selection.endsWith("NUM")) {
			txtModifierNum.setEnabled(true);
		} else {
			txtModifierNum.setEnabled(false);
		}
	}

	@Override
	public void updateView(ExportColumn column) {

		PrescriptionColumnsGroup eg = (PrescriptionColumnsGroup) column;
		IColumnEnum[] values = eg.getColumns();
		tblViewerPrescriptionAttributes.setCheckedElements(values);

		int PrescriptionsNum = eg.getModifierNum();

		if ((eg.getModifier() == ColumnModifier.MODIFIER_NEWEST)
				|| (eg.getModifier() == ColumnModifier.MODIFIER_NEWEST_NUM)) {
			txtNumRecent.setText("" + PrescriptionsNum);
			rdBtnRecent.setSelection(true);
			rdBtnOldest.setSelection(false);
			rdBtnFirstAndLast.setSelection(false);

		} else if ((eg.getModifier() == ColumnModifier.MODIFIER_OLDEST)
				|| (eg.getModifier() == ColumnModifier.MODIFIER_OLDEST_NUM)) {
			txtNumOldest.setText("" + PrescriptionsNum);
			rdBtnRecent.setSelection(false);
			rdBtnOldest.setSelection(true);
			rdBtnFirstAndLast.setSelection(false);

		} else if (eg.getModifier() == ColumnModifier.MODIFIER_FIRST_AND_LAST) {
			rdBtnFirstAndLast.setSelection(true);
			rdBtnRecent.setSelection(false);
			rdBtnOldest.setSelection(false);
		}

	}

	@Override
	public PrescriptionColumnsGroup getColumn() {

		Object[] prescriptionSelections = tblViewerPrescriptionAttributes
		.getCheckedElements();

		// no prescription fields were selected
		if (prescriptionSelections == null
				|| prescriptionSelections.length == 0)
			return null;

		PrescriptionDetailsEnum[] prescriptionSelection = new PrescriptionDetailsEnum[prescriptionSelections.length];

		for (int i = 0; i < prescriptionSelections.length; i++) {
			prescriptionSelection[i] = (PrescriptionDetailsEnum) prescriptionSelections[i];
		}

		int prescriptionsNum = 0;
		ColumnModifier cm;
		if (rdBtnOldest.getSelection()) {
			prescriptionsNum = Integer.parseInt(txtNumOldest.getText());
			cm = (prescriptionsNum <= 1) ? ColumnModifier.MODIFIER_OLDEST
					: ColumnModifier.MODIFIER_OLDEST_NUM;
		} else if (rdBtnRecent.getSelection()) {
			prescriptionsNum = Integer.parseInt(txtNumRecent.getText());
			cm = (prescriptionsNum <= 1) ? ColumnModifier.MODIFIER_NEWEST
					: ColumnModifier.MODIFIER_NEWEST_NUM;

		} else {
			cm = ColumnModifier.MODIFIER_FIRST_AND_LAST;
			prescriptionsNum = 2;
		}


		prescriptionGroup = new PrescriptionColumnsGroup(cm, new Integer(
				prescriptionsNum), prescriptionSelection);

		return prescriptionGroup;

	}

	@Override
	public boolean fieldsOk() {

		try {

			@SuppressWarnings("unused")
			int numPrescriptions = (rdBtnOldest.getSelection()) ? Integer
					.parseInt(txtNumOldest.getText()) : Integer
					.parseInt(txtNumRecent.getText());

		} catch (NumberFormatException e) {

			return false;

		}
		return true;
	}

	@Override
	public void clearForm() {
		tblViewerPrescriptionAttributes.setAllChecked(false);
		txtNumOldest.setText("1");
		txtNumRecent.setText("1");
		rdBtnOldest.setSelection(false);
		rdBtnFirstAndLast.setSelection(false);
		rdBtnRecent.setSelection(true);
	}

}
