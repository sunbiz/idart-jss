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

package org.celllife.idart.print.label;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import org.celllife.idart.commonobjects.iDartProperties;

/**
 */
public class DrugLabel implements Printable, DefaultLabel {

	final int BORDER_X = 5;

	final int BORDER_Y = 3;

	// Used for spacing between border and text
	final int LABEL_BUFFER = 2;

	// Used for space to display the number of pills and
	// the number of times
	final int AMOUNT_BUFFER = 25;

	// Header
	private String pharmHeaderName;

	private String pharmHeaderPharmacist;

	private String pharmHeaderLocation;

	// Trade drug info
	private String drug;
	private String dispInstructions1;
	private String dispInstructions2;

	// Dispensing info
	private String dispTakeLang1;

	private String dispTakeLang2;

	private String dispTakeLang3;

	private String dispFormLang1;

	private String dispFormLang2;

	private String dispFormLang3;

	private String dispTimesPerDayLang1;

	private String dispTimesPerDayLang2;

	private String dispTimesPerDayLang3;

	private String dispTabletNum;

	private String dispTimesPerDay;

	// Patient info
	private String patientName;

	private String patientId;

	private String patientFirstName;

	private String patientLastName;

	// Package info
	private String packageExpiryDate;

	private String packagePackagedDate;

	private String nextAppointmentDate;

	private String issuesString;

	private boolean boldIssuesString = false;

	private String batchNumber;

	private String clinicNotes;

	SimpleDateFormat sdfLong = new SimpleDateFormat("dd MMM yyyy");

	SimpleDateFormat sdfShort = new SimpleDateFormat("dd MMM yy");

	public DrugLabel() {
		super();
	}

	public void init() {

		// Check to see if Patient name should appear on Label
		if (!iDartProperties.patientNameOnDrugLabel) {
			this.patientName = "";
			this.patientFirstName = "";
			this.patientLastName = "";
		}

		try {
			if (nextAppointmentDate != null
					&& !nextAppointmentDate.isEmpty()) {
				nextAppointmentDate = sdfShort.format(sdfLong
						.parse(nextAppointmentDate));
			}
			// http://dev.cell-life.org/jira/browse/IDART-58
			// else {
			// nextAppointmentDate = sdfShort.format(new Date());
			// }

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Check to see if next Appointment date should appear on Label
		if (!nextAppointmentDate.equals("")) {
			this.nextAppointmentDate = "Next appointment:"
					+ nextAppointmentDate;
		}

		// Check to see if batch number and expiry date
		// should appear on the label
		if (iDartProperties.showBatchInfoOnDrugLabels) {
			this.batchNumber = "Batch:" + batchNumber;
		} else {
			this.batchNumber = "";
			this.packageExpiryDate = "";
		}

	}

	/**
	 * Method print.
	 * 
	 * @param g
	 *            Graphics
	 * @param format
	 *            PageFormat
	 * @param pageIndex
	 *            int
	 * @return int
	 * @see java.awt.print.Printable#print(Graphics, PageFormat, int)
	 */
	@Override
	public int print(Graphics g, PageFormat format, int pageIndex) {
		// set up the graphics
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(format.getImageableX(), format.getImageableY());
		g2d.setPaint(Color.black);

		// crate the border around the label
		int x = BORDER_X;
		int y = BORDER_Y;
		int w = (int) format.getImageableWidth() - (2 * BORDER_X);
		int h = (int) format.getImageableHeight() - (2 * BORDER_Y);
		g2d.drawRect(x, y, w, h - 4);

		// Heading Information
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 10));
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(pharmHeaderName, PrintLayoutUtils.center(fm,
				pharmHeaderName, (w + 2 * BORDER_X)), 11);

		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		fm = g2d.getFontMetrics();
		g2d.drawString(pharmHeaderPharmacist, PrintLayoutUtils.center(fm,
				pharmHeaderPharmacist, (w + 2 * BORDER_X)), 22);
		g2d.drawString(pharmHeaderLocation, PrintLayoutUtils.center(fm,
				pharmHeaderLocation, (w + 2 * BORDER_X)), 32);
		// Underline header
		g2d.drawRect(x, y, w, 30);

		// Chemical drugs issued
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 11));
		fm = g2d.getFontMetrics();
		if (drug != null) {
			g2d.drawString(drug, PrintLayoutUtils.center(fm, drug,
					(w + 2 * BORDER_X)), 43);
		}

		// Special Instructions
		g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 8));
		fm = g2d.getFontMetrics();
		g2d.drawString(dispInstructions1, PrintLayoutUtils.center(fm,
				dispInstructions1, (w + 2 * BORDER_X)), 52);
		g2d.drawString(dispInstructions2, PrintLayoutUtils.center(fm,
				dispInstructions2, (w + 2 * BORDER_X)), 60);

		// Main dispensing instructions
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 11));
		fm = g2d.getFontMetrics();
		String[] dispTakeStrings = {
				dispTakeLang1 == null ? "" : dispTakeLang1.trim(),
				dispTakeLang2 == null ? "" : dispTakeLang2.trim(),
				dispTakeLang3 == null ? "" : dispTakeLang3.trim() };
		int maxLength = PrintLayoutUtils.getLongestStringWidth(fm,
				dispTakeStrings);
		int totalLength = maxLength;
		String[] dispFormStrings = { dispFormLang1, dispFormLang2,
				dispFormLang3 };
		maxLength = PrintLayoutUtils.getLongestStringWidth(fm, dispFormStrings);
		totalLength += maxLength + AMOUNT_BUFFER;
		String[] dispTimesPerDayStrings = { dispTimesPerDayLang1,
				dispTimesPerDayLang2, dispTimesPerDayLang3 };
		maxLength = PrintLayoutUtils.getLongestStringWidth(fm,
				dispTimesPerDayStrings);
		totalLength += maxLength + AMOUNT_BUFFER;
		int printX = ((w + 2 * BORDER_X) - totalLength) / 2;
		g2d.drawString(dispTakeLang1 == null ? "" : dispTakeLang1, printX - 7,
				84);
		g2d.drawString((dispTakeLang2 == null
				|| "".equalsIgnoreCase(dispTakeLang2.trim()) || "''"
				.equalsIgnoreCase(dispTakeLang2.trim())) ? "" : dispTakeLang2,
				printX - 7, 97);
		g2d.drawString((dispTakeLang3 == null
				|| "".equalsIgnoreCase(dispTakeLang3.trim()) || "''"
				.equalsIgnoreCase(dispTakeLang3.trim())) ? "" : dispTakeLang3,
				printX - 7, 71);
		maxLength = PrintLayoutUtils.getLongestStringWidth(fm, dispTakeStrings);
		printX = printX + AMOUNT_BUFFER + maxLength + 2 * LABEL_BUFFER;
		// Set the position for the first value on label
		int bigNum1 = printX - AMOUNT_BUFFER;
		g2d.drawString(dispFormLang1 == null ? "" : dispFormLang1, printX, 84);
		g2d.drawString((dispFormLang2 == null
				|| "".equalsIgnoreCase(dispFormLang2.trim()) || "''"
				.equalsIgnoreCase(dispFormLang2.trim())) ? "" : dispFormLang2,
				printX, 97);
		g2d.drawString((dispFormLang3 == null
				|| "".equalsIgnoreCase(dispFormLang3.trim()) || "''"
				.equalsIgnoreCase(dispFormLang3.trim())) ? "" : dispFormLang3,
				printX, 71);
		maxLength = PrintLayoutUtils.getLongestStringWidth(fm, dispFormStrings);
		printX = printX + AMOUNT_BUFFER + maxLength + 2 * LABEL_BUFFER;
		// Set the position for the first value on label
		int bigNum2 = printX - AMOUNT_BUFFER;
		g2d.drawString(
				dispTimesPerDayLang1 == null ? "" : dispTimesPerDayLang1,
				printX, 84);
		g2d.drawString(((dispFormLang2 == null
				|| "".equalsIgnoreCase(dispFormLang2.trim()) || "''"
				.equalsIgnoreCase(dispFormLang2.trim()))) ? ""
				: dispTimesPerDayLang2, printX, 97);
		g2d.drawString((dispFormLang3 == null
				|| "".equalsIgnoreCase(dispFormLang3.trim()) || "''"
				.equalsIgnoreCase(dispFormLang3.trim())) ? ""
				: dispTimesPerDayLang3, printX, 71);

		// The big numbers
		// choose font for display amount of drug per time - if the amount is
		// half the font should be smaller
		if (dispTabletNum.length() > 1) {
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 16));
			fm = g2d.getFontMetrics();
			if (dispTabletNum.equals("Half")) {
				g2d.drawString(dispTabletNum, bigNum1 - 9, 86);
			} else {
				g2d.drawString(dispTabletNum, bigNum1 - 8, 86);
			}
			g2d.drawString(dispTimesPerDay, bigNum2, 86);
		} else {
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 30));
			fm = g2d.getFontMetrics();
			g2d.drawString(dispTabletNum, bigNum1 - 5, 91);
			g2d.drawString(dispTimesPerDay, bigNum2, 91);
		}

		// Bottom right package details
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		fm = g2d.getFontMetrics();

		// combine batch number and expiry date onto one line
		String batchAndExpString = batchNumber + "  " + packageExpiryDate;

		// figure out how much space is needed for the information in the bottom
		// right of the label
		String[] bottomRightStrings = { batchAndExpString, packagePackagedDate,
				nextAppointmentDate.substring(0, nextAppointmentDate.length()) };
		maxLength = PrintLayoutUtils.getLongestStringWidth(fm,
				bottomRightStrings);
		int rightMax = maxLength;
		int boxX = w - maxLength - BORDER_X;
		int boxY = h - (8 * 3);

		// draw information onto label
		g2d.drawString(batchAndExpString, PrintLayoutUtils.alignRight(fm,
				batchAndExpString, w + BORDER_X - 2) - 3, boxY + 3);
		g2d.drawString(packagePackagedDate, PrintLayoutUtils.alignRight(fm,
				packagePackagedDate, w + BORDER_X - 2) - 3, boxY + 10);
		g2d.drawString(nextAppointmentDate, PrintLayoutUtils.alignRight(fm,
				nextAppointmentDate, w + BORDER_X - 2) - 3, boxY + 17);

		boolean noInfoInrightCorner = false;
		if (!"".equals(batchAndExpString.trim())
				|| !"".equals(packagePackagedDate)
				|| !"".equals(nextAppointmentDate)) {
			g2d.drawRect(boxX - 2, boxY - 5, maxLength + 2 * BORDER_X + 2, h
					- boxY + BORDER_Y + 1);

		} else {
			noInfoInrightCorner = true;
		}
		// Bottom left patient details

		if (boldIssuesString) {
			issuesString += "**";
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 8));
		}

		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 10));
		fm = g2d.getFontMetrics();
		// figure out how much space is needed for the information in the bottom
		// left of the label
		String[] bottomLeftStrings = { patientName, patientId, issuesString };
		maxLength = PrintLayoutUtils.getLongestStringWidth(fm,
				bottomLeftStrings);

		// compress if first name is too long
		if (maxLength > (w - rightMax - (2 * BORDER_X))) {
			if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
				patientName = PrintLayoutUtils.buildWindowsCompressedLabelName(
						w - rightMax, fm, getPatientFirstName(),
						getPatientLastName());

			} else {
				patientName = PrintLayoutUtils
						.buildEPL2CompressedName(w - rightMax,
								getPatientFirstName(), getPatientLastName());
			}
		} else {
			patientName = patientFirstName + " " + patientLastName;
		}

		patientName = patientName.trim();

		g2d.drawString(patientName, x + 5, boxY + 3);
		g2d.drawString(patientId, x + 5, boxY + 13);
		g2d.drawString(issuesString, x + 5, boxY + 21);

		boolean noInfoInLeftCorner = false;
		if (!"".equals(patientName.trim()) || !"".equals(patientId)
				|| !"".equals(issuesString)) {

			g2d.drawRect(x, boxY - 5, w, h - boxY + BORDER_Y + 1);

		} else {
			noInfoInLeftCorner = true;
		}

		if (noInfoInLeftCorner && noInfoInrightCorner) {
			g2d.drawRect(x, boxY - 5, w, h - boxY + BORDER_Y + 1);

		}
		// Study Notes - FOR CIPRA!!!
		if (clinicNotes != null) {
			g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 6));
			fm = g2d.getFontMetrics();
			g2d.drawString(clinicNotes, PrintLayoutUtils.center(fm,
					clinicNotes, w), 135);
		}

		return Printable.PAGE_EXISTS;
	}

	/**
	 * Method getEPL2Commands.
	 * 
	 * @return Vector<String>
	 * @see org.celllife.idart.print.label.DefaultLabel#getEPL2Commands()
	 */
	@Override
	public Vector<String> getEPL2Commands() {

		Vector<String> commands = new Vector<String>();
		// set the label length and width
		commands.add("Q400,25\n");
		commands.add("q600\n");
		commands.add("N\n");
		// draw the boxes
		commands.add("X5,1,2,595,390\n");
		commands.add("X5,1,2,595,110\n");
		// commands.add("X5,320,2,595,390\n");

		// add the header text
		commands.add("A" + PrintLayoutUtils.centerX(1, 1, 1, pharmHeaderName)
				+ ",11,0,1,1,2,N,\"" + pharmHeaderName + "\"\n");
		commands.add("A"
				+ PrintLayoutUtils.centerX(1, 1, 1, pharmHeaderPharmacist)
				+ ",40,0,1,1,2,N,\"" + pharmHeaderPharmacist + "\"\n");

		commands.add("A"
				+ PrintLayoutUtils.centerX(1, 1, 1, pharmHeaderLocation)
				+ ",71,0,1,1,2,N,\"" + pharmHeaderLocation + "\"\n");

		// drug name
		commands.add("A" + PrintLayoutUtils.centerX(3, 1, 2, drug)
				+ ",122,0,3,1,2,N,\"" + drug + "\"\n");

		commands.add("A" + PrintLayoutUtils.centerX(3, 1, 1, dispInstructions1)
				+ ",172,0,3,1,1,N,\"" + dispInstructions1 + "\"\n");
		commands.add("A" + PrintLayoutUtils.centerX(3, 1, 1, dispInstructions2)
				+ ",202,0,3,1,1,N,\"" + dispInstructions2 + "\"\n");

		commands.add("A25,250,0,2,1,2,N,\"" + dispTakeLang1 + "\"\n");

		commands.add("A220,250,0,2,1,2,N,\"" + dispFormLang1 + "\"\n");

		commands.add("A410,250,0,2,1,2,N,\"" + dispTimesPerDayLang1 + "\"\n");

		if (!dispFormLang2.equals("")) {
			commands.add("A220,280,0,2,1,2,N,\"" + dispFormLang2 + "\"\n");
			commands.add("A25,280,0,2,1,2,N,\"" + dispTakeLang2 + "\"\n");
			commands.add("A410,280,0,2,1,2,N,\"" + dispTimesPerDayLang2
					+ "\"\n");
		}

		if (!dispFormLang3.equals("")) {
			commands.add("A220,220,0,2,1,2,N,\"" + dispFormLang3 + "\"\n");
			commands.add("A25,220,0,2,1,2,N,\"" + dispTakeLang3 + "\"\n");
			commands.add("A410,220,0,2,1,2,N,\"" + dispTimesPerDayLang3
					+ "\"\n");
		}

		// choose font for display amount of drug per time - if the amount is
		// half the font should be smaller
		if ((dispTabletNum.length() == 1)
				&& (dispTabletNum.trim().charAt(0) == '0')) {
			commands.add("A320,240,0,3,4,4,N,\"" + dispTimesPerDay + "\"\n");
		} else if ((dispTabletNum.length() == 1)
				&& (dispTimesPerDay.length() == 1)) {
			commands.add("A120,240,0,3,4,4,N,\"" + dispTabletNum + "\"\n");
			commands.add("A360,240,0,3,4,4,N,\"" + dispTimesPerDay + "\"\n");
		} else if ((dispTabletNum.length() > 1)
				&& (dispTimesPerDay.length() == 1)) {

			// if the amount per time is "Half" it needs to be moved to the
			// right to accommodate the form text
			if (dispTabletNum.equals("Half")) {
				commands.add("A100,250,0,3,2,2,N,\"" + dispTabletNum + "\"\n");
			} else {
				commands.add("A115,250,0,3,2,2,N,\"" + dispTabletNum + "\"\n");
			}
			commands.add("A340,240,0,3,4,4,N,\"" + dispTimesPerDay + "\"\n");
		} else {
			commands.add("A115,250,0,3,2,2,N,\"" + dispTabletNum + "\"\n");
			commands.add("A340,250,0,3,2,2,N,\"" + dispTimesPerDay + "\"\n");
		}

		// details at the bottom of the lable: patient id, name and issues on
		// the left
		// dispensed/packaged date and next appointment date on the right, but
		// only print next
		// appointment for printing if one has been passed from the
		// NewPatientPackaging screen

		// add right hand details
		commands.add("A318,345,0,2,1,1,N,\"" + packagePackagedDate + "\"\n");

		// conditional printing of next appointment date - this will also affect
		// the
		// positioning of the right hand side box
		int rightBoxLength;
		if (!nextAppointmentDate.equals("")) {
			commands.add("X255,320,2,595,390\n");
			rightBoxLength = 595 - 255;
			commands
					.add("A268,370,0,2,1,1,N,\"" + nextAppointmentDate + "\"\n");
		} else {
			commands.add("X310,320,2,595,390\n");
			rightBoxLength = 595 - 310;
		}

		// draw left hand box
		int maxLength = 600 - rightBoxLength - 10;
		commands.add("X5,320,2," + maxLength + ",390\n");

		if ((getPatientFirstName() == null) & (getPatientLastName() == null)) {
			patientName = PrintLayoutUtils.buildEPL2CompressedName(
					maxLength - 70, this.getPatientName(), "");
		} else {
			patientName = PrintLayoutUtils.buildEPL2CompressedName(
					maxLength - 70, (getPatientFirstName() == null) ? ""
							: getPatientFirstName(),
					(getPatientLastName() == null) ? "" : getPatientLastName());
		}

		// produce the patient name
		if (!iDartProperties.patientNameOnDrugLabel) {
			this.patientName = "";
		}

		// add details for patient and issuesString
		commands.add("A25,345,0,2,1,1,N,\"" + patientId + "\"\n");
		commands.add("A25,325,0,2,1,1,N,\"" + patientName + "\"\n");
		if (boldIssuesString) {
			commands.add("A25,370,0,3,1,1,N,\"" + issuesString + "**\"\n");
		} else {
			commands.add("A25,370,0,2,1,1,N,\"" + issuesString + "\"\n");
		}

		commands.add("P1\n");
		return commands;
	}

	/**
	 * Method setPackagePackagedDate.
	 * 
	 * @param packagePackagedDate
	 *            Date
	 */
	public void setPackagePackagedDate(String packagePackagedDate) {
		String tmp = "";
		if ("".equalsIgnoreCase(packagePackagedDate)) {
			this.packagePackagedDate = "";
			return;
		}
		tmp = "Dispensed on:";
		this.packagePackagedDate = tmp + packagePackagedDate;
	}

	/**
	 * Method setPackageExpiryDate.
	 * 
	 * @param packageExpiryDate
	 *            Date
	 */
	public void setPackageExpiryDate(String packageExpiryDate) {
		this.packageExpiryDate = "Expiry date:" + packageExpiryDate;
	}

	/**
	 * @return the clinicNotes
	 */
	public String getClinicNotes() {
		return clinicNotes;
	}

	/**
	 * @param clinicNotes
	 *            the clinicNotes to set
	 */
	public void setClinicNotes(String clinicNotes) {
		this.clinicNotes = clinicNotes;
	}

	/**
	 * @return the drug
	 */
	public String getDrug() {
		return drug;
	}

	/**
	 * @param drug
	 *            the drug to set
	 */
	public void setDrug(String drug) {
		this.drug = drug;
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @param patientName
	 *            the patientName to set
	 */
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	/**
	 * @return the issuesString
	 */
	public String getIssuesString() {
		return issuesString;
	}

	/**
	 * @param issuesString
	 *            the issuesString to set
	 */
	public void setIssuesString(String issuesString) {
		this.issuesString = issuesString;
	}

	/**
	 * @return the nextAppointmentDate
	 */
	public String getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	/**
	 * @param nextAppointmentDate
	 *            the nextAppointmentDate to set
	 */
	public void setNextAppointmentDate(String nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	/**
	 * @return the patientFirstName
	 */
	public String getPatientFirstName() {
		return patientFirstName;
	}

	/**
	 * @param patientFirstName
	 *            the patientFirstName to set
	 */
	public void setPatientFirstName(String patientFirstName) {
		this.patientFirstName = patientFirstName;
	}

	/**
	 * @return the patientLastName
	 */
	public String getPatientLastName() {
		return patientLastName;
	}

	/**
	 * @param patientLastName
	 *            the patientLastName to set
	 */
	public void setPatientLastName(String patientLastName) {
		this.patientLastName = patientLastName;
	}

	/**
	 * @return the batchNumber
	 */
	public String getBatchNumber() {
		return batchNumber;
	}

	/**
	 * @param batchNumber
	 *            the batchNumber to set
	 */
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	/**
	 * @return the boldIssuesString
	 */
	public boolean isBoldIssuesString() {
		return boldIssuesString;
	}

	/**
	 * @param boldIssuesString
	 *            the boldIssuesString to set
	 */
	public void setBoldIssuesString(boolean boldIssuesString) {
		this.boldIssuesString = boldIssuesString;
	}

	/**
	 * @return the dispFormLang1
	 */
	public String getDispFormLang1() {
		return dispFormLang1;
	}

	/**
	 * @param dispFormLang1
	 *            the dispFormLang1 to set
	 */
	public void setDispFormLang1(String dispFormLang1) {
		this.dispFormLang1 = dispFormLang1;
	}

	/**
	 * @return the dispFormLang2
	 */
	public String getDispFormLang2() {
		return dispFormLang2;
	}

	/**
	 * @param dispFormLang2
	 *            the dispFormLang2 to set
	 */
	public void setDispFormLang2(String dispFormLang2) {
		this.dispFormLang2 = dispFormLang2;
	}

	/**
	 * @return the dispInstructions1
	 */
	public String getDispInstructions1() {
		return dispInstructions1;
	}

	/**
	 * @param dispInstructions1
	 *            the dispInstructions1 to set
	 */
	public void setDispInstructions1(String dispInstructions1) {
		this.dispInstructions1 = dispInstructions1;
	}

	/**
	 * @return the dispInstructions2
	 */
	public String getDispInstructions2() {
		return dispInstructions2;
	}

	/**
	 * @param dispInstructions2
	 *            the dispInstructions2 to set
	 */
	public void setDispInstructions2(String dispInstructions2) {
		this.dispInstructions2 = dispInstructions2;
	}

	/**
	 * @return the dispTabletNum
	 */
	public String getDispTabletNum() {
		return dispTabletNum;
	}

	/**
	 * @param dispTabletNum
	 *            the dispTabletNum to set
	 */
	public void setDispTabletNum(String dispTabletNum) {
		this.dispTabletNum = dispTabletNum;
	}

	/**
	 * @return the dispTakeLang1
	 */
	public String getDispTakeLang1() {
		return dispTakeLang1;
	}

	/**
	 * @param dispTakeLang1
	 *            the dispTakeLang1 to set
	 */
	public void setDispTakeLang1(String dispTakeLang1) {
		this.dispTakeLang1 = dispTakeLang1;
	}

	/**
	 * @return the dispTakeLang2
	 */
	public String getDispTakeLang2() {
		return dispTakeLang2;
	}

	/**
	 * @param dispTakeLang2
	 *            the dispTakeLang2 to set
	 */
	public void setDispTakeLang2(String dispTakeLang2) {
		this.dispTakeLang2 = dispTakeLang2;
	}

	/**
	 * @return the dispTimesPerDay
	 */
	public String getDispTimesPerDay() {
		return dispTimesPerDay;
	}

	/**
	 * @param dispTimesPerDay
	 *            the dispTimesPerDay to set
	 */
	public void setDispTimesPerDay(String dispTimesPerDay) {
		this.dispTimesPerDay = dispTimesPerDay;
	}

	/**
	 * @return the dispTimesPerDayLang1
	 */
	public String getDispTimesPerDayLang1() {
		return dispTimesPerDayLang1;
	}

	/**
	 * @param dispTimesPerDayLang1
	 *            the dispTimesPerDayLang1 to set
	 */
	public void setDispTimesPerDayLang1(String dispTimesPerDayLang1) {
		this.dispTimesPerDayLang1 = dispTimesPerDayLang1;
	}

	/**
	 * @return the dispTimesPerDayLang2
	 */
	public String getDispTimesPerDayLang2() {
		return dispTimesPerDayLang2;
	}

	/**
	 * @param dispTimesPerDayLang2
	 *            the dispTimesPerDayLang2 to set
	 */
	public void setDispTimesPerDayLang2(String dispTimesPerDayLang2) {
		this.dispTimesPerDayLang2 = dispTimesPerDayLang2;
	}

	/**
	 * @return the packageExpiryDate
	 */
	public String getPackageExpiryDate() {
		return packageExpiryDate;
	}

	/**
	 * @return the packagePackagedDate
	 */
	public String getPackagePackagedDate() {
		return packagePackagedDate;
	}

	/**
	 * @return the patientId
	 */
	public String getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId
	 *            the patientId to set
	 */
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the pharmHeaderLocation
	 */
	public String getPharmHeaderLocation() {
		return pharmHeaderLocation;
	}

	/**
	 * @param pharmHeaderLocation
	 *            the pharmHeaderLocation to set
	 */
	public void setPharmHeaderLocation(String pharmHeaderLocation) {
		this.pharmHeaderLocation = pharmHeaderLocation;
	}

	/**
	 * @return the pharmHeaderName
	 */
	public String getPharmHeaderName() {
		return pharmHeaderName;
	}

	/**
	 * @param pharmHeaderName
	 *            the pharmHeaderName to set
	 */
	public void setPharmHeaderName(String pharmHeaderName) {
		this.pharmHeaderName = pharmHeaderName;
	}

	/**
	 * @return the pharmHeaderPharmacist
	 */
	public String getPharmHeaderPharmacist() {
		return pharmHeaderPharmacist;
	}

	/**
	 * @param pharmHeaderPharmacist
	 *            the pharmHeaderPharmacist to set
	 */
	public void setPharmHeaderPharmacist(String pharmHeaderPharmacist) {
		this.pharmHeaderPharmacist = pharmHeaderPharmacist;
	}

	/**
	 * @return the dispTakeLang3
	 */
	public String getDispTakeLang3() {
		return dispTakeLang3;
	}

	/**
	 * @param dispTakeLang3
	 *            the dispTakeLang3 to set
	 */
	public void setDispTakeLang3(String dispTakeLang3) {
		this.dispTakeLang3 = dispTakeLang3;
	}

	/**
	 * @return the dispFormLang3
	 */
	public String getDispFormLang3() {
		return dispFormLang3;
	}

	/**
	 * @param dispFormLang3
	 *            the dispFormLang3 to set
	 */
	public void setDispFormLang3(String dispFormLang3) {
		this.dispFormLang3 = dispFormLang3;
	}

	/**
	 * @return the dispTimesPerDayLang
	 */
	public String getDispTimesPerDayLang3() {
		return dispTimesPerDayLang3;
	}

	/**
	 * @param dispTimesPerDayLang3
	 *            the dispTimesPerDayLang3 to set
	 */
	public void setDispTimesPerDayLang3(String dispTimesPerDayLang3) {
		this.dispTimesPerDayLang3 = dispTimesPerDayLang3;
	}
}
