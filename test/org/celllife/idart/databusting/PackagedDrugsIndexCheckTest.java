/**
 * Unit test for PackagedDrugsIndexCheck
 */
package org.celllife.idart.databusting;

import java.util.Arrays;
import java.util.List;

import org.celllife.idart.misc.task.PackagedDrugsIndexCheck;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author ilda
 *
 */
public class PackagedDrugsIndexCheckTest {

	/**
	 * Test method for {@link
	 * org.celllife.idart.databusting#indicesOk(java.util.List<Integer>)} .
	 */
	@Test(dataProvider = "listProvider")
	public void testIndicesOk(List<Integer> pdList, boolean result) {
		PackagedDrugsIndexCheck p = new PackagedDrugsIndexCheck();
		Assert.assertEquals(p.indicesOk(pdList), result);
	}

	@DataProvider(name = "listProvider")
	public Object[][] dataProvider() {
		// create array of test arrays with correct outcomes
		return new Object[][] {
				{ Arrays.asList(new Integer[] { 0, 1, 2 }), true },
				{ Arrays.asList(new Integer[] { 1, 2, 3 }), false },
				{ Arrays.asList(new Integer[] { 0, 2 }), false },
				{ Arrays.asList(new Integer[] { 0, 2, 1 }), false } };
	}
}
