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

/*
 * Maps the characters to be encoded to their Code128 B or C representations
 * Uses code from jbarcodebean, http://jbarcodebean.sourceforge.net/
 * 
 */

/**
 */
public class CompactCode128 {
	/** Code 128 FUNCTION CODE 1 */
	public static final String FNC_1 = "\u0080";

	/** Code 128 FUNCTION CODE 2 */
	public static final String FNC_2 = "\u0081";

	/** Code 128 FUNCTION CODE 3 */
	public static final String FNC_3 = "\u0082";

	/** Code 128 FUNCTION CODE 4 */
	public static final String FNC_4 = "\u0083";

	public static final String START_A = "\u0084";

	public static final String START_B = "\u0085";

	public static final String START_C = "\u0086";

	public static final String MODE_A = "\u0087";

	public static final String MODE_B = "\u00C8";

	public static final String MODE_C = "\u0089";

	public static final String SHIFT = "\u008A";

	public static final String STOP = "\u008B";

	/**
	 */
	public static class CharacterCode {

		/** The character that is encoded */
		public String character;

		/** The widths of the modules (bars and spaces) of this encoded character */
		public byte[] widths;

		/**
		 * The check digit corresponding to this character, used in checksum
		 * calculations
		 */
		public int check;

		/** Constructor which fully initializes the properties of the object. * @param character String
		 * @param widths byte[]
		 * @param check int
		 * @param character String
		 * @param widths byte[]
		 * @param check int
		 */
		public CharacterCode(String character, byte[] widths, int check) {
			this.character = character;
			this.widths = widths;
			this.check = check;
		}
	}

	/**
	 * A static array of
	 * {@link AbstractBarcodeStrategy.CharacterCode CharacterCode} objects for
	 * Code 128. The {@link AbstractBarcodeStrategy#getCodes() getCodes()}
	 * method returns this array.
	 * 
	 * The <tt>character</tt> member of the elements in this array corresponds
	 * to the MODE B version of the character.
	 */
	protected static CharacterCode[] codes = {
			new CharacterCode(" ", new byte[] { 2, 1, 2, 2, 2, 2 }, 0),
			new CharacterCode("!", new byte[] { 2, 2, 2, 1, 2, 2 }, 1),
			new CharacterCode("\"", new byte[] { 2, 2, 2, 2, 2, 1 }, 2),
			new CharacterCode("#", new byte[] { 1, 2, 1, 2, 2, 3 }, 3),
			new CharacterCode("$", new byte[] { 1, 2, 1, 3, 2, 2 }, 4),
			new CharacterCode("%", new byte[] { 1, 3, 1, 2, 2, 2 }, 5),
			new CharacterCode("&", new byte[] { 1, 2, 2, 2, 1, 3 }, 6),
			new CharacterCode("\"", new byte[] { 1, 2, 2, 3, 1, 2 }, 7),
			new CharacterCode("(", new byte[] { 1, 3, 2, 2, 1, 2 }, 8),
			new CharacterCode(")", new byte[] { 2, 2, 1, 2, 1, 3 }, 9),
			new CharacterCode("*", new byte[] { 2, 2, 1, 3, 1, 2 }, 10),
			new CharacterCode("+", new byte[] { 2, 3, 1, 2, 1, 2 }, 11),
			new CharacterCode(",", new byte[] { 1, 1, 2, 2, 3, 2 }, 12),
			new CharacterCode("-", new byte[] { 1, 2, 2, 1, 3, 2 }, 13),
			new CharacterCode(".", new byte[] { 1, 2, 2, 2, 3, 1 }, 14),
			new CharacterCode("/", new byte[] { 1, 1, 3, 2, 2, 2 }, 15),
			new CharacterCode("0", new byte[] { 1, 2, 3, 1, 2, 2 }, 16),
			new CharacterCode("1", new byte[] { 1, 2, 3, 2, 2, 1 }, 17),
			new CharacterCode("2", new byte[] { 2, 2, 3, 2, 1, 1 }, 18),
			new CharacterCode("3", new byte[] { 2, 2, 1, 1, 3, 2 }, 19),
			new CharacterCode("4", new byte[] { 2, 2, 1, 2, 3, 1 }, 20),
			new CharacterCode("5", new byte[] { 2, 1, 3, 2, 1, 2 }, 21),
			new CharacterCode("6", new byte[] { 2, 2, 3, 1, 1, 2 }, 22),
			new CharacterCode("7", new byte[] { 3, 1, 2, 1, 3, 1 }, 23),
			new CharacterCode("8", new byte[] { 3, 1, 1, 2, 2, 2 }, 24),
			new CharacterCode("9", new byte[] { 3, 2, 1, 1, 2, 2 }, 25),
			new CharacterCode(":", new byte[] { 3, 2, 1, 2, 2, 1 }, 26),
			new CharacterCode(";", new byte[] { 3, 1, 2, 2, 1, 2 }, 27),
			new CharacterCode("<", new byte[] { 3, 2, 2, 1, 1, 2 }, 28),
			new CharacterCode("=", new byte[] { 3, 2, 2, 2, 1, 1 }, 29),
			new CharacterCode(">", new byte[] { 2, 1, 2, 1, 2, 3 }, 30),
			new CharacterCode("?", new byte[] { 2, 1, 2, 3, 2, 1 }, 31),
			new CharacterCode("@", new byte[] { 2, 3, 2, 1, 2, 1 }, 32),
			new CharacterCode("A", new byte[] { 1, 1, 1, 3, 2, 3 }, 33),
			new CharacterCode("B", new byte[] { 1, 3, 1, 1, 2, 3 }, 34),
			new CharacterCode("C", new byte[] { 1, 3, 1, 3, 2, 1 }, 35),
			new CharacterCode("D", new byte[] { 1, 1, 2, 3, 1, 3 }, 36),
			new CharacterCode("E", new byte[] { 1, 3, 2, 1, 1, 3 }, 37),
			new CharacterCode("F", new byte[] { 1, 3, 2, 3, 1, 1 }, 38),
			new CharacterCode("G", new byte[] { 2, 1, 1, 3, 1, 3 }, 39),
			new CharacterCode("H", new byte[] { 2, 3, 1, 1, 1, 3 }, 40),
			new CharacterCode("I", new byte[] { 2, 3, 1, 3, 1, 1 }, 41),
			new CharacterCode("J", new byte[] { 1, 1, 2, 1, 3, 3 }, 42),
			new CharacterCode("K", new byte[] { 1, 1, 2, 3, 3, 1 }, 43),
			new CharacterCode("L", new byte[] { 1, 3, 2, 1, 3, 1 }, 44),
			new CharacterCode("M", new byte[] { 1, 1, 3, 1, 2, 3 }, 45),
			new CharacterCode("N", new byte[] { 1, 1, 3, 3, 2, 1 }, 46),
			new CharacterCode("O", new byte[] { 1, 3, 3, 1, 2, 1 }, 47),
			new CharacterCode("P", new byte[] { 3, 1, 3, 1, 2, 1 }, 48),
			new CharacterCode("Q", new byte[] { 2, 1, 1, 3, 3, 1 }, 49),
			new CharacterCode("R", new byte[] { 2, 3, 1, 1, 3, 1 }, 50),
			new CharacterCode("S", new byte[] { 2, 1, 3, 1, 1, 3 }, 51),
			new CharacterCode("T", new byte[] { 2, 1, 3, 3, 1, 1 }, 52),
			new CharacterCode("U", new byte[] { 2, 1, 3, 1, 3, 1 }, 53),
			new CharacterCode("V", new byte[] { 3, 1, 1, 1, 2, 3 }, 54),
			new CharacterCode("W", new byte[] { 3, 1, 1, 3, 2, 1 }, 55),
			new CharacterCode("X", new byte[] { 3, 3, 1, 1, 2, 1 }, 56),
			new CharacterCode("Y", new byte[] { 3, 1, 2, 1, 1, 3 }, 57),
			new CharacterCode("Z", new byte[] { 3, 1, 2, 3, 1, 1 }, 58),
			new CharacterCode("[", new byte[] { 3, 3, 2, 1, 1, 1 }, 59),
			new CharacterCode("\\", new byte[] { 3, 1, 4, 1, 1, 1 }, 60),
			new CharacterCode("]", new byte[] { 2, 2, 1, 4, 1, 1 }, 61),
			new CharacterCode("^", new byte[] { 4, 3, 1, 1, 1, 1 }, 62),
			new CharacterCode("_", new byte[] { 1, 1, 1, 2, 2, 4 }, 63),
			new CharacterCode("`", new byte[] { 1, 1, 1, 4, 2, 2 }, 64),
			new CharacterCode("a", new byte[] { 1, 2, 1, 1, 2, 4 }, 65),
			new CharacterCode("b", new byte[] { 1, 2, 1, 4, 2, 1 }, 66),
			new CharacterCode("c", new byte[] { 1, 4, 1, 1, 2, 2 }, 67),
			new CharacterCode("d", new byte[] { 1, 4, 1, 2, 2, 1 }, 68),
			new CharacterCode("e", new byte[] { 1, 1, 2, 2, 1, 4 }, 69),
			new CharacterCode("f", new byte[] { 1, 1, 2, 4, 1, 2 }, 70),
			new CharacterCode("g", new byte[] { 1, 2, 2, 1, 1, 4 }, 71),
			new CharacterCode("h", new byte[] { 1, 2, 2, 4, 1, 1 }, 72),
			new CharacterCode("i", new byte[] { 1, 4, 2, 1, 1, 2 }, 73),
			new CharacterCode("j", new byte[] { 1, 4, 2, 2, 1, 1 }, 74),
			new CharacterCode("k", new byte[] { 2, 4, 1, 2, 1, 1 }, 75),
			new CharacterCode("l", new byte[] { 2, 2, 1, 1, 1, 4 }, 76),
			new CharacterCode("m", new byte[] { 4, 1, 3, 1, 1, 1 }, 77),
			new CharacterCode("n", new byte[] { 2, 4, 1, 1, 1, 2 }, 78),
			new CharacterCode("o", new byte[] { 1, 3, 4, 1, 1, 1 }, 79),
			new CharacterCode("p", new byte[] { 1, 1, 1, 2, 4, 2 }, 80),
			new CharacterCode("q", new byte[] { 1, 2, 1, 1, 4, 2 }, 81),
			new CharacterCode("r", new byte[] { 1, 2, 1, 2, 4, 1 }, 82),
			new CharacterCode("s", new byte[] { 1, 1, 4, 2, 1, 2 }, 83),
			new CharacterCode("t", new byte[] { 1, 2, 4, 1, 1, 2 }, 84),
			new CharacterCode("u", new byte[] { 1, 2, 4, 2, 1, 1 }, 85),
			new CharacterCode("v", new byte[] { 4, 1, 1, 2, 1, 2 }, 86),
			new CharacterCode("w", new byte[] { 4, 2, 1, 1, 1, 2 }, 87),
			new CharacterCode("x", new byte[] { 4, 2, 1, 2, 1, 1 }, 88),
			new CharacterCode("y", new byte[] { 2, 1, 2, 1, 4, 1 }, 89),
			new CharacterCode("z", new byte[] { 2, 1, 4, 1, 2, 1 }, 90),
			new CharacterCode("{", new byte[] { 4, 1, 2, 1, 2, 1 }, 91),
			new CharacterCode("|", new byte[] { 1, 1, 1, 1, 4, 3 }, 92),
			new CharacterCode("}", new byte[] { 1, 1, 1, 3, 4, 1 }, 93),
			new CharacterCode("~", new byte[] { 1, 3, 1, 1, 4, 1 }, 94),
			new CharacterCode("\u007F", new byte[] { 1, 1, 4, 1, 1, 3 }, 95),
			new CharacterCode(FNC_3, new byte[] { 1, 1, 4, 3, 1, 1 }, 96),
			new CharacterCode(FNC_2, new byte[] { 4, 1, 1, 1, 1, 3 }, 97),
			new CharacterCode(SHIFT, new byte[] { 4, 1, 1, 3, 1, 1 }, 98),
			new CharacterCode(MODE_C, new byte[] { 1, 1, 3, 1, 4, 1 }, 99),
			new CharacterCode(FNC_4, new byte[] { 1, 1, 4, 1, 3, 1 }, 100),
			new CharacterCode(MODE_A, new byte[] { 3, 1, 1, 1, 4, 1 }, 101),
			new CharacterCode(FNC_1, new byte[] { 4, 1, 1, 1, 3, 1 }, 102),
			new CharacterCode(START_A, new byte[] { 2, 1, 1, 4, 1, 2 }, 103),
			new CharacterCode(START_B, new byte[] { 2, 1, 1, 2, 1, 4 }, 104),
			new CharacterCode(START_C, new byte[] { 2, 1, 1, 2, 3, 2 }, 105),
			new CharacterCode(STOP, new byte[] { 2, 3, 3, 1, 1, 1, 2 }, 106) };

	protected static CharacterCode[] codesC = {
			new CharacterCode("00", new byte[] { 2, 1, 2, 2, 2, 2 }, 0),
			new CharacterCode("01", new byte[] { 2, 2, 2, 1, 2, 2 }, 1),
			new CharacterCode("02", new byte[] { 2, 2, 2, 2, 2, 1 }, 2),
			new CharacterCode("03", new byte[] { 1, 2, 1, 2, 2, 3 }, 3),
			new CharacterCode("04", new byte[] { 1, 2, 1, 3, 2, 2 }, 4),
			new CharacterCode("05", new byte[] { 1, 3, 1, 2, 2, 2 }, 5),
			new CharacterCode("06", new byte[] { 1, 2, 2, 2, 1, 3 }, 6),
			new CharacterCode("07", new byte[] { 1, 2, 2, 3, 1, 2 }, 7),
			new CharacterCode("08", new byte[] { 1, 3, 2, 2, 1, 2 }, 8),
			new CharacterCode("09", new byte[] { 2, 2, 1, 2, 1, 3 }, 9),
			new CharacterCode("10", new byte[] { 2, 2, 1, 3, 1, 2 }, 10),
			new CharacterCode("11", new byte[] { 2, 3, 1, 2, 1, 2 }, 11),
			new CharacterCode("12", new byte[] { 1, 1, 2, 2, 3, 2 }, 12),
			new CharacterCode("13", new byte[] { 1, 2, 2, 1, 3, 2 }, 13),
			new CharacterCode("14", new byte[] { 1, 2, 2, 2, 3, 1 }, 14),
			new CharacterCode("15", new byte[] { 1, 1, 3, 2, 2, 2 }, 15),
			new CharacterCode("16", new byte[] { 1, 2, 3, 1, 2, 2 }, 16),
			new CharacterCode("17", new byte[] { 1, 2, 3, 2, 2, 1 }, 17),
			new CharacterCode("18", new byte[] { 2, 2, 3, 2, 1, 1 }, 18),
			new CharacterCode("19", new byte[] { 2, 2, 1, 1, 3, 2 }, 19),
			new CharacterCode("20", new byte[] { 2, 2, 1, 2, 3, 1 }, 20),
			new CharacterCode("21", new byte[] { 2, 1, 3, 2, 1, 2 }, 21),
			new CharacterCode("22", new byte[] { 2, 2, 3, 1, 1, 2 }, 22),
			new CharacterCode("23", new byte[] { 3, 1, 2, 1, 3, 1 }, 23),
			new CharacterCode("24", new byte[] { 3, 1, 1, 2, 2, 2 }, 24),
			new CharacterCode("25", new byte[] { 3, 2, 1, 1, 2, 2 }, 25),
			new CharacterCode("26", new byte[] { 3, 2, 1, 2, 2, 1 }, 26),
			new CharacterCode("27", new byte[] { 3, 1, 2, 2, 1, 2 }, 27),
			new CharacterCode("28", new byte[] { 3, 2, 2, 1, 1, 2 }, 28),
			new CharacterCode("29", new byte[] { 3, 2, 2, 2, 1, 1 }, 29),
			new CharacterCode("30", new byte[] { 2, 1, 2, 1, 2, 3 }, 30),
			new CharacterCode("31", new byte[] { 2, 1, 2, 3, 2, 1 }, 31),
			new CharacterCode("32", new byte[] { 2, 3, 2, 1, 2, 1 }, 32),
			new CharacterCode("33", new byte[] { 1, 1, 1, 3, 2, 3 }, 33),
			new CharacterCode("34", new byte[] { 1, 3, 1, 1, 2, 3 }, 34),
			new CharacterCode("35", new byte[] { 1, 3, 1, 3, 2, 1 }, 35),
			new CharacterCode("36", new byte[] { 1, 1, 2, 3, 1, 3 }, 36),
			new CharacterCode("37", new byte[] { 1, 3, 2, 1, 1, 3 }, 37),
			new CharacterCode("38", new byte[] { 1, 3, 2, 3, 1, 1 }, 38),
			new CharacterCode("39", new byte[] { 2, 1, 1, 3, 1, 3 }, 39),
			new CharacterCode("40", new byte[] { 2, 3, 1, 1, 1, 3 }, 40),
			new CharacterCode("41", new byte[] { 2, 3, 1, 3, 1, 1 }, 41),
			new CharacterCode("42", new byte[] { 1, 1, 2, 1, 3, 3 }, 42),
			new CharacterCode("43", new byte[] { 1, 1, 2, 3, 3, 1 }, 43),
			new CharacterCode("44", new byte[] { 1, 3, 2, 1, 3, 1 }, 44),
			new CharacterCode("45", new byte[] { 1, 1, 3, 1, 2, 3 }, 45),
			new CharacterCode("46", new byte[] { 1, 1, 3, 3, 2, 1 }, 46),
			new CharacterCode("47", new byte[] { 1, 3, 3, 1, 2, 1 }, 47),
			new CharacterCode("48", new byte[] { 3, 1, 3, 1, 2, 1 }, 48),
			new CharacterCode("49", new byte[] { 2, 1, 1, 3, 3, 1 }, 49),
			new CharacterCode("50", new byte[] { 2, 3, 1, 1, 3, 1 }, 50),
			new CharacterCode("51", new byte[] { 2, 1, 3, 1, 1, 3 }, 51),
			new CharacterCode("52", new byte[] { 2, 1, 3, 3, 1, 1 }, 52),
			new CharacterCode("53", new byte[] { 2, 1, 3, 1, 3, 1 }, 53),
			new CharacterCode("54", new byte[] { 3, 1, 1, 1, 2, 3 }, 54),
			new CharacterCode("55", new byte[] { 3, 1, 1, 3, 2, 1 }, 55),
			new CharacterCode("56", new byte[] { 3, 3, 1, 1, 2, 1 }, 56),
			new CharacterCode("57", new byte[] { 3, 1, 2, 1, 1, 3 }, 57),
			new CharacterCode("58", new byte[] { 3, 1, 2, 3, 1, 1 }, 58),
			new CharacterCode("59", new byte[] { 3, 3, 2, 1, 1, 1 }, 59),
			new CharacterCode("60", new byte[] { 3, 1, 4, 1, 1, 1 }, 60),
			new CharacterCode("61", new byte[] { 2, 2, 1, 4, 1, 1 }, 61),
			new CharacterCode("62", new byte[] { 4, 3, 1, 1, 1, 1 }, 62),
			new CharacterCode("63", new byte[] { 1, 1, 1, 2, 2, 4 }, 63),
			new CharacterCode("64", new byte[] { 1, 1, 1, 4, 2, 2 }, 64),
			new CharacterCode("65", new byte[] { 1, 2, 1, 1, 2, 4 }, 65),
			new CharacterCode("66", new byte[] { 1, 2, 1, 4, 2, 1 }, 66),
			new CharacterCode("67", new byte[] { 1, 4, 1, 1, 2, 2 }, 67),
			new CharacterCode("68", new byte[] { 1, 4, 1, 2, 2, 1 }, 68),
			new CharacterCode("69", new byte[] { 1, 1, 2, 2, 1, 4 }, 69),
			new CharacterCode("70", new byte[] { 1, 1, 2, 4, 1, 2 }, 70),
			new CharacterCode("71", new byte[] { 1, 2, 2, 1, 1, 4 }, 71),
			new CharacterCode("72", new byte[] { 1, 2, 2, 4, 1, 1 }, 72),
			new CharacterCode("73", new byte[] { 1, 4, 2, 1, 1, 2 }, 73),
			new CharacterCode("74", new byte[] { 1, 4, 2, 2, 1, 1 }, 74),
			new CharacterCode("75", new byte[] { 2, 4, 1, 2, 1, 1 }, 75),
			new CharacterCode("76", new byte[] { 2, 2, 1, 1, 1, 4 }, 76),
			new CharacterCode("77", new byte[] { 4, 1, 3, 1, 1, 1 }, 77),
			new CharacterCode("78", new byte[] { 2, 4, 1, 1, 1, 2 }, 78),
			new CharacterCode("79", new byte[] { 1, 3, 4, 1, 1, 1 }, 79),
			new CharacterCode("80", new byte[] { 1, 1, 1, 2, 4, 2 }, 80),
			new CharacterCode("81", new byte[] { 1, 2, 1, 1, 4, 2 }, 81),
			new CharacterCode("82", new byte[] { 1, 2, 1, 2, 4, 1 }, 82),
			new CharacterCode("83", new byte[] { 1, 1, 4, 2, 1, 2 }, 83),
			new CharacterCode("84", new byte[] { 1, 2, 4, 1, 1, 2 }, 84),
			new CharacterCode("85", new byte[] { 1, 2, 4, 2, 1, 1 }, 85),
			new CharacterCode("86", new byte[] { 4, 1, 1, 2, 1, 2 }, 86),
			new CharacterCode("87", new byte[] { 4, 2, 1, 1, 1, 2 }, 87),
			new CharacterCode("88", new byte[] { 4, 2, 1, 2, 1, 1 }, 88),
			new CharacterCode("89", new byte[] { 2, 1, 2, 1, 4, 1 }, 89),
			new CharacterCode("90", new byte[] { 2, 1, 4, 1, 2, 1 }, 90),
			new CharacterCode("91", new byte[] { 4, 1, 2, 1, 2, 1 }, 91),
			new CharacterCode("92", new byte[] { 1, 1, 1, 1, 4, 3 }, 92),
			new CharacterCode("93", new byte[] { 1, 1, 1, 3, 4, 1 }, 93),
			new CharacterCode("94", new byte[] { 1, 3, 1, 1, 4, 1 }, 94),
			new CharacterCode("95", new byte[] { 1, 1, 4, 1, 1, 3 }, 95),
			new CharacterCode("96", new byte[] { 1, 1, 4, 3, 1, 1 }, 96),
			new CharacterCode("97", new byte[] { 4, 1, 1, 1, 1, 3 }, 97),
			new CharacterCode("98", new byte[] { 4, 1, 1, 3, 1, 1 }, 98),
			new CharacterCode("99", new byte[] { 1, 1, 3, 1, 4, 1 }, 99),
			new CharacterCode(MODE_B, new byte[] { 1, 1, 4, 1, 3, 1 }, 100),
			new CharacterCode(MODE_A, new byte[] { 3, 1, 1, 1, 4, 1 }, 101),
			new CharacterCode(FNC_1, new byte[] { 4, 1, 1, 1, 3, 1 }, 102),
			new CharacterCode(START_A, new byte[] { 2, 1, 1, 4, 1, 2 }, 103),
			new CharacterCode(START_B, new byte[] { 2, 1, 1, 2, 1, 4 }, 104),
			new CharacterCode(START_C, new byte[] { 2, 1, 1, 2, 3, 2 }, 105),
			new CharacterCode(STOP, new byte[] { 2, 3, 3, 1, 1, 1, 2 }, 106) };

	/**
	 * Method get128BVal.
	 * @param s String
	 * @return CharacterCode
	 */
	public static CharacterCode get128BVal(String s) {

		CharacterCode returnCode = new CharacterCode(null, null, 999);

		for (int i = 0; i < codes.length; i++) {
			if (s.equals(codes[i].character)) {
				returnCode = codes[i];
				break;
			}
		}

		return returnCode;
	}

	/**
	 * Method get128CValByInt.
	 * @param val int
	 * @return CharacterCode
	 */
	public static CharacterCode get128CValByInt(int val) {

		return codesC[val];
	}

	/**
	 * Method get128CVal.
	 * @param s String
	 * @return CharacterCode
	 */
	public static CharacterCode get128CVal(String s) {

		CharacterCode returnCode = new CharacterCode(null, null, 999);

		for (int i = 0; i < codesC.length; i++) {
			if (s.equals(codesC[i].character)) {
				returnCode = codesC[i];
				break;
			}
		}

		return returnCode;
	}

}
