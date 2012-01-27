package org.celllife.idart.gui.welcome;

import junit.framework.Assert;

import org.celllife.idart.misc.Screens;
import org.celllife.idart.test.gui.AbstractGUITest;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PharmacyWelcomeTest extends AbstractGUITest {

	@Before
	public void setup() {
		goToPharmacyWelcome();
	}

	@Test
	public void testGeneralAdminButton() {
		testWelcomeButton(Screens.GENERAL_ADMIN);
	}

	@Test
	public void testPatientAdminButton() {
		testWelcomeButton(Screens.PATIENT_ADMIN);
	}

	@Test
	public void testStockControlButton() {
		testWelcomeButton(Screens.STOCK_CONTROL);
	}

	@Test
	public void testReportsButton() {
		testWelcomeButton(Screens.REPORTS);
	}

	public void testWelcomeButton(Screens screen) {
		bot.buttonWithId(screen.getAccessButtonId()).click();
		SWTBotShell activeShell = bot.activeShell();

		Assert.assertTrue(activeShell.getText()
				.contains(screen.getShellTitle()));
	}

	@After
	public void tearDown() {
		goToPharmacyWelcome();
	}
}
