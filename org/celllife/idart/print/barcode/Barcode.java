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

package org.celllife.idart.print.barcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.Vector;

import javax.swing.JComponent;

import org.apache.log4j.Logger;
import org.celllife.idart.print.barcode.CompactCode128.CharacterCode;
import org.celllife.idart.print.label.PrintLayoutUtils;

/*
 * Code128 Barcode representation, without barcode text Uses modes B and C to
 * acheive compression Based on jbarcodebean,
 * http://jbarcodebean.sourceforge.net/
 * 
 */
/**
 */
public class Barcode extends JComponent {


	Logger log = Logger.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -968438223363997017L;

	private final EncodedBarcode encoded;

	private int barcodeHeight = 40;

	private double angleDegrees;

	public String barcodeString;

	public static final int windowsBarcodeLength = 35;

	public static final int otherOSBarcodeLength = 23;

	CharacterCode[] cc;

	/**
	 * Constructor for Barcode.
	 * @param toEncode String
	 */
	public Barcode(String toEncode) {
		barcodeString = toEncode;
		// get the complex code128 string
		cc = getComplexCode128String(toEncode);

		encoded = getEncodedBarcode(cc);
		setForeground(Color.black);
		setFont(new Font("Monospaced", Font.PLAIN, 12));
		setDoubleBuffered(false);

	}

	/*
	 * Get the CharacterCode array representing this string
	 * 
	 */
	/**
	 * Method getComplexCode128String.
	 * @param toBeParsed String
	 * @return CharacterCode[]
	 */
	public CharacterCode[] getComplexCode128String(String toBeParsed) {

		/*
		 * By default, produce the mode B encoding
		 * 
		 */
		Vector<CharacterCode> charCodes = new Vector<CharacterCode>();

		for (int k = 0; k < toBeParsed.length(); k++) {
			String s = new String(new char[] { toBeParsed.charAt(k) });
			charCodes.addElement(CompactCode128.get128BVal(s));
		}

		/*
		 * Check the string to see where we have opportunity to compress by
		 * switching to mode c
		 */

		for (int i = 0; i < charCodes.size() - 1; i++) {
			char c = ((charCodes.elementAt(i)).character.charAt(0));
			if (Character.isDigit(c)) {
				int checkNext = 0;
				for (int j = i + 1; j < charCodes.size(); j++) {
					char nextC = ((charCodes.elementAt(j)).character.charAt(0));
					if (Character.isDigit(nextC)) {
						checkNext++;
					} else {
						break;
					}
				}

				/*
				 * Ok, so there are enough characters to make the transition to
				 * code C worthwile
				 */
				if (checkNext > 3) {
					Integer[] ints = new Integer[2];
					ints[0] = new Integer(i);
					ints[1] = new Integer(i + checkNext);

					charCodes = replaceWithCodeC(charCodes, i, i + checkNext);
					i = i + checkNext;
				}
			}

		}

		/*
		 * Now that we have the array, lets work out what the check character
		 * should be
		 */
		int weighting = 0;
		if (charCodes.size() > 0
				&& (charCodes.elementAt(0)).character
				.equals(CompactCode128.START_C)) {
			weighting += 105;
		} else {
			charCodes.insertElementAt(CompactCode128
					.get128BVal(CompactCode128.START_B), 0);
			weighting += 104;
		}

		for (int i = 1; i < charCodes.size(); i++) {
			CharacterCode c = charCodes.elementAt(i);
			int c128val = c.check;
			weighting += c128val * (i);
			//			System.out.print(c128val + ":" + c128val * (i) + "+");
		}
		int checksum = weighting % 103;

		CharacterCode checkchar;

		checkchar = CompactCode128.get128CValByInt(checksum);
		charCodes.add(checkchar);
		charCodes.add(CompactCode128.get128CVal(CompactCode128.STOP));

		CharacterCode[] charArr = new CharacterCode[charCodes.size()];
		charCodes.toArray(charArr);
		return charArr;
	}

	/**
	 * Method replaceWithCodeC.
	 * @param toChangeVec Vector<CharacterCode>
	 * @param start int
	 * @param stop int
	 * @return Vector<CharacterCode>
	 */
	private static Vector<CharacterCode> replaceWithCodeC(
			Vector<CharacterCode> toChangeVec, int start, int stop) {

		CharacterCode[] toChange = new CharacterCode[toChangeVec.size()];
		toChangeVec.toArray(toChange);

		Vector<CharacterCode> teenyVec = new Vector<CharacterCode>();
		if (start == 0) {
			teenyVec.add(CompactCode128.get128BVal(CompactCode128.START_C));
		}

		else {
			teenyVec.add(CompactCode128.get128BVal(CompactCode128.MODE_C));
		}

		// If it is an odd number, make sure not to include the final digit
		if ((stop - start) % 2 == 0) {
			stop--;
		}

		for (int j = start; j < stop; j = j + 2) {
			CharacterCode newCode = CompactCode128.get128CVal(new String(
					new char[] { toChange[j].character.charAt(0),
							toChange[j + 1].character.charAt(0) }));

			if (newCode != null) {
				teenyVec.addElement(newCode);
			}

		}
		teenyVec.addElement(CompactCode128.get128CVal(CompactCode128.MODE_B));

		Vector<CharacterCode> newVec = new Vector<CharacterCode>();

		for (int p = 0; p < start; p++) {
			newVec.add(toChange[p]);
		}

		for (int r = 0; r < teenyVec.size(); r++) {
			newVec.add(teenyVec.elementAt(r));
		}

		for (int q = stop + 1; q < toChange.length; q++) {
			newVec.add(toChange[q]);
		}

		return newVec;
	}

	/*
	 * Return the barcode as a jbarcodebean.EncodedBarcode This is mostly just
	 * an array of jbarcodebean.BarcodeElements which are the individual
	 * bars/spaces.
	 * 
	 */
	/**
	 * Method getEncodedBarcode.
	 * @param charCode CharacterCode[]
	 * @return EncodedBarcode
	 */
	private EncodedBarcode getEncodedBarcode(CharacterCode[] charCode) {

		int size = (charCode.length * 6) + 3;
		BarcodeElement[] elements = new BarcodeElement[size];

		// Margin
		elements[0] = new BarcodeElement();
		elements[0].bar = false;
		elements[0].width = 2;

		int len = charCode.length;
		int j = 1;
		for (int i = 0; i < len; i++) {

			for (int k = 0; k < charCode[i].widths.length; k++) {
				elements[j] = new BarcodeElement();
				elements[j].width = charCode[i].widths[k];
				elements[j].bar = ((k % 2) == 1) ? false : true;
				j++;
			}

		}

		elements[j] = new BarcodeElement();
		elements[j].bar = false;
		elements[j].width = 2;
		j++;

		return new EncodedBarcode(elements, barcodeString);
	}

	/**
	 * Method getBarcodeHeight.
	 * @return int
	 */
	public int getBarcodeHeight() {
		return barcodeHeight;
	}

	/**
	 * Method getBarcodeLengthInChars.
	 * @return int
	 */
	public int getBarcodeLengthInChars() {

		return cc.length;

	}

	/**
	 * Method setBarcodeHeight.
	 * @param barcodeHeight int
	 */
	public void setBarcodeHeight(int barcodeHeight) {

		this.barcodeHeight = barcodeHeight;

		repaint();
	}

	// parameters: the graphics context on which to paint this barcode, the x
	// offset (barcode will be centered) and the y co-ord of the barcode
	/**
	 * Method doPaint.
	 * @param graphics Graphics
	 * @param xPos int
	 * @param yPos int
	 */
	public void doPaint(Graphics graphics, int xPos, int yPos) {

		// this method is problematic - will not work. - see replacement below

		Graphics2D g = (Graphics2D) graphics;
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		//		Dimension d = new Dimension(width,height);
		Dimension d = getSize();
		log.debug("D.toString(): "+d.toString());

		StringBuffer barString = new StringBuffer(encoded.elements.length);

		// Save graphics properties
		AffineTransform tran = g.getTransform();
		Shape oldClip = g.getClip();
		Color oldColor = g.getColor();

		// Fill control background
		g.setColor(Color.WHITE);
		g.fillRect(xPos, yPos, d.width, d.height);

		g.rotate(angleDegrees / 180 * Math.PI, d.width / 2.0, d.height / 2.0);

		// Draw barcode
		if (encoded != null) {
			for (int i = 0; i < encoded.elements.length; i++) {

				if (encoded.elements[i].bar == true) {
					switch (encoded.elements[i].width) {
					case 1:
						barString.append("A");
						break;
					case 2:
						barString.append("B");
						break;
					case 3:
						barString.append("C");
						break;
					case 4:
						barString.append("D");
						break;

					}
				}

				else if (encoded.elements[i].bar == false) {
					switch (encoded.elements[i].width) {
					case 1:
						barString.append(" ");
						break;
					case 2:
						barString.append("  ");
						break;
					case 3:
						barString.append("   ");
						break;
					case 4:
						barString.append("    ");
						break;

					}

				}
			}

		}


		try {
			File fontFile = new File("Code128mel.ttf");
			Font barFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			barFont = barFont.deriveFont(Font.PLAIN, 12);
			g.setFont(barFont);
			g.setColor(Color.BLACK);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(barString.toString(), PrintLayoutUtils.center(fm,	barString.toString(), (int)d.getWidth()), yPos);
			log.debug("============BarString.toString(): "+barString.toString());
			log.debug("WIDTH: " + (int)d.getWidth());

			//g.drawString(barString.toString(), PrintLayoutUtils.center(fm,	barString.toString(), xPos), yPos);
		}

		catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (FontFormatException fe) {
			fe.printStackTrace();
		}

		// Restore graphics properties
		g.setTransform(tran);
		g.setClip(oldClip);
		g.setColor(oldColor);

	}




	public void doPaint(Graphics graphics, int xPos, int yPos, int height, int width) {

		Graphics2D g = (Graphics2D) graphics;
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		Dimension d = new Dimension(width,height);
		log.debug("D.toString(): "+d.toString());

		StringBuffer barString = new StringBuffer(encoded.elements.length);

		// Save graphics properties
		AffineTransform tran = g.getTransform();
		Shape oldClip = g.getClip();
		Color oldColor = g.getColor();

		g.rotate(angleDegrees / 180 * Math.PI, d.width / 2.0, d.height / 2.0);

		// Draw barcode
		if (encoded != null) {
			for (int i = 0; i < encoded.elements.length; i++) {

				if (encoded.elements[i].bar == true) {
					switch (encoded.elements[i].width) {
					case 1:
						barString.append("A");
						break;
					case 2:
						barString.append("B");
						break;
					case 3:
						barString.append("C");
						break;
					case 4:
						barString.append("D");
						break;

					}
				}

				else if (encoded.elements[i].bar == false) {
					switch (encoded.elements[i].width) {
					case 1:
						barString.append(" ");
						break;
					case 2:
						barString.append("  ");
						break;
					case 3:
						barString.append("   ");
						break;
					case 4:
						barString.append("    ");
						break;

					}

				}
			}

		}


		try {
			File fontFile = new File("Code128mel.ttf");
			Font barFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			barFont = barFont.deriveFont(Font.PLAIN, 32f);
			g.setFont(barFont);
			g.setColor(Color.BLACK);
			FontMetrics fm = g.getFontMetrics();
			g.drawString(barString.toString(), PrintLayoutUtils.center(fm,	barString.toString(), (int)d.getWidth()), yPos);
			log.debug("============BarString.toString(): "+barString.toString());
			log.debug("WIDTH: " + (int)d.getWidth());

			//g.drawString(barString.toString(), PrintLayoutUtils.center(fm,	barString.toString(), xPos), yPos);
		}

		catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (FontFormatException fe) {
			fe.printStackTrace();
		}

		// Restore graphics properties
		g.setTransform(tran);
		g.setClip(oldClip);
		g.setColor(oldColor);

	}

	public static int getLengthForCurrentOS(){
		String OSName = System.getProperty("os.name").toUpperCase(); //$NON-NLS-1$
		if (OSName.startsWith("WINDOWS")) { //$NON-NLS-1$
			return windowsBarcodeLength;
		} else{
			return otherOSBarcodeLength;
		}
	}
}
