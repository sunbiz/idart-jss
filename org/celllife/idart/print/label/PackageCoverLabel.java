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
import java.util.Vector;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.commonobjects.iDartProperties.LabelType;

/**
 */
public class PackageCoverLabel implements Printable, DefaultLabel {
	
	public static final String KEY = "PACKAGECOVER";

	private String header1 = null;
	private String header2 = null;
	private String header3 = null;
	private String patientid = null;
	private String patientName = null;
	private String packageString = null;
	private String date = null;
	private String clinic = null;
	private String barcode = null;
	private String dateExpected = null;

	final int BORDER_X = 5;
	final int BORDER_Y = 3;

	private final LabelType labeltype;


	/**
	 * Constructor for PackageCoverLabel.
	 * @param theBarcode String
	 * @param theClinic String
	 * @param theDate String
	 * @param theHeader1 String
	 * @param theHeader2 String
	 * @param theHeader3 String
	 * @param thePackageString String
	 * @param thePatientid String
	 * @param theDateExpectedString String
	 */
	public PackageCoverLabel(String theBarcode, String theClinic,
			String theDate, String theHeader1, String theHeader2,
			String theHeader3, String thePackageString, String thePatientid,
			String thePatientName, String theDateExpectedString) {

		super();
		barcode = theBarcode;
		clinic = theClinic;
		date = theDate;
		header1 = theHeader1;
		header2 = theHeader2;
		header3 = theHeader3;
		packageString = thePackageString;
		patientid = thePatientid;
		patientName = thePatientName;
		dateExpected = theDateExpectedString;


		labeltype = iDartProperties.labelType;


	}

	/**
	 * Method print.
	 * @param g Graphics
	 * @param pf PageFormat
	 * @param pageIndex int
	 * @return int
	 * @throws PrinterException
	 * @see java.awt.print.Printable#print(Graphics, PageFormat, int)
	 */
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex)
	throws PrinterException {

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		g2d.setColor(Color.black);

		// crate the border around the label
		int x = (int) pf.getImageableX() + BORDER_X;
		int y = (int) pf.getImageableY() + BORDER_Y;
		int w = (int) pf.getImageableWidth() - (2 * BORDER_X);
		int h = (int) pf.getImageableHeight() - (2 * BORDER_Y);


		// Draw outer Border
		g2d.drawRect(x, y, w, h);
		// Header (i.e. Pharmacy Details)
		g2d.drawRect(x, y, w, 31);
		// Clinic and date expected
		g2d.drawRect(x, 65, (w / 2) + 3, 25);
		// Issue String and pack Date
		g2d.drawRect(x + (w / 2) + 3, 65, (w / 2) - 3, 25);

		// Header
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 10));
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(header1, PrintLayoutUtils.center(fm, header1, w) + x, 13);

		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		fm = g2d.getFontMetrics();
		g2d.drawString(header2, PrintLayoutUtils.center(fm, header2, w) + x, 23);
		g2d.drawString(header3, PrintLayoutUtils.center(fm, header3, w) + x, 32);

		// PatientID
		if (!iDartProperties.patientNameOnPackageLabel) {
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 20));
			fm = g2d.getFontMetrics();
			g2d.drawString(patientid, PrintLayoutUtils.center(fm, patientid, w)
					+ x, 57);


		} else {
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 14));
			fm = g2d.getFontMetrics();
			g2d.drawString(patientid, PrintLayoutUtils.center(fm, patientid, w)
					+ x, 50);

			// Draw patient name
			g2d.setFont(new Font("Arial", java.awt.Font.BOLD, 10));
			fm = g2d.getFontMetrics();
			g2d.drawString(patientName, PrintLayoutUtils.center(fm,
					patientName,
					w)
					+ x, 60);
		}




		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 11));
		fm = g2d.getFontMetrics();

		// Clinic Information
		g2d.drawString(clinic, PrintLayoutUtils.center(fm, clinic,
				w / 2) + x,
				75);

		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		fm = g2d.getFontMetrics();
		// Package & Date Information

		if (!dateExpected.equals("")) {
			g2d.drawString(dateExpected, PrintLayoutUtils.center(fm,
					dateExpected, w / 2)
					+ x, 86);
		}

		g2d.drawString(packageString, w	/ 2
				+ PrintLayoutUtils.center(fm, packageString,
						w / 2) + x, 75);

		g2d.drawString(date, w
				/ 2
				+ PrintLayoutUtils.center(fm, date,
						w / 2)
						+ x, 86);

		if (packageString != null && !"".equalsIgnoreCase(packageString)) {
			PrintLayoutUtils.printBarcode(g2d, barcode, w, 100);
			// Barcode
			/*Barcode jb = new Barcode(barcode);
			jb.doPaint(g2d, 6, 122, 12, w);
			g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 10));
			fm = g2d.getFontMetrics();
			g2d.drawString(jb.barcodeString, PrintLayoutUtils.center(fm,
					jb.barcodeString, (int) pf.getImageableWidth())
					+ x, 130);*/
		}
		return Printable.PAGE_EXISTS;

	}

	/**
	 * Method getEPL2Commands.
	 * @return Vector<String>
	 * @see org.celllife.idart.print.label.DefaultLabel#getEPL2Commands()
	 */
	@Override
	public Vector<String> getEPL2Commands() {

		if (labeltype == LabelType.EKAPA ) return new Vector<String>();

		Vector<String> commands = new Vector<String>();
		commands.add("Q400,25\n");
		commands.add("q600\n");
		commands.add("N\n");
		// draw the boxes
		commands.add("X5,1,2,595,380\n");
		commands.add("X5,1,2,595,110\n");
		commands.add("X5,162,2,595,232\n");// dec by 10
		commands.add("X300,162,2,595,232\n");// dec by 10
		// add the header text
		// add the header text
		commands.add("A" + PrintLayoutUtils.centerX(1, 1, 2, header1)
				+ ",11,0,1,1,2,N,\"" + header1 + "\"\n");
		commands.add("A" + PrintLayoutUtils.centerX(1, 1, 2, header2)
				+ ",40,0,1,1,2,N,\"" + header2 + "\"\n");

		commands.add("A" + PrintLayoutUtils.centerX(1, 1, 2, header3)
				+ ",71,0,1,1,2,N,\"" + header3 + "\"\n");
		// rest of text

		if (!iDartProperties.patientNameOnPackageLabel) {

			commands.add("A" + PrintLayoutUtils.centerX(3, 2, 2, patientid)
					+ ",120,0,3,2,2,N,\"" + patientid + "\"\n");
		}

		else {
			commands.add("A" + PrintLayoutUtils.centerX(3, 2, 2, patientid)
					+ ",120,0,3,2,2,N,\"" + patientid + "\"\n");

			commands.add("A" + PrintLayoutUtils.centerX(3, 2, 2, patientid)
					+ ",120,0,3,2,2,N,\"" + patientid + "\"\n");

		}


		commands.add("A20,172,0,1,1,2,N,\"" + clinic + "\"\n"); // clinic
		commands.add("A20,212,0,1,1,2,N,\"" + dateExpected + "\"\n");		//	dateExpected
		commands.add("A315,172,0,1,1,2,N,\"" + packageString + "\"\n");	// packageString
		commands.add("A315,212,0,1,1,2,N,\"" + date + "\"\n");	// date

		// first, set the barcode size

		// add the barcode
		commands.add("B" + PrintLayoutUtils.centerCode128Barcode(2, barcode)
				+ ",252,0,1,2,4,100,N,\"" + barcode + "\"\n");
		commands.add("A" + PrintLayoutUtils.centerX(3, 1, 1, barcode)
				+ ",356,0,3,1,1,N,\"" + barcode + "\"\n");

		// print one label
		commands.add("P1\n");

		return commands;
	}


}
