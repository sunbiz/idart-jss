package org.celllife.idart.gui.dataExports;

import model.manager.exports.AppointmentColumnsGroup;
import model.manager.exports.ExportColumn;
import model.manager.exports.columns.AppointmentDetailsEnum;
import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.IColumnEnum;

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

public class AppointmentGroupGUI implements iDataExport {

	private Composite compTblModifiers;
	private CCombo columnModifier;
	private Text txtModifierNum;
	private int ypos;
	private CheckboxTableViewer tblViewerAppointmentAttributes;
	private Label lblHead;
	private Button rdBtnRecent;
	private Button rdBtnOldest;
	private Button rdBtnFirstAndLast;
	private Group grpSelectOption;
	private Label lblNumRecentAppointments;
	private Label lblNumOldestAppointments;
	private Text txtNumRecent;
	private Text txtNumOldest;
	private final Object[] appointmentFields;
	private AppointmentColumnsGroup appointmentGroup;

	public AppointmentGroupGUI() {
		appointmentFields = AppointmentDetailsEnum.values();
	}

	@Override
	public void createView(Composite compDetails) {

		ypos = 0;

		tblViewerAppointmentAttributes = CheckboxTableViewer.newCheckList(
				compDetails, SWT.BORDER);
		tblViewerAppointmentAttributes.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(0, ypos, 250, 240));
		tblViewerAppointmentAttributes.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblViewerAppointmentAttributes
		.setContentProvider(new ExportListViewerContentProvider());
		tblViewerAppointmentAttributes
		.setLabelProvider(new ExportListViewerLabelProvider());
		tblViewerAppointmentAttributes.setInput(appointmentFields);

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
		lblNumRecentAppointments = new Label(grpSelectOption, SWT.LEFT);
		lblNumRecentAppointments.setText("appointment(s)");
		lblNumRecentAppointments.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblNumRecentAppointments.setBounds(143, row1Y + 6, 70, 16);

		lblNumOldestAppointments = new Label(grpSelectOption, SWT.LEFT);
		lblNumOldestAppointments.setText("appointment(s)");
		lblNumOldestAppointments.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblNumOldestAppointments.setBounds(143, row2Y + 6, 70, 16);

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
		rdBtnFirstAndLast.setText("first and last appointments");

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

		AppointmentColumnsGroup eg = (AppointmentColumnsGroup) column;
		IColumnEnum[] values = eg.getColumns();
		tblViewerAppointmentAttributes.setCheckedElements(values);

		int AppointmentsNum = eg.getModifierNum();

		if ((eg.getModifier() == ColumnModifier.MODIFIER_NEWEST)
				|| (eg.getModifier() == ColumnModifier.MODIFIER_NEWEST_NUM)) {
			txtNumRecent.setText("" + AppointmentsNum);
			rdBtnRecent.setSelection(true);
			rdBtnOldest.setSelection(false);
			rdBtnFirstAndLast.setSelection(false);

		} else if ((eg.getModifier() == ColumnModifier.MODIFIER_OLDEST)
				|| (eg.getModifier() == ColumnModifier.MODIFIER_OLDEST_NUM)) {
			txtNumOldest.setText("" + AppointmentsNum);
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
	public AppointmentColumnsGroup getColumn() {

		Object[] appointmentSelections = tblViewerAppointmentAttributes
		.getCheckedElements();

		// no appointment fields were selected
		if (appointmentSelections == null || appointmentSelections.length == 0)
			return null;

		AppointmentDetailsEnum[] appointmentSelection = new AppointmentDetailsEnum[appointmentSelections.length];

		for (int i = 0; i < appointmentSelections.length; i++) {
			appointmentSelection[i] = (AppointmentDetailsEnum) appointmentSelections[i];
		}

		int appointmentsNum = 0;
		ColumnModifier cm;
		if (rdBtnOldest.getSelection()) {
			appointmentsNum = Integer.parseInt(txtNumOldest.getText());
			cm = (appointmentsNum <= 1) ? ColumnModifier.MODIFIER_OLDEST
					: ColumnModifier.MODIFIER_OLDEST_NUM;
		} else if (rdBtnRecent.getSelection()) {
			appointmentsNum = Integer.parseInt(txtNumRecent.getText());
			cm = (appointmentsNum <= 1) ? ColumnModifier.MODIFIER_NEWEST
					: ColumnModifier.MODIFIER_NEWEST_NUM;

		} else {
			cm = ColumnModifier.MODIFIER_FIRST_AND_LAST;
		}


		appointmentGroup = new AppointmentColumnsGroup(cm, new Integer(
				appointmentsNum), appointmentSelection);

		return appointmentGroup;

	}

	@Override
	public boolean fieldsOk() {

		try {

			@SuppressWarnings("unused")
			int numAppointments = (rdBtnOldest.getSelection()) ? Integer
					.parseInt(txtNumOldest.getText()) : Integer
					.parseInt(txtNumRecent.getText());

		} catch (NumberFormatException e) {

			return false;

		}
		return true;
	}

	@Override
	public void clearForm() {
		tblViewerAppointmentAttributes.setAllChecked(false);
		txtNumOldest.setText("1");
		txtNumRecent.setText("1");
		rdBtnOldest.setSelection(false);
		rdBtnFirstAndLast.setSelection(false);
		rdBtnRecent.setSelection(true);
	}

}
