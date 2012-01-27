package org.celllife.idart.misc;

import static org.junit.Assert.assertEquals;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class VersionTest {

	@Test(dataProvider = "versionProvider")
	public void testisGreaterThan(String version1, String version2,
			boolean result) {
		Version v1 = Version.parse(version1);
		Version v2 = Version.parse(version2);
		assertEquals(v1.isGreaterThan(v2), result);

	}

	@DataProvider(name = "versionProvider")
	public Object[][] versionProvider() {
		return new Object[][] { { "1.0", "1.1", false },
				{ "1.1", "1.0", true }, { "1.1.1", "1.1", true },
				{ "0.9", "0.9.1", false }, { "2.0", "1.9.9", true },
				{ "0.0.2", "0.0.1", true }, { "1.1.1", "1.1.2", false } };
	}
}
