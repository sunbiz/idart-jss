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
public class BlankLabel implements Printable, DefaultLabel {

	private String header1 = null;
	private String header2 = null;
	private String header3 = null;
	private String line1 = null;
	private String line2 = null;
	private String line3 = null;
	private String line4 = null;
	private String line5 = null;
	private String line6 = null;
	private LabelType labeltype;
	final int BORDER_X = 5;
	final int BORDER_Y = 3;

	/**
	 * Constructor for BlankLabel.
	 * 
	 * @param header1
	 *            String
	 * @param header2
	 *            String
	 * @param header3
	 *            String
	 * @param line1
	 *            String
	 * @param line2
	 *            String
	 * @param line3
	 *            String
	 * @param line4
	 *            String
	 * @param line5
	 *            String
	 * @param line6
	 *            String
	 */
	public BlankLabel(String header1, String header2, String header3,
			String line1, String line2, String line3, String line4,
			String line5, String line6) {

		this.header1 = header1;
		this.header2 = header2;
		this.header3 = header3;
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
		this.line4 = line4;
		this.line5 = line5;
		this.line6 = line6;

		labeltype = iDartProperties.labelType;
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

		if (pageIndex != 0)
			return Printable.NO_SUCH_PAGE;

		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		g2d.setPaint(Color.black);

		// crate the border around the label
		int x = (int) pf.getImageableX() + BORDER_X;
		int y = (int) pf.getImageableY() + BORDER_Y;
		int w = (int) pf.getImageableWidth() - (2 * BORDER_X);
		int h = (int) pf.getImageableHeight() - (2 * BORDER_Y);
		g2d.drawRect(x, y, w, h);

		// Heading Information
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 10));
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(header1, PrintLayoutUtils.center(fm, header1, w), 11);

		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 8));
		fm = g2d.getFontMetrics();
		g2d.drawString(header2, PrintLayoutUtils.center(fm, header2, w), 22);
		g2d.drawString(header3, PrintLayoutUtils.center(fm, header3, w), 32);

		// Underline header
		g2d.drawRect(x, y, w, 34);

		// Print each of the blank lines (1-6)
		g2d.setFont(new Font("Arial", java.awt.Font.PLAIN, 12));
		fm = g2d.getFontMetrics();

		// }
		g2d.drawString(line1, 15, 50);
		g2d.drawString(line2, 15, 65);
		g2d.drawString(line3, 15, 80);
		g2d.drawString(line4, 15, 95);
		g2d.drawString(line5, 15, 110);
		g2d.drawString(line6, 15, 125);

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
		if (labeltype == LabelType.EKAPA)
			return new Vector<String>();

		Vector<String> commands = new Vector<String>();
		// set the label length and width
		commands.add("Q390\n");
		commands.add("q600\n");
		commands.add("N\n");
		// draw the boxes
		commands.add("X20,1,2,600,380\n");
		commands.add("X20,1,2,600,110\n");

		commands.add(PrintLayoutUtils.EPL2_Ascii(PrintLayoutUtils.centerX(1, 1,
				1, header1), 11, 0, 1, 1, 2, 'N', header1));
		commands.add(PrintLayoutUtils.EPL2_Ascii(PrintLayoutUtils.centerX(1, 1,
				1, header2), 40, 0, 1, 1, 2, 'N', header2));

		commands.add(PrintLayoutUtils.EPL2_Ascii(PrintLayoutUtils.centerX(1, 1,
				1, header3), 71, 0, 1, 1, 2, 'N', header3));

		commands.add(PrintLayoutUtils.EPL2_Ascii(25, 120, 0, 2, 1, 2, 'N',
				line1));
		commands.add(PrintLayoutUtils.EPL2_Ascii(25, 120 + (45 * 1), 0, 2, 1,
				2, 'N', line2));
		commands.add(PrintLayoutUtils.EPL2_Ascii(25, 120 + (45 * 2), 0, 2, 1,
				2, 'N', line3));
		commands.add(PrintLayoutUtils.EPL2_Ascii(25, 120 + (45 * 3), 0, 2, 1,
				2, 'N', line4));
		commands.add(PrintLayoutUtils.EPL2_Ascii(25, 120 + (45 * 4), 0, 2, 1,
				2, 'N', line5));
		commands.add(PrintLayoutUtils.EPL2_Ascii(25, 120 + (45 * 5), 0, 2, 1,
				2, 'N', line6));

		commands.add("P1\n");
		return commands;
	}

}
