package org.celllife.idart.print.label;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * The class <code>PrintLayoutUtilsTest</code> contains tests for the class
 * <code>{@link PrintLayoutUtils}</code>.
 * 
 * @generatedBy CodePro at 3/13/08 12:46 PM
 * @author renato
 * @version $Revision: 1.0 $
 */
public class PrintLayoutUtilsTest {

	private Font testFont = new Font("Arial", java.awt.Font.PLAIN, 10);

	@DataProvider(name = "names")
	public Object[][] createNames() {
		return new Object[][] {
				{ "John Michael Alex McEntire Robertson", "Mitchel",
						"J. M. A. M. R. Mitchel", 400 },
				{ "John Michael Alex McEntire Robertson", "Mitchel Lopez",
						"J. M. A. M. R. Mitchel Lopez", 400 },
				{ "John Michael Alex McEntire Robertson", "Mitchel Lopez",
						"J. Mitchel Lopez", 200 },
				{ "John Michael Alex McEntire Robertson",
						"Mitchel Lopez Ferrowline", "Mitchel Lopez Fer...", 200 },
				{ "John Michael Alex McEntire Robertson",
						"Mitchel Lopez Ferrowline Daniels Tunner",
						"Mitchel Lopez Fer...", 200 },
				{ "John Michael Alex McEntire Robertson",
						"Mitchel Lopez Ferrowline Daniels Tunner",
						"Mitchel Lopez Ferrowline Daniels Tunner", 400 },
				{ "John Michael Alex McEntire Robertson",
						"Mitchel Lopez Ferrowline Daniels Tunner",
						"Mitchel Lopez Ferrowline Da...", 300 },
				{ "John Michael Alex McEntire Robertson",
						"Mitchel Lopez Ferrowline Daniels Tunner",
						"Mitchel...", 100 },
				{ "John", "Mitchel", "Mitchel", 100 },
				{ "John", "Mitchel", "John Mitchel", 200 },
				{ "John", "Mitchel", "John Mitchel", 300 },
				{ "John", "Mitchel Lopez Ferrowline Daniels Tunner",
						"Mitchel Lopez Ferrowline Da...", 300 },
				{ "John", "Mitchel Lopez Ferrowliner",
						"John Mitchel Lopez Ferrowliner", 300 },
				{ "John", "Mitchel Lopez Ferrowliner Daniels",
						"Mitchel Lope...", 150 },
				{ "John Michael Alex McEntire Robertson", "Mitchel",
						"John Michael Alex McEntire Robertson Mitchel", 600 },
				{ "Johanes", "Mitchel", "J. Mitchel", 130 }, };
	}

	/**
	 * Run the String buildEPL2CompressedName(int,String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false, dataProvider = "names")
	public void testBuildEPL2CompressedName_1(String name, String surname,
			String expectedResult, int allocatedWidth) throws Exception {
		String result = PrintLayoutUtils.buildEPL2CompressedName(
				allocatedWidth, name, surname);

		assertNotNull(result);
		assertEquals(expectedResult, result);
	}

	/**
	 * Run the String testBuildWindowsCompressedLabelName(int,FontMetrics,
	 * String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false)
	public void testBuildWindowsCompressedLabelName_1() throws Exception {
		int allocatedWidth = 100;
		int allocatedHeight = 130;
		FontMetrics fm = getFontMetrics(allocatedWidth, allocatedHeight);
		String name = "Johanes";
		String surname = "Mitchel";
		String result = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth, fm, name, surname);

		assertNotNull(result);
		assertEquals("J. Mitchel", result);
	}

	/**
	 * Run the String testBuildWindowsCompressedLabelName(int,FontMetrics,
	 * String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false)
	public void testBuildWindowsCompressedLabelName_2() throws Exception {
		int allocatedWidth = 150;
		int allocatedHeight = 130;
		FontMetrics fm = getFontMetrics(allocatedWidth, allocatedHeight);
		String name = "Johanes Maxwell";
		String surname = "Mitchel";
		String result = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth, fm, name, surname);

		assertNotNull(result);
		assertEquals("J. M. Mitchel", result);
	}

	/**
	 * Run the String testBuildWindowsCompressedLabelName(int,FontMetrics,
	 * String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false)
	public void testBuildWindowsCompressedLabelName_3() throws Exception {
		int allocatedWidth = 200;
		int allocatedHeight = 130;
		FontMetrics fm = getFontMetrics(allocatedWidth, allocatedHeight);
		String name = "Johanes Maxwell";
		String surname = "Mitchel";
		String result = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth, fm, name, surname);

		assertNotNull(result);
		assertEquals("Johanes Maxwell Mitchel", result);
	}

	/**
	 * Run the String testBuildWindowsCompressedLabelName(int,FontMetrics,
	 * String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false)
	public void testBuildWindowsCompressedLabelName_4() throws Exception {
		int allocatedWidth = 200;
		int allocatedHeight = 130;
		FontMetrics fm = getFontMetrics(allocatedWidth, allocatedHeight);
		String name = "Johanes Maxwell Williams";
		String surname = "Mitchel";
		String result = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth, fm, name, surname);

		assertNotNull(result);
		assertEquals("J. M. W. Mitchel", result);
	}

	/**
	 * Run the String testBuildWindowsCompressedLabelName(int,FontMetrics,
	 * String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false)
	public void testBuildWindowsCompressedLabelName_5() throws Exception {
		int allocatedWidth = 115;
		int allocatedHeight = 130;
		FontMetrics fm = getFontMetrics(allocatedWidth, allocatedHeight);
		String name = "Johanes Maxwell Williams";
		String surname = "Mitchel Lorenz";
		String result = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth, fm, name, surname);

		assertNotNull(result);
		assertEquals("Mitchel Lorenz", result);
	}

	/**
	 * Run the String testBuildWindowsCompressedLabelName(int,FontMetrics,
	 * String,String) method test.
	 * 
	 * @generatedBy CodePro at 3/13/08 12:46 PM
	 */
	@Test(enabled = false)
	public void testBuildWindowsCompressedLabelName_6() throws Exception {
		int allocatedWidth = 91;
		int allocatedHeight = 130;
		FontMetrics fm = getFontMetrics(allocatedWidth, allocatedHeight);
		String name = "Johanes Maxwell Williams";
		String surname = "Mitchel Lorenz";
		String result = PrintLayoutUtils.buildWindowsCompressedLabelName(
				allocatedWidth, fm, name, surname);

		assertNotNull(result);
		assertEquals("Mitchel Lo...", result);
	}

	private FontMetrics getFontMetrics(int allocatedWidth, int allocatedHeight) {
		BufferedImage bufferedImage = new BufferedImage(allocatedWidth,
				allocatedHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		FontMetrics fm = g2d.getFontMetrics(testFont);
		return fm;
	}
}