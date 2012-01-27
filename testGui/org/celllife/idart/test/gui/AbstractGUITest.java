package org.celllife.idart.test.gui;

import java.util.Stack;

import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.Screens;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotAssert;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(iDARTguiTestClassRunner.class)
public abstract class AbstractGUITest {

	protected static SWTBot bot;

	@BeforeClass
	public static void beforeClass() {
		SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
		SWTBotPreferences.KEYBOARD_STRATEGY = "org.eclipse.swtbot.swt.finder.keyboard.SWTKeyboardStrategy";
		bot = new SWTBot();
	}
	
	public void login() {
		login("admin", "123");
	}

	public void login(String user, String passwd) {
		SWTBotCCombo userName = bot.ccomboBoxWithId("login.username");
		userName.setSelection(user);

		SWTBotText password = bot.textWithId("login.password");
		password.setText(passwd);

		bot.button("&Login").click();

		SWTBotButton logoff = bot.buttonWithId("logoff");
		SWTBotAssert.assertVisible(logoff);
	}

	protected void goToLogin() {
		while (true) {
			SWTBotShell activeShell = bot.activeShell();
			if (activeShell.getText().startsWith(
					Messages.getString("login.screen.title"))) {
				break;
			} else {
				activeShell.close();
			}
		}
	}
	
	protected void goToScreen(Screens screen){
		switch (screen) {
		case LOGIN:
			goToLogin();
			break;
		case PHARMACY_WELCOME:
			goToPharmacyWelcome();
		default:
			SWTBotShell activeShell = bot.activeShell();
			String shellTitle = screen.getShellTitle();
			if (!activeShell.getText().contains(shellTitle)){
				goToPharmacyWelcome();
			}
			
			Stack<Screens> screenStack = new Stack<Screens>();
			Screens parent = screen.getParent();
			do {
				if (parent != null && !parent.equals(Screens.PHARMACY_WELCOME)){
					screenStack.push(parent);
					parent = parent.getParent();
				}
			} while (parent != null && !parent.equals(Screens.PHARMACY_WELCOME));
	
			// add desired screen to stack
			screenStack.push(screen);
			
			for (Screens parent1 : screenStack) {
				String title = parent1.getShellTitle();
				bot.buttonWithId(parent1.getAccessButtonId()).click();
				bot.waitUntil(new ShellContainingTextIsActive(title), 1000);
//				activeShell = bot.activeShell();
//				Assert.assertTrue(
//						"Unable to navigate to screen: " + title,
//						activeShell.getText().contains(title));
			}
			break;
		}
	}

	protected void goToPharmacyWelcome() {
		while (true) {
			SWTBotShell activeShell = bot.activeShell();
			if (activeShell.getText().startsWith(
					Messages.getString("login.screen.title"))) {
				login();
			} else if (activeShell.getText().contains(
					Messages.getString("welcome.screen.name"))) {
				break;
			} else {
				activeShell.close();
			}
		}
	}
	
	protected void checkMessageDialog(String title, String message) {
		bot.waitUntil(Conditions.shellIsActive(title), 1000);
		SWTBot dialogBot = bot.shell(title).bot();
		if (message != null && !message.isEmpty()){
			SWTBotLabel errorLabel = dialogBot.label(message);
			SWTBotAssert.assertVisible(errorLabel);
		}
		dialogBot.button("OK").click();
		
	}
}
