package org.celllife.idart.gui.login;

import org.celllife.idart.messages.Messages;
import org.celllife.idart.test.gui.AbstractGUITest;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Test;

public class LoginTest extends AbstractGUITest {

	@Test
	public void testLogin() {
		login();
	}

	@Test
	public void testLoginWithIncorrectPassword() throws Exception {
		SWTBotCCombo userName = bot.ccomboBoxWithId("login.username");
		userName.setSelection("admin");

		SWTBotText password = bot.textWithId("login.password");
		password.setText("");

		bot.buttonWithId("btnLogin").click();

		checkMessageDialog(Messages.getString("login.dialog.error.title"), 
				Messages.getString("login.error.password"));
	}

	@After
	public void tearDown() {
		goToLogin();
	}
}
