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
import java.awt.print.PrinterException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.commonobjects.iDartProperties.LabelType;
import org.celllife.idart.database.hibernate.Patient;

/**
 */
public class PatientInfoLabel implements Printable, DefaultLabel {

	Logger log = Logger.getLogger(this.getClass());
	private String id;
	private String surname;
	private String firstname;
	private String dateOfBirth;
	private String sex;

	private String downReferralClinic;

	final int BORDER_X = 5;
	final int BORDER_Y = 3;
	SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

	private LabelType labeltype;

	/**
	 * Constructor for PatientInfoLabel.
	 * 
	 * @param theDateOfBirth
	 *            String
	 * @param theFirstname
	 *            String
	 * @param thePatientId
	 *            String
	 * @param theSex
	 *            String
	 * @param theSurname
	 *            String
	 * 
	 * public PatientInfoLabel(String theDateOfBirth, String theFirstname,
	 * String thePatientId, String theSex, String theSurname) {
	 * 
	 * super(); dateOfBirth = theDateOfBirth; firstname = theFirstname; id =
	 * thePatientId;
	 * 
	 * if (theSex.charAt(0)=='u') sex = "unknown"; else if (theSex.charAt(0)
	 * =='m') sex = "Male"; else sex = "Female";
	 * 
	 * surname = theSurname; labeltype = iDartProperties.labelType; }
	 */
	/**
	 * Constructor from EkapaLabelPatient.
	 * 
	 * @param patient
	 *            Patient
	 */
	public PatientInfoLabel(Patient patient) {
		if (patient == null)
			return;
		firstname = patient.getFirstNames();
		surname = patient.getLastname();
		id = patient.getPatientId();
		dateOfBirth = sdf.format(patient.getDateOfBirth());

		labeltype = iDartProperties.labelType;
		downReferralClinic = patient.getCurrentClinic().getClinicName();

		char theSex = Character.toLowerCase(patient.getSex());
		if (theSex == 'm')
			sex = "Male";
		else if (theSex == 'f')
			sex = "Female";
		else
			sex = "Unknown";

		// ImgPrint();
	}

	/**
	 * Method print.
	 * 
	 * @param g
	 *            Graphics
	 * @param pf
	 *            PageFormat
	 * @param pageIndex
	 *            int
	 * @return int
	 * @throws PrinterException
	 * @see java.awt.print.Printable#print(Graphics, PageFormat, int)
	 */
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex)
			throws PrinterException {

		// set up the graphics
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		g2d.setPaint(Color.black);
		// create the border
		int x = (int) pf.getImageableX() + BORDER_X;
		int y = (int) pf.getImageableY() + BORDER_Y;
		int w = (int) pf.getImageableWidth() - (2 * BORDER_X);
		int h = (int) pf.getImageableHeight() - (2 * BORDER_Y);

		g2d.drawRect(x, y, w, h);

		// Header Title
		int hHeight = 16;
		g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 12));
		FontMetrics fm = g2d.getFontMetrics();
		String msg = "PATIENT DETAILS";
		g2d.drawString(msg, PrintLayoutUtils.center(fm, msg, w), hHeight);

		// Rectangle surrounding the text (i.e. from Folder Number to Clinic)
		g2d.drawRect(x, 18, w, 70);
		int currentHeight = hHeight + 14;

		/**
		 * Note 166 = total space for text on the line 20 = space between label
		 * and info 10 = offset from right margin
		 */
		int xPos = x + (w - 166 - 20 - 10) / 2;
		g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 10));
		fm = g2d.getFontMetrics();
		g2d.drawString("Folder No: ", xPos, currentHeight);
		g2d.drawString("Patient Name: ", xPos, currentHeight + 12);
		g2d.drawString("Date of Birth: ", xPos, currentHeight + 24);
		g2d.drawString("Sex: ", xPos, currentHeight + 36);
		g2d.drawString("Clinic: ", xPos, currentHeight + 48);

		/**
		 * 60 = length of the Labels. ie Patient Id, Clinic, etc 20 = spcae
		 * between label and info
		 */
		xPos = xPos + (60 + 20);
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 10));
		fm = g2d.getFontMetrics();
		String compressedName = PrintLayoutUtils
				.buildWindowsCompressedLabelName(w - 35, fm, firstname, surname);
		g2d.drawString(id, xPos, currentHeight);
		g2d.drawString(compressedName, xPos, currentHeight + 12);
		try {
			Date theDate = sdf.parse(dateOfBirth);
			dateOfBirth = new SimpleDateFormat("dd MMM yyyy").format(theDate);
		} catch (ParseException e) {
			log.error("Error parsing date", e);
		}
		g2d.drawString(dateOfBirth, xPos, currentHeight + 24);
		g2d.drawString(sex, xPos, currentHeight + 36);
		g2d.drawString(downReferralClinic, xPos, currentHeight + 48);

		// Print the barcode at the bottom
		/*Barcode barcode = new Barcode(id);
		barcode.doPaint(g2d, 20, 125, 20, w);*/
		
		PrintLayoutUtils.printBarcode(g2d, id, w, 97);
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

		if (labeltype == LabelType.EKAPA) {
			return new Vector<String>();
		} else {
			Vector<String> commands = new Vector<String>();

			String compressedPatientName = PrintLayoutUtils
					.buildEPL2CompressedName(280, firstname, surname);

			commands.add(PrintLayoutUtils.EPL2_SetFormLength(400, 25));// commands.add("Q400,25\n");
			commands.add(PrintLayoutUtils.EPL2_SetLabelWidth(600));// commands.add("q600\n");
			commands.add(PrintLayoutUtils.EPL2_ClearImageBuffer()); // commands.add("N\n");
			commands.add(PrintLayoutUtils.EPL2_BoxDraw(5, 1, 2, 595, 390)); // commands.add("X5,1,2,595,390\n");
			commands.add(PrintLayoutUtils.EPL2_BoxDraw(5, 55, 2, 595, 265)); // commands.add("X5,55,2,595,265\n");
			commands.add(PrintLayoutUtils.EPL2_Ascii(100, 11, 0, 2, 2, 2, 'N',
					"PATIENT DETAILS")); // commands.add("A100,11,0,2,2,2,N,\"PATIENT
			// DETAILS\"\n");
			commands.add(PrintLayoutUtils.EPL2_Ascii(30, 62, 0, 2, 1, 2, 'N',
					"Folder No:")); // commands.add("A30,62,0,2,1,2,N,\"Folder
			// No:\"\n");
			commands.add(PrintLayoutUtils.EPL2_Ascii(250, 62, 0, 2, 1, 2, 'N',
					id));
			commands.add(PrintLayoutUtils.EPL2_Ascii(30, 102, 0, 2, 1, 2, 'N',
					"Patient Name:"));
			commands.add(PrintLayoutUtils.EPL2_Ascii(250, 102, 0, 2, 1, 2, 'N',
					compressedPatientName));
			try {
				Date theDate = sdf.parse(dateOfBirth);
				dateOfBirth = sdf.format(theDate);
			} catch (ParseException e) {
				log.error("Error parsing date", e);
			}
			commands.add(PrintLayoutUtils.EPL2_Ascii(30, 142, 0, 2, 1, 2, 'N',
					"Date of Birth:"));
			commands.add(PrintLayoutUtils.EPL2_Ascii(250, 142, 0, 2, 1, 2, 'N',
					dateOfBirth));
			commands.add(PrintLayoutUtils.EPL2_Ascii(30, 182, 0, 2, 1, 2, 'N',
					"Sex:"));
			commands.add(PrintLayoutUtils.EPL2_Ascii(250, 182, 0, 2, 1, 2, 'N',
					sex));
			commands.add(PrintLayoutUtils.EPL2_Ascii(30, 222, 0, 2, 1, 2, 'N',
					"Clinic:"));
			commands.add(PrintLayoutUtils.EPL2_Ascii(250, 222, 0, 2, 1, 2, 'N',
					downReferralClinic));
			commands.add("B" + PrintLayoutUtils.centerCode128Barcode(2, id)
					+ ",270,0,1,2,4,100,N," + "\"" + id + "\"\n");
			commands.add(PrintLayoutUtils.EPL2_PrintLabel());

			return commands;

		}
	}

}
