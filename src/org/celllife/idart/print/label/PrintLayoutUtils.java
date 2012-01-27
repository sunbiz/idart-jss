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

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

import javax.swing.RepaintManager;

import net.sourceforge.barbecue.BarcodeFactory;

import org.apache.log4j.Logger;

/**
 */
public class PrintLayoutUtils {

	// font width and height is actually number in manual +2 points, for the
	// ResourceUtils.getColor(iDartColor.WHITE)space around chars.AAARGH!

	Logger log = Logger.getLogger(this.getClass());

	private static ZebraFont[] zfonts = new ZebraFont[] {
			new ZebraFont(1, 10, 14), new ZebraFont(2, 12, 18),
			new ZebraFont(3, 14, 22), new ZebraFont(4, 34, 48),
			new ZebraFont(5, 34, 50) };

	/**
	 * Private constructor to prevent instantiation
	 */
	private PrintLayoutUtils() {
	}

	/**
	 * Aligns the text on the center of the row
	 * 
	 * @param fm
	 *            Font Metrics
	 * @param string
	 *            The text that will be printed
	 * @param width
	 *            The width of the imageable area
	 * @return Returns the position to start printing
	 */
	public static int center(FontMetrics fm, String string, int width) {
		if (string != null) {
			int msg_width = fm.stringWidth(string.trim());
			return (width - msg_width) / 2;
		} else
			return 0;
	}

	/**
	 * Aligns the text on the right
	 * 
	 * @param fm
	 *            FontMetrics
	 * @param string
	 *            The string to be printed
	 * @param align
	 *            The right hand border on which to align the text
	 * @return Returns the position where the text should start printing
	 */
	public static int alignRight(FontMetrics fm, String string, int align) {
		int msg_width = fm.stringWidth(string);
		int result = align - msg_width - 2;
		return result;
	}

	/**
	 * Method getLongestStringWidth.
	 * 
	 * @param fm
	 *            FontMetrics
	 * @param theStrings
	 *            String[]
	 * @return int
	 */
	public static int getLongestStringWidth(FontMetrics fm, String[] theStrings) {
		int longestWidth = 0;
		for (int i = 0; i < theStrings.length; i++) {
			if (theStrings[i] != null && !theStrings[i].equalsIgnoreCase("")) {
				if (fm.stringWidth(theStrings[i]) > longestWidth) {
					longestWidth = fm.stringWidth(theStrings[i]);
				}
			}
		}
		return longestWidth;
	}

	/**
	 * Method centerX.
	 * 
	 * @param zebraFont
	 *            int
	 * @param hStretch
	 *            int
	 * @param vStretch
	 *            int
	 * @param string
	 *            String
	 * @return int
	 */
	public static int centerX(int zebraFont, int hStretch, int vStretch,
			String string) {

		int msg_width = zfonts[zebraFont - 1].getWidth() * hStretch
				* string.length();
		int msg_x = (PrintLabel.EPL2_LABEL_WIDTH / 2) - (msg_width / 2);
		if (msg_x < 0) {
			msg_x = 0;
		}
		return msg_x;

	}

	/**
	 * Method alignToRight.
	 * 
	 * @param zebraFont
	 *            int
	 * @param hStretch
	 *            int
	 * @param vStretch
	 *            int
	 * @param string
	 *            String
	 * @return int
	 */
	public static int alignToRight(int zebraFont, int hStretch, int vStretch,
			String string) {
		int msg_width = zfonts[zebraFont - 1].getWidth() * hStretch
				* string.length();
		int result = PrintLabel.EPL2_LABEL_WIDTH - msg_width - 4;
		return result;
	}

	/**
	 * Method centerCode128Barcode.
	 * 
	 * @param moduleSize
	 *            int
	 * @param barcode
	 *            String
	 * @return int
	 */
	public static int centerCode128Barcode(int moduleSize, String barcode) {
		int msg_width = moduleSize * 11 * barcode.length() + 2; // most digits
		// are 11
		// modules, but
		// the check is
		// 13
		int msg_x = (PrintLabel.EPL2_LABEL_WIDTH / 2) - (msg_width / 2);
		return msg_x;
	}

	public static String buildEPL2CompressedName(int allocatedWidth,
			String name, String surname) {
		int charwidth = 10; // This according to the Zebra printer manual.
		return compressedName(allocatedWidth, charwidth, name, surname);
	}

	public static String buildWindowsCompressedLabelName(int allocatedWidth,
			FontMetrics fm, String name, String surname) {
		int charSize = fm.stringWidth("X");
		return compressedName(allocatedWidth, charSize, name, surname);
	}

	public static String compressedName(int allocatedWidth, int charWidth,
			String name, String surname) {
		String compressedName = name + " " + surname;
		int charwidth = charWidth;
		int totalCharWidth = compressedName.length() * charwidth;
		if (totalCharWidth > allocatedWidth) {
			// Checking first names for complete initials
			StringTokenizer stk = new StringTokenizer(name);
			if (stk.countTokens() > 1) {
				// Obtaining all first names if more then one.
				String[] token = new String[stk.countTokens()];
				String initials[] = new String[token.length];
				compressedName = "";
				for (int i = 0; i < token.length; i++) {
					token[i] = stk.nextToken();
					initials[i] = token[i].substring(0, 1);
					compressedName += initials[i]
							+ (i < token.length ? ". " : " ");
				}
				compressedName += surname;
				int newtotCharWidth = compressedName.length() * charwidth;
				if (newtotCharWidth > allocatedWidth) {
					// working with the first initial now
					compressedName = initials[0] + ". " + surname;
					newtotCharWidth = compressedName.length() * charwidth;
					if (newtotCharWidth < allocatedWidth)
						return compressedName;
					else {
						// working with the surname
						newtotCharWidth = surname.length() * charwidth;
						if (newtotCharWidth > allocatedWidth) {
							// truncate the surname
							int charnumber = allocatedWidth / charwidth;
							compressedName = surname.substring(0,
									charnumber - 3) + "...";
							return compressedName;
						} else {
							// using just the surname
							compressedName = surname;
							return compressedName;
						}
					}
				} else
					return compressedName;
			} else {
				if (name.length() > 0) {
					compressedName = name.substring(0, 1) + ". " + surname;
				} else {
					compressedName = surname;
				}
				int totCharWidth = compressedName.length() * charwidth;
				if (totCharWidth < allocatedWidth)
					return compressedName;
				else {
					// working with the surname
					totCharWidth = surname.length() * charwidth;
					if (totCharWidth > allocatedWidth) {
						// truncate the surname
						int charnumber = allocatedWidth / charwidth;
						compressedName = surname.substring(0, charnumber - 3)
								+ "...";
						return compressedName;
					} else {
						// using just the surname
						compressedName = surname;
						return compressedName;
					}
				}
			}
		} else {
			compressedName = name + " " + surname;
		}
		return compressedName;
	}

	/**
	 * Method EPL2_BoxDraw.
	 * 
	 * @param StartX
	 *            int
	 * @param StartY
	 *            int
	 * @param LineThickness
	 *            int
	 * @param EndX
	 *            int
	 * @param EndY
	 *            int
	 * @return String
	 */
	public static String EPL2_BoxDraw(int StartX, int StartY,
			int LineThickness, int EndX, int EndY) {
		return "X" + StartX + "," + StartY + "," + LineThickness + "," + EndX
				+ "," + EndY + "\n";
	}

	public static String EPL2_Ascii(int StartX, int StartY,
			int RotationDegrees, int FontSelection, int HorizontalMultiplier,
			int VerticalMultiplier, char Direction, String Data) {
		// Direction Must be one of 'N' (Normal) or 'R' (black-white inverter)
		return "A" + StartX + "," + StartY + "," + RotationDegrees + ","
				+ FontSelection + "," + HorizontalMultiplier + ","
				+ VerticalMultiplier + "," + Direction + ",\"" + Data + "\"\n";
	}

	public static String EPL2_Barcode(int StartX, int StartY,
			int RotationDegrees, int BarCode, int NarrowBarWidth,
			int WideBarWidth, int BarCodeHeight, char HumanReadableCode,
			String Data) {
		String temp = "B" + StartX + "," + StartY + "," + RotationDegrees + ","
				+ BarCode + "," + NarrowBarWidth + "," + WideBarWidth + ","
				+ WideBarWidth + "," + BarCodeHeight + "," + HumanReadableCode
				+ ",\"" + Data + "\"\n";

		return temp;

	}

	public static String EPL2_PrintLabel() {
		return "P1\n";
	}

	public static String EPL2_PrintLabelSet(int Sets, int NumLabelsPerSet) {
		return "P" + Sets + "," + NumLabelsPerSet + "\n";
	}

	/**
	 * Method EPL2_ClearImageBuffer.
	 * 
	 */
	public static String EPL2_ClearImageBuffer() {
		return "N\n";
	}

	/**
	 * Method EPL2_SetLabelWidth.
	 * 
	 */
	public static String EPL2_SetLabelWidth(int Width_Dots) {
		return "q" + Width_Dots + "\n";
		// commands.add("q600\n");
	}

	/**
	 * Method EPL2_SetFormLength.
	 * 
	 */
	public static String EPL2_SetFormLength(int Length_Dots, int GapLength) {
		return "Q" + Length_Dots + "," + GapLength + "\n";
		// commands.add("Q400,25\n");
	}

	public static void printBarcode(Graphics2D g2d, String barcodeText, int printableWidth, int ypos) {
		try {
			net.sourceforge.barbecue.Barcode b = BarcodeFactory
					.createCode128(barcodeText);
			b.setDrawingText(false);
			b.setBarWidth(1);
			b.setBarHeight(20);
			b.setResolution(70);
			disableDoubleBuffering(b);
			// print barcode in center of printableWidth
			b.draw(g2d, (printableWidth - b.getWidth()) / 2, ypos);
			enableDoubleBuffering(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	public static void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}
}
