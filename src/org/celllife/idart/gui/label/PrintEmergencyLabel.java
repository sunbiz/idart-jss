/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.celllife.idart.gui.label;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.commonobjects.iDartProperties.LabelType;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.print.label.BlankLabel;
import org.celllife.idart.print.label.DrugLabel;
import org.celllife.idart.print.label.PackageCoverLabel;
import org.celllife.idart.print.label.PrintLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 */
public class PrintEmergencyLabel extends GenericOthersGui {

	private Group grpOptions;

	private Label lblLabelType;

	private CCombo cmbLabelType;

	private Label lblNumToPrint;

	private Text txtNumToPrint;

	private Group grpLabelPreview;

	private Canvas canvasLabel;

	private Label lblBorderTop;

	private Label lblBorderLeft;

	private Label lblBorderBottom;

	private Label lblBorderRight;

	private Label lblBorderMiddle;

	private Label lblName;

	private Label lblPharmacist;

	private Label lblStockCenterAddress;

	private Text txtTakeLanguage1;

	// private Text txtTakeLanguage2;

	// private Text txtTakeLanguage3;

	private Text txtDrugName;

	private Text txtSpecialInstructions1;

	private Text txtSpecialInstructions2;

	private Text txtAmountPerTime;

	private Text txtNumOfRepetitions;

	private Text txtFormLanguage1;

	private Text txtTimesPerDayLanguage1;

	private Text txtPatientId;

	private Text txtPatientName;

	private Text txtDrugNotes;

	private Text txtDrugDate;

	private Text txtIssuesString;

	private Text txtPackPatientId;

	private Label lblPackBorder1;

	private Label lblPackBorder2;

	private Text txtPackHeader;

	private Text txtPackDate;

	private Label lblBorderVertical;

	private Text txtPackClinic;

	private Text txtPackBarcode;

	private final Text[] txtBlankLines = new Text[6];

	private Button btnPrintLabel;

	private Text txtDateExpected;

	private Text txtNextApp;

	private Label lblDrugDate;

	private Label lblNextApp;

	private Label lblBatchNo;

	private Text txtBatchNo;

	private Label lblExpiryDate;

	private Text txtExpiryDate;


	/***************************************************************************
	 * Default Constructor
	 * 
	 * @param parent
	 *            Shell
	 */
	public PrintEmergencyLabel(Shell parent) {
		super(parent, HibernateUtil.getNewSession());
		activate();
	}

	/**
	 * This method initializes newPrintBlankLabel
	 */
	@Override
	protected void createShell() {

		buildShell("Print a Custom Label", new Rectangle(0, 0, 600, 570));
		createMyComposites();
	}

	private void createMyComposites() {
		createGrpLabelPreview();
		createPackageLabel();
		createDrugLabel();
		createBlankLabel();
		showDrugLabel(false);
		showPackageLabel(false);
		showBlankLabel(false);
	}

	/**
	 * This method initializes compHeader
	 * 
	 */
	@Override
	protected void createCompHeader() {
		buildCompHeader("Print a Custom Label", iDartImage.PATIENTINFOLABEL);
	}

	/**
	 * This method initializes compOptions
	 * 
	 */
	@Override
	protected void createCompOptions() {

		grpOptions = new Group(getShell(), SWT.NONE);
		grpOptions.setBounds(new Rectangle(100, 80, 400, 100));
		grpOptions.setText("Printing Information");
		grpOptions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblLabelType = new Label(grpOptions, SWT.LEFT);
		lblLabelType.setText("Label Type:");
		lblLabelType.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblLabelType.setBounds(new Rectangle(10, 25, 90, 20));

		cmbLabelType = new CCombo(grpOptions, SWT.BORDER);
		cmbLabelType.setEditable(false);
		cmbLabelType.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbLabelType.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbLabelType.setBounds(new Rectangle(130, 25, 140, 20));
		cmbLabelType
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmbLabelTypeSelected();
			}
		});
		cmbLabelType.add("Drug Label");
		cmbLabelType.add("Package Label");
		cmbLabelType.add("Blank Label");

		lblNumToPrint = new Label(grpOptions, SWT.LEFT);
		lblNumToPrint.setText("Number To Print:");
		lblNumToPrint.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblNumToPrint.setBounds(new Rectangle(10, 55, 105, 20));
		txtNumToPrint = new Text(grpOptions, SWT.BORDER);
		txtNumToPrint.setEditable(false);
		txtNumToPrint.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		txtNumToPrint.setBounds(new Rectangle(130, 55, 50, 20));
		txtNumToPrint.setText("1");
		txtNumToPrint.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNumToPrint.setEditable(true);

	}

	/**
	 * This method initializes grpLabelPreview
	 * 
	 */
	private void createGrpLabelPreview() {

		grpLabelPreview = new Group(getShell(), SWT.NONE);
		grpLabelPreview.setBounds(new Rectangle(125, 200, 350, 260));
		grpLabelPreview.setText("Label Preview");
		grpLabelPreview.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		createCanvasLabel();

	}

	/**
	 * This method initializes canvasLabel
	 * 
	 */
	private void createCanvasLabel() {

		canvasLabel = new Canvas(grpLabelPreview, SWT.NONE);
		canvasLabel.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		canvasLabel.setBounds(new Rectangle(15, 30, 320, 225));

		createLabelBorders();

		// StockCenter Information
		lblName = new Label(canvasLabel, SWT.CENTER);
		lblName.setBounds(new Rectangle(2, 4, 316, 17));
		lblName.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		lblName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10));

		lblPharmacist = new Label(canvasLabel, SWT.CENTER);
		lblPharmacist.setBounds(new Rectangle(2, 20, 316, 15));
		lblPharmacist.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		lblPharmacist.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblStockCenterAddress = new Label(canvasLabel, SWT.CENTER);
		lblStockCenterAddress.setBounds(new Rectangle(2, 34, 316, 12));
		lblStockCenterAddress.setBackground(ResourceUtils
				.getColor(iDartColor.WHITE));
		lblStockCenterAddress.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));

		populateStockCenterDetails();
	}

	private void createLabelBorders() {

		lblBorderTop = new Label(canvasLabel, SWT.NONE);
		lblBorderTop.setBackground(ResourceUtils.getColor(iDartColor.BLACK));
		lblBorderTop.setBounds(new Rectangle(0, 1, 320, 2));

		lblBorderLeft = new Label(canvasLabel, SWT.NONE);
		lblBorderLeft.setBackground(ResourceUtils.getColor(iDartColor.BLACK));
		lblBorderLeft.setBounds(new Rectangle(0, 1, 2, 208));

		lblBorderBottom = new Label(canvasLabel, SWT.NONE);
		lblBorderBottom.setBounds(new Rectangle(0, 208, 320, 2));
		lblBorderBottom.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		lblBorderRight = new Label(canvasLabel, SWT.NONE);
		lblBorderRight.setBounds(new Rectangle(318, 2, 2, 208));
		lblBorderRight.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		lblBorderMiddle = new Label(canvasLabel, SWT.NONE);
		lblBorderMiddle.setBounds(new Rectangle(2, 46, 320, 2));
		lblBorderMiddle.setBackground(ResourceUtils.getColor(iDartColor.BLACK));
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		btnPrintLabel = new Button(getCompButtons(), SWT.PUSH);
		btnPrintLabel.setText("Print");
		btnPrintLabel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPrintLabel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmbPrintLabelSelected();
			}
		});
		btnPrintLabel
		.setToolTipText("Press this button to print the specified custom label.");

		Button btnClear = new Button(getCompButtons(), SWT.PUSH);
		btnClear.setText("Clear");
		btnClear
		.setToolTipText("Press this button to clear all the information \nyou've entered), so that you can start again.");
		btnClear
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmbClearSelected();
			}
		});
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Button btnClose = new Button(getCompButtons(), SWT.PUSH);
		btnClose.setText("Close");
		btnClose.setToolTipText("Press this button to close this screen.");
		btnClose
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmbCloseSelected();
			}
		});
		btnClose.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	private void cmbLabelTypeSelected() {

		if (cmbLabelType.getText().equals("Drug Label")) {
			showDrugLabel(true);
			showPackageLabel(false);
			showBlankLabel(false);
			resetDrugLabelFields();
		} else if (cmbLabelType.getText().equals("Package Label")) {
			showPackageLabel(true);
			showDrugLabel(false);
			showBlankLabel(false);
			resetPackageLabelFields();
		} else {
			showBlankLabel(true);
			showDrugLabel(false);
			showPackageLabel(false);
			resetBlankLabelFields();
		}
	}

	private void populateStockCenterDetails() {

		// Set up the stockCenter information on the label preview
		lblName.setText(LocalObjects.pharmacy.getPharmacyName());
		lblPharmacist.setText(LocalObjects.pharmacy.getPharmacist());
		lblStockCenterAddress.setText(LocalObjects.pharmacy.getStreet().concat(
		", ").concat(LocalObjects.pharmacy.getCity()).concat(", Tel: ")
		.concat(LocalObjects.pharmacy.getContactNo()));
	}

	private void createBlankLabel() {

		txtBlankLines[0] = new Text(canvasLabel, SWT.NONE);

		int labelTop = 60;
		int lineTopY = 25;

		for (int i = 0; i < 6; i++) {
			txtBlankLines[i] = new Text(canvasLabel, SWT.NONE);
			txtBlankLines[i].setBounds(new Rectangle(2,
					labelTop + i * lineTopY, 316, 20));
			txtBlankLines[i].setText("Line " + (i + 1));
			txtBlankLines[i].setTextLimit(32);
			txtBlankLines[i].setBackground(ResourceUtils
					.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
			txtBlankLines[i].setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_12));
		}
	}

	private void createPackageLabel() {

		// Patient Id
		txtPackPatientId = new Text(canvasLabel, SWT.CENTER);
		txtPackPatientId.setBounds(new Rectangle(2, 60, 316, 33));
		txtPackPatientId.setText("PS2-3502-1");
		txtPackPatientId.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtPackPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_20));

		lblPackBorder1 = new Label(canvasLabel, SWT.NONE);
		lblPackBorder1.setBounds(new Rectangle(2, 96, 320, 2));
		lblPackBorder1.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		txtPackHeader = new Text(canvasLabel, SWT.CENTER);
		txtPackHeader.setBounds(new Rectangle(161, 101, 158, 22));
		txtPackHeader.setText("Issue 1 of 6 month script");
		txtPackHeader.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtPackHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackDate = new Text(canvasLabel, SWT.CENTER);

		txtPackDate.setBounds(new Rectangle(161, 127, 158, 22));
		Date today = new Date();
		txtPackDate.setText("Packed "
				+ new SimpleDateFormat("dd MMM yy").format(today));
		txtPackDate.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtPackDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblBorderVertical = new Label(canvasLabel, SWT.NONE);
		lblBorderVertical.setBounds(new Rectangle(160, 97, 2, 58));
		lblBorderVertical.setBackground(ResourceUtils
				.getColor(iDartColor.BLACK));

		txtDateExpected = new Text(canvasLabel, SWT.CENTER);

		txtDateExpected.setBounds(new Rectangle(2, 127, 158, 22));
		txtDateExpected.setText("Patient Expected "
				+ new SimpleDateFormat("dd MMM yy").format(today));
		txtDateExpected.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtDateExpected.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtPackClinic = new Text(canvasLabel, SWT.CENTER);
		txtPackClinic.setBounds(new Rectangle(2, 101, 158, 22));

		txtPackClinic.setText("Clinic");
		txtPackClinic.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtPackClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		lblPackBorder2 = new Label(canvasLabel, SWT.NONE);
		lblPackBorder2.setBounds(new Rectangle(2, 153, 320, 2));
		lblPackBorder2.setBackground(ResourceUtils.getColor(iDartColor.BLACK));

		txtPackBarcode = new Text(canvasLabel, SWT.CENTER);
		txtPackBarcode.setBounds(new Rectangle(2, 160, 316, 45));
		txtPackBarcode.setText("Package ID");
		txtPackBarcode.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtPackBarcode.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	private void createDrugLabel() {

		// Drug Information
		txtDrugName = new Text(canvasLabel, SWT.CENTER);
		txtDrugName.setBounds(new Rectangle(2, 50, 316, 17));
		txtDrugName.setText("Drug Name");
		txtDrugName.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtDrugName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDrugName.setFocus();

		txtSpecialInstructions1 = new Text(canvasLabel, SWT.CENTER);
		txtSpecialInstructions1.setBounds(new Rectangle(2, 70, 316, 16));
		txtSpecialInstructions1.setText("Special Instructions (line 1)");
		txtSpecialInstructions1.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_11));
		txtSpecialInstructions1.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		txtSpecialInstructions2 = new Text(canvasLabel, SWT.CENTER);
		txtSpecialInstructions2.setBounds(new Rectangle(2, 89, 316, 16));
		txtSpecialInstructions2.setText("Special Instructions (line 2)");
		txtSpecialInstructions2.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_11));
		txtSpecialInstructions2.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		// Dosage Information
		txtTakeLanguage1 = new Text(canvasLabel, SWT.NONE);
		// txtTakeLanguage1.setBounds(new Rectangle(4, 120, 37, 12));
		// Changed this - there is only 1 language for RHRU
		txtTakeLanguage1.setBounds(new Rectangle(4, 120, 37, 15));
		txtTakeLanguage1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtTakeLanguage1.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtTakeLanguage1.setText("Take");

		txtAmountPerTime = new Text(canvasLabel, SWT.CENTER);
		txtAmountPerTime.setBounds(new Rectangle(45, 110, 78, 40));
		txtAmountPerTime.setText("10.5");
		txtAmountPerTime.setFont(ResourceUtils.getFont(iDartFont.VERASANS_24));
		txtAmountPerTime.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		txtFormLanguage1 = new Text(canvasLabel, SWT.NONE);
		// txtFormLanguage1.setBounds(new Rectangle(126, 120, 71, 12));
		txtFormLanguage1.setBounds(new Rectangle(126, 120, 71, 15));
		txtFormLanguage1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtFormLanguage1.setText("capsules");
		txtFormLanguage1.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		txtNumOfRepetitions = new Text(canvasLabel, SWT.CENTER);
		txtNumOfRepetitions.setBounds(new Rectangle(200, 110, 30, 35));
		txtNumOfRepetitions.setText("3");
		txtNumOfRepetitions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_24));
		txtNumOfRepetitions.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		txtTimesPerDayLanguage1 = new Text(canvasLabel, SWT.NONE);
		txtTimesPerDayLanguage1.setBounds(new Rectangle(233, 120, 80, 15));
		txtTimesPerDayLanguage1.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		txtTimesPerDayLanguage1.setText("times per day");
		txtTimesPerDayLanguage1.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		// Patient Information
		txtPatientName = new Text(canvasLabel, SWT.NONE);
		txtPatientName.setBounds(new Rectangle(3, 170, 130, 15));
		txtPatientName.setText("Patient Name");
		txtPatientName.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_BOLD));
		txtPatientName.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		txtPatientId = new Text(canvasLabel, SWT.NONE);
		txtPatientId.setBounds(new Rectangle(3, 185, 130, 15));
		txtPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$
		txtPatientId.setFont(ResourceUtils.getFont(iDartFont.VERASANS_10_BOLD));
		txtPatientId.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		txtIssuesString = new Text(canvasLabel, SWT.LEFT);
		txtIssuesString.setBounds(new Rectangle(3, 190, 130, 15));
		txtIssuesString.setText("1 of 6 months");
		txtIssuesString.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_10_BOLD));
		txtIssuesString.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		lblNextApp = new Label(canvasLabel, SWT.LEFT);
		txtNextApp = new Text(canvasLabel, SWT.RIGHT);
		lblDrugDate = new Label(canvasLabel, SWT.LEFT);
		txtDrugDate = new Text(canvasLabel, SWT.RIGHT);
		// txtIssuesString = new Text(canvasLabel, SWT.RIGHT);
		lblBatchNo = new Label(canvasLabel, SWT.LEFT);
		txtBatchNo = new Text(canvasLabel, SWT.NONE);
		lblExpiryDate = new Label(canvasLabel, SWT.LEFT);
		txtExpiryDate = new Text(canvasLabel, SWT.RIGHT);

		lblDrugDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblDrugDate.setBounds(new Rectangle(150, 180, 85, 12));
		lblDrugDate.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		lblDrugDate.setText("Dispensed On:");

		txtDrugDate.setBounds(new Rectangle(238, 180, 75, 12));
		txtDrugDate.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtDrugDate.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event1) {
				if ("".equalsIgnoreCase(txtDrugDate.getText())) {
					lblDrugDate.setVisible(false);
				} else {
					lblDrugDate.setVisible(true);
				}
			}
		});

		lblNextApp.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblNextApp.setText("Next Appointment:");
		lblNextApp.setBounds(new Rectangle(140, 195, 105, 12));
		lblNextApp.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 28);

		txtNextApp.setText(new SimpleDateFormat("dd MMM yy").format(cal
				.getTime()));
		txtNextApp.setBounds(new Rectangle(238, 195, 75, 12));
		txtNextApp.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtNextApp.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNextApp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event1) {
				if ("".equalsIgnoreCase(txtNextApp.getText())) {
					lblNextApp.setVisible(false);
				} else {
					lblNextApp.setVisible(true);
				}
			}
		});

		txtPatientName.setBounds(new Rectangle(3, 160, 130, 15));
		txtPatientId.setBounds(new Rectangle(3, 175, 130, 15));

		Date today = new Date();
		txtDrugDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtDrugDate.setText(new SimpleDateFormat("dd MMM yy").format(today));

		// Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 28);

		txtNextApp.setText(new SimpleDateFormat("dd MMM yy").format(cal
				.getTime()));
		txtNextApp.setBounds(new Rectangle(228, 195, 85, 12));
		txtNextApp.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));
		txtNextApp.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtDrugNotes = new Text(canvasLabel, SWT.CENTER);
		txtDrugNotes.setBounds(new Rectangle(0, 210, 320, 15));
		txtDrugNotes.setText("Notes");
		txtDrugNotes.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		lblBatchNo.setBounds(new Rectangle(173, 150, 50, 12));
		lblBatchNo.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		lblBatchNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtBatchNo.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtBatchNo.setBounds(new Rectangle(238, 150, 75, 12));
		txtBatchNo.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		lblExpiryDate.setBackground(ResourceUtils.getColor(iDartColor.WHITE));

		lblExpiryDate.setBounds(new Rectangle(155, 165, 67, 12));
		lblExpiryDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		txtExpiryDate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtExpiryDate.setBounds(new Rectangle(238, 165, 75, 12));
		txtExpiryDate.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND));

		if (iDartProperties.labelType == LabelType.EKAPA) {
			txtBatchNo.setText("01234");
			lblExpiryDate.setText("Expiry date:");
			lblBatchNo.setText("Batch:");
			txtExpiryDate.setText(new SimpleDateFormat("MM/yyy")
			.format(new Date()));

		}

		else {
			txtBatchNo.setText("");
			lblExpiryDate.setText("");
			lblBatchNo.setText("");
			txtExpiryDate.setText("");
			txtExpiryDate.setBackground(ResourceUtils
					.getColor(iDartColor.WHITE));
			txtBatchNo.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
			txtExpiryDate.setEnabled(false);
			txtBatchNo.setEnabled(false);
		}
	}

	/**
	 * This method is called when the user pressed the "Clear" button It clears
	 * the 2 input fields, enables the search button, and clears the information
	 * in the label preview
	 */
	private void cmbClearSelected() {

		if (cmbLabelType.getText().equals("Drug Label")) {
			clearDrugLabel();
		} else if (cmbLabelType.getText().equals("Package Label")) {
			clearPackageLabel();
		} else {
			clearBlankLabel();
		}

	}

	/**
	 * Method showPackageLabel.
	 * 
	 * @param toShow
	 *            boolean
	 */
	private void showPackageLabel(boolean toShow) {
		// Clears the package specific text fields and labels
		txtPackPatientId.setVisible(toShow);
		lblPackBorder1.setVisible(toShow);
		txtPackDate.setVisible(toShow);
		lblPackBorder2.setVisible(toShow);
		txtDateExpected.setVisible(toShow);
		txtPackClinic.setVisible(toShow);
		lblBorderVertical.setVisible(toShow);
		txtPackBarcode.setVisible(toShow);
		txtPackHeader.setVisible(toShow);
	}

	/**
	 * Method showDrugLabel.
	 * 
	 * @param toShow
	 *            boolean
	 */
	private void showDrugLabel(boolean toShow) {
		// Clear the drug specific text fields and labels
		txtDrugName.setVisible(toShow);
		txtSpecialInstructions1.setVisible(toShow);
		txtSpecialInstructions2.setVisible(toShow);
		txtTakeLanguage1.setVisible(toShow);
		// txtTakeLanguage2.setVisible(toShow);
		// txtTakeLanguage3.setVisible(toShow);
		txtAmountPerTime.setVisible(toShow);
		txtFormLanguage1.setVisible(toShow);
		// txtFormLanguage2.setVisible(toShow);
		// txtFormLanguage3.setVisible(toShow);
		txtNumOfRepetitions.setVisible(toShow);
		txtTimesPerDayLanguage1.setVisible(toShow);
		// txtTimesPerDayLanguage2.setVisible(toShow);
		// txtTimesPerDayLanguage3.setVisible(toShow);
		txtPatientId.setVisible(toShow);
		txtPatientName.setVisible(toShow);
		txtDrugNotes.setVisible(toShow);
		txtDrugDate.setVisible(toShow);
		// txtBatchDetails.setVisible(toShow);
		txtIssuesString.setVisible(toShow);
		txtNextApp.setVisible(toShow);
		lblDrugDate.setVisible(toShow);
		lblNextApp.setVisible(toShow);
		lblBatchNo.setVisible(toShow);
		txtBatchNo.setVisible(toShow);
		lblExpiryDate.setVisible(toShow);
		txtExpiryDate.setVisible(toShow);

		if (toShow) {
			txtNextApp.setText(new SimpleDateFormat("dd MMM yy")
			.format(new Date()));
			txtDrugDate.setText(new SimpleDateFormat("dd MMM yy")
			.format(new Date()));
		}

	}

	/**
	 * Method showBlankLabel.
	 * 
	 * @param toShow
	 *            boolean
	 */
	private void showBlankLabel(boolean toShow) {

		for (int i = 0; i < 6; i++) {
			txtBlankLines[i].setVisible(toShow);
		}
	}

	/**
	 * This method is called when the user pressed the "Close" button It
	 * disposes the current shell.
	 */
	private void cmbCloseSelected() {
		closeShell(true);
	}

	/**
	 * This method is called when the user pressed the "Print" button It checks
	 * that a patient ID has been entered, and also that a number of prints has
	 * been chosen. If it succeeds these checks, it creates a label for this
	 * patient, and prints it n number of times
	 */
	private void cmbPrintLabelSelected() {
		// Error checking
		if (txtNumToPrint.getText().equals("")) {
			MessageBox numToPrintMissing = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			numToPrintMissing.setText("Information Missing");
			numToPrintMissing
			.setMessage("You haven't entered how many labels you would like printed. Please enter this information.");
			numToPrintMissing.open();
			txtNumToPrint.setFocus();
		}

		else if (cmbLabelType.getText().equals("")) {
			MessageBox noLabelTypeSelected = new MessageBox(getShell(), SWT.OK
					| SWT.ICON_INFORMATION);
			noLabelTypeSelected.setText("No Label Type Selected");
			noLabelTypeSelected
			.setMessage("You haven't entered the type of label you'd like to print. Please enter this information.");
			noLabelTypeSelected.open();
			cmbLabelType.setFocus();
		} else {

			// Error checking
			if (txtNumToPrint.getText().equals("")) {
				MessageBox numToPrintMissing = new MessageBox(getShell(),
						SWT.OK | SWT.ICON_INFORMATION);
				numToPrintMissing.setText("Information Missing");
				numToPrintMissing
				.setMessage("You haven't entered how many labels you would like printed. Please enter this information.");
				numToPrintMissing.open();
				txtNumToPrint.setFocus();
			}

			// else, we create a label for this patient, and print it n number
			// of times

			// Label is a Drug Label
			if (cmbLabelType.getText().equals("Drug Label")) {
				Object myLabel;

				DrugLabel pdl = new DrugLabel();
				pdl.setPharmHeaderName(LocalObjects.pharmacy.getPharmacyName());
				pdl.setPharmHeaderPharmacist(LocalObjects.pharmacy
						.getPharmacist());
				pdl.setPharmHeaderLocation(LocalObjects.pharmacy.getStreet()
						+ ", " + LocalObjects.pharmacy.getCity() + ", Tel: "
						+ LocalObjects.pharmacy.getContactNo());
				pdl.setDrug(txtDrugName.getText());
				pdl.setDispInstructions1(txtSpecialInstructions1.getText());
				pdl.setDispTakeLang1(txtTakeLanguage1.getText());
				pdl.setDispTakeLang2("");
				pdl.setDispTakeLang3("");
				pdl.setDispFormLang1(txtFormLanguage1.getText());
				pdl.setDispFormLang2("");
				pdl.setDispFormLang3("");
				pdl.setDispTimesPerDayLang1(txtTimesPerDayLanguage1.getText());
				pdl.setDispTimesPerDayLang2("");
				pdl.setDispTimesPerDayLang3("");
				pdl.setDispTabletNum(txtAmountPerTime.getText());
				pdl.setDispTimesPerDay(txtNumOfRepetitions.getText());
				pdl.setPatientLastName(txtPatientName.getText());
				pdl.setPatientFirstName("");
				pdl.setPatientId(txtPatientId.getText());
				pdl.setPackageExpiryDate(txtExpiryDate.getText());
				pdl.setPackagePackagedDate(txtDrugDate.getText());
				pdl.setNextAppointmentDate(txtNextApp.getText());
				pdl.setDispInstructions2(txtSpecialInstructions2.getText());
				pdl.setIssuesString((txtIssuesString.getText()));
				pdl.setBoldIssuesString(false);
				pdl.setBatchNumber(txtBatchNo.getText());
				pdl.setClinicNotes(txtDrugNotes.getText());
				pdl.init();
				myLabel = pdl;

				try {

					ArrayList<Object> labelList = new ArrayList<Object>(Integer
							.parseInt(txtNumToPrint.getText()));

					for (int i = 0; i < Integer.parseInt(txtNumToPrint
							.getText()); i++) {

						labelList.add(myLabel);

					}

					try {
						PrintLabel.printiDARTLabels(labelList);
					} catch (Exception e) {
						getLog().error("Error printing emergency label", e);
					}

				} catch (NumberFormatException n) {
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					m.setText("Error: Number of Labels");
					m
					.setMessage("Please enter a number in the Labels to Print field.");
					m.open();
				}
			}

			// Label is a Package Cover Label
			else if (cmbLabelType.getText().equals("Package Label")) {

				Object myLabel;
				/*
				 * if (iDartProperties.labelType.equals(LabelType.EKAPA)) {
				 * myLabel = new EkapaLabelPackageCover(lblName.getText(),
				 * lblPharmacist.getText(), lblStockCenterAddress .getText(),
				 * txtPackBarcode.getText(), txtPackClinic.getText(),
				 * txtPackDate.getText(), txtPackHeader.getText(),
				 * txtPackPatientId.getText()); } else {
				 */

				myLabel = new PackageCoverLabel(txtPackBarcode.getText(),
						txtPackClinic.getText(), txtPackDate.getText(), lblName
						.getText(), lblPharmacist.getText(),
						lblStockCenterAddress.getText(), txtPackHeader
						.getText(), txtPackPatientId.getText(), "",
						txtDateExpected.getText());
				// }

				try {
					ArrayList<Object> labelList = new ArrayList<Object>(Integer
							.parseInt(txtNumToPrint.getText()));

					for (int i = 0; i < Integer.parseInt(txtNumToPrint
							.getText()); i++) {

						labelList.add(myLabel);

					}
					try {
						PrintLabel.printiDARTLabels(labelList);
					} catch (Exception e) {
						getLog().error("Error printing emergency label", e);
					}

				} catch (NumberFormatException n) {
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					m.setText("Error: Number of Labels");
					m
					.setMessage("Please enter a number in the Labels to Print field.");
					m.open();
				}
			}

			// Label is a blank label
			else if (cmbLabelType.getText().equals("Blank Label")) {

				Object myLabel;

				myLabel = new BlankLabel(lblName.getText(), lblPharmacist
						.getText(), lblStockCenterAddress.getText(),
						txtBlankLines[0].getText(), txtBlankLines[1].getText(),
						txtBlankLines[2].getText(), txtBlankLines[3].getText(),
						txtBlankLines[4].getText(), txtBlankLines[5].getText());

				try {

					ArrayList<Object> labelList = new ArrayList<Object>(Integer
							.parseInt(txtNumToPrint.getText()));

					for (int i = 0; i < Integer.parseInt(txtNumToPrint
							.getText()); i++) {

						labelList.add(myLabel);

					}
					PrintLabel.printiDARTLabels(labelList);
				} catch (NumberFormatException n) {
					MessageBox m = new MessageBox(getShell(), SWT.OK
							| SWT.ICON_INFORMATION);
					m.setText("Error: Number of Labels");
					m
					.setMessage("Please enter a number in the Labels to Print field.");
					m.open();
				} catch (Exception e) {
					getLog().error("Error printing emergency label", e);
				}
			}
		}
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	public void resetDrugLabelFields() {
		// Drug Information
		txtDrugName.setText("Drug Name");
		txtDrugName.setFocus();

		txtSpecialInstructions1.setText("Special Instructions (line 1)");

		txtSpecialInstructions2.setText("Special Instructions (line 2)");

		// Dosage Information
		txtTakeLanguage1.setText("Take");

		txtAmountPerTime.setText("10.5");

		txtFormLanguage1.setText("capsules");

		txtNumOfRepetitions.setText("3");

		txtTimesPerDayLanguage1.setText("times per day");

		// Patient Information
		txtPatientName.setText("Patient Name");

		txtPatientId.setText(Messages.getString("patient.label.patientid")); //$NON-NLS-1$

		txtIssuesString.setText("1 of 6 months");

		lblDrugDate.setText("Dispensed On:");

		lblNextApp.setText("Next Appointment:");

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 28);

		txtNextApp.setText(new SimpleDateFormat("dd MMM yy").format(cal
				.getTime()));

		Date today = new Date();
		txtDrugDate.setText(new SimpleDateFormat("dd MMM yy").format(today));

		// Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 28);

		txtNextApp.setText(new SimpleDateFormat("dd MMM yy").format(cal
				.getTime()));

		txtDrugNotes.setText("Notes");

		if (iDartProperties.labelType == LabelType.EKAPA) {
			txtBatchNo.setText("01234");
		}

		else {
			txtBatchNo.setText("");
			lblExpiryDate.setText("");
			lblBatchNo.setText("");
			txtExpiryDate.setText("");
		}
	}

	public void resetBlankLabelFields() {

		for (int i = 0; i < 6; i++) {
			txtBlankLines[i].setText("Line " + (i + 1));
		}
	}

	public void resetPackageLabelFields() {

		// Patient Id
		txtPackPatientId.setText("PS2-3502-1");

		txtPackHeader.setText("Issue 1 of 6 month script");
		txtPackDate.setBounds(new Rectangle(161, 127, 158, 22));

		Date today = new Date();
		txtPackDate.setText("Packed "
				+ new SimpleDateFormat("dd MMM yy").format(today));

		txtDateExpected.setText("Patient Expected "
				+ new SimpleDateFormat("dd MMM yy").format(today));

		txtPackClinic.setText("Clinic");

		txtPackBarcode.setText("Package ID");
	}

	public void clearBlankLabel() {
		for (int i = 0; i < 6; i++) {
			txtBlankLines[i].setText("");
		}
	}

	public void clearDrugLabel() {
		// Drug Information
		txtDrugName.setText("");
		txtDrugName.setFocus();

		txtSpecialInstructions1.setText("");

		txtSpecialInstructions2.setText("");

		// Dosage Information
		txtTakeLanguage1.setText("");

		txtAmountPerTime.setText("");

		txtFormLanguage1.setText("");

		txtNumOfRepetitions.setText("");

		txtTimesPerDayLanguage1.setText("");

		// Patient Information
		txtPatientName.setText("");

		txtPatientId.setText("");

		txtIssuesString.setText("");

		lblDrugDate.setText("");

		lblNextApp.setText("");

		txtNextApp.setText("");

		txtDrugDate.setText("");

		txtNextApp.setText("");

		txtDrugNotes.setText("");

		if (iDartProperties.labelType == LabelType.EKAPA) {
			txtBatchNo.setText("");
		}

		else {
			txtBatchNo.setText("");
			lblExpiryDate.setText("");
			lblBatchNo.setText("");
			txtExpiryDate.setText("");
		}
	}

	public void clearPackageLabel() {

		// Patient Id
		txtPackPatientId.setText("");

		txtPackHeader.setText("");
		txtPackDate.setText("");
		txtDateExpected.setText("");
		txtPackClinic.setText("");
		txtPackBarcode.setText("");
	}
}
