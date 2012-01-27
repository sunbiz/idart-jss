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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;

public class ScriptSummaryLabel implements Printable, DefaultLabel {
	
	public static final String KEY = "SUMMARY";
	
	Logger log = Logger.getLogger(this.getClass());

	final int BORDER_X = 5;
	final int BORDER_Y = 3;
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

	private String pharmacyName;
	private String dispDate;
	private String patientName;
	private String patientFirstName;
	private String patientLastName;
	private String folderNumber;
	private String issuesString;
	private String prescriptionId;
	private String nextAppointmentDate;
	private String labelNumber;
	List<String> drugs;
	List<PackageDrugInfo> drugListInfo;
	private boolean boldIssuesString = false; // default set to false 'cos not
	// all methods set it

	public ScriptSummaryLabel() {
	}

	public ScriptSummaryLabel(List<PackageDrugInfo> pdiList) {
		this.drugListInfo = pdiList;
		populateDrugs(pdiList);
	}

	/**
	 * Populate the readable list of drugs for the label
	 * 
	 * @param drs
	 */
	private void populateDrugs(List<PackageDrugInfo> drs) {
		
		drugs = new ArrayList<String>();
		
		for (PackageDrugInfo pdi : drs) {

			this.patientFirstName = pdi.getPatientFirstName(); // in the case
			// that the
			// constructor
			// didn't pass
			// the patient's
			// name in...
			this.patientLastName = pdi.getPatientLastName();

			
			if (iDartProperties.showBatchInfoOnSummaryLabels) {
				drugs.add(pdi.getDrugName()+ " " + pdi
						.getSummaryQtyInHand()
						+ "     " + pdi.getBatchNumber());
			} else {
				drugs.add(pdi.getDrugName()+ " " + pdi.getSummaryQtyInHand());
			}
		}

	}

	@Override
	public int print(Graphics g, PageFormat format, int pageIndex) {
		// set up the graphics
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(format.getImageableX(), format.getImageableY());
		g2d.setPaint(Color.black);

		// crate the border around the label
		int x = (int) format.getImageableX() + BORDER_X;
		int y = (int) format.getImageableY() + BORDER_Y;
		int w = (int) format.getImageableWidth() - (2 * BORDER_X);
		int h = (int) format.getImageableHeight() - (2 * BORDER_Y);
		g2d.drawRect(x, y, w, h);

		// Header Title
		int headerHeight = 15;
		g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 12));
		FontMetrics fm = g2d.getFontMetrics();
		String msg = prescriptionId;
		g2d.drawString(msg, PrintLayoutUtils.center(fm, msg, w), headerHeight);

		// Header Information
		int currentHeight = headerHeight + 15;
		int m = x + 5;

		g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 8));
		String itemDescr[] = { "Next App: " };
		int maxItemDescrWidth = PrintLayoutUtils.getLongestStringWidth(fm,
				itemDescr);

		if (boldIssuesString) {
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 8));
			issuesString += "**";
		}
		g2d.drawString("Drugs : " + issuesString, m,
				currentHeight + 31);
		g2d.drawString(getLabelNumber(), 160, currentHeight + 31);

		// int xPos = (w / 2) - 20;
		int xPos = maxItemDescrWidth + 5;
		int dHeight = headerHeight + 20;
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		g2d.drawString(pharmacyName, m, currentHeight);
		g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 8));
		g2d.drawString("Packed: ",m,currentHeight + 10);
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		g2d.drawString(dispDate, xPos, currentHeight + 10);

		int allocatedWidth = w - maxItemDescrWidth;

		// log.debug("\n\nPatient's full name: " + patientFirstName + "\t" +
		// patientLastName + "\n\n");
		patientName = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth + 100, fm, getPatientFirstName(),
				getPatientLastName());
		// log.debug("\n\nPatients Compressed Name: " + patientName + "\t" +
		// patientName + "\n\n");
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		fm = g2d.getFontMetrics();
		int fnl = fm.stringWidth(getPatientFirstName());
		int lnl = fm.stringWidth(getPatientLastName());
		g2d.drawString(getPatientFirstName(), w-fnl, currentHeight + 10);
		g2d.drawString(getPatientLastName(), w-lnl, currentHeight + 20);
		// log.warn("\n\nPatient's full name: " + patientFirstName + "\t" +
		// patientLastName + "\n\n");


		if (!nextAppointmentDate.equals("")) {
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 8));
			g2d.drawString("Next App: ", m, currentHeight + 20);
			g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
			g2d.drawString(getNextAppointmentDate(), xPos, currentHeight + 20);
		}

		// Border surrounding header information
		g2d.drawRect(x, headerHeight + 4, w, dHeight - 2);

		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 6));
		int thisHeight = currentHeight + 39;
		int thisXOffset = m;

		// for (int i = 0; i < drugs.size(); i++) {
		for (int i = 0; (i < drugs.size() && i < 9); i++) {
			/*
			 * if (i == 4) { thisHeight = 59; thisXOffset = w / 2 + 10; }
			 */

			g2d.drawString(drugs.get(i) + "", thisXOffset, thisHeight);
			thisHeight += 10;

		}
		// }
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
		commands.add("Q400,25\n");
		commands.add("q600\n");
		commands.add("N\n");
		commands.add("X5,1,2,595,390\n");
		commands.add("X5,55,2,595,235\n");
		commands.add("A100,11,0,2,2,2,N,\"" + prescriptionId + "\"\n");
		commands.add("A10,62,0,3,1,1,N,\"Pharmacy:\"\n");
		commands.add("A230,62,0,2,1,1,N,\"" + pharmacyName + "\"\n");
		commands.add("A10,92,0,3,1,1,N,\"Date:\"\n");
		commands.add("A230,92,0,2,1,1,N,\"" + getDispDate() + "\"\n");
		commands.add("A10,122,0,3,1,1,N,\"Patient Name:\"\n");
		patientName = PrintLayoutUtils.buildEPL2CompressedName(300,
				getPatientFirstName(), getPatientLastName());
		commands.add("A230,122,0,2,1,1,N,\"" + patientName + "\"\n");
		commands.add("A10,152,0,3,1,1,N,\"Folder No:\"\n");
		commands.add("A230,152,0,2,1,1,N,\"" + folderNumber + "\"\n");
		commands.add("A10,182,0,3,1,1,N,\"Issue:\"\n");
		commands.add("A230,182,0,2,1,1,N,\"" + issuesString + "\"\n");
		if (!nextAppointmentDate.equals("")) {
			commands.add("A10,212,0,3,1,1,N,\"Next Appointment:\"\n");
			commands
			.add("A260,212,0,3,1,1,N,\"" + getNextAppointmentDate()
					+ "\"\n");
		}
		commands.add("A10,245,0,3,1,1,N,\"Drugs Dispensed:\"\n");

		int thisHeight = 272;
		int thisXOffset = 10;
		for (int i = 0; i < drugs.size(); i++) {
			if (i == 4) {
				thisHeight = 272;
				thisXOffset = 600 / 2 + 10;
			}

			commands.add("A" + thisXOffset + "," + thisHeight + ",0,1,1,1,N,\""
					+ drugs.get(i) + "\"\n");

			thisHeight += 20;
		}

		commands.add("P1\n");

		return commands;
	}

	/**
	 * @return the pharmacyName
	 */
	public String getPharmacyName() {
		return pharmacyName;
	}

	/**
	 * @param pharmacyName
	 *            the pharmacyName to set
	 */
	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	/**
	 * @return the dispDate
	 */
	public String getDispDate() {
		return dispDate;
	}

	/**
	 * @param dispDate
	 *            the dispDate to set
	 */
	public void setDispDate(String dispDate) {
		this.dispDate = dispDate;
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
	 * @return the folderNumber
	 */
	public String getFolderNumber() {
		return folderNumber;
	}

	/**
	 * @param folderNumber
	 *            the folderNumber to set
	 */
	public void setFolderNumber(String folderNumber) {
		this.folderNumber = folderNumber;
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
	 * @return the packageId
	 */
	public String getPrescriptionId() {
		return prescriptionId;
	}

	/**
	 * @param packageId
	 *            the packageId to set
	 */
	public void setPrescriptionId(String packageId) {
		this.prescriptionId = packageId;
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

	public String getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	/**
	 * @return the drugs
	 */
	public List<String> getDrugs() {
		return drugs;
	}

	/**
	 * @param drugs
	 *            the drugs to set
	 */
	public void setDrugs(List<String> drugs) {
		this.drugs = drugs;
	}

	public void setNextAppointmentDate(String nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
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
	 * @return the labelNumber
	 */
	public String getLabelNumber() {
		return labelNumber;
	}

	/**
	 * @param labelNumber
	 *            the labelNumber to set
	 */
	public void setLabelNumber(String labelNumber) {
		this.labelNumber = labelNumber;
	}



}
