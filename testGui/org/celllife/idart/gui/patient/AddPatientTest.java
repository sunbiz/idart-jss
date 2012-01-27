package org.celllife.idart.gui.patient;

import java.text.MessageFormat;

import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.Screens;
import org.celllife.idart.test.gui.AbstractGUITest;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AddPatientTest extends AbstractGUITest {

	@Before
	public void setup() {
		goToScreen(Screens.ADD_PATIENT);
	}

	@Test
	public void testEmptyPatientId(){
		SWTBotText patientId = bot.textWithId("txtPatientId");
		patientId.pressShortcut(Keystrokes.CR);
		
		checkMessageDialog(Messages.getString("patient.error.missingPatientId.title"),
				Messages.getString("patient.error.missingPatientId"));
	}
	
	@Test
	public void testIllegalCharInPatientId(){
		SWTBotText patientId = bot.textWithId("txtPatientId");
		patientId.typeText("12'3");
		patientId.pressShortcut(Keystrokes.CR);
		
		checkMessageDialog(Messages.getString("patient.error.badCharacterInPatientId.title"),
				Messages.getString("patient.error.badCharacterInPatientId"));
	}
	
	@Test
	public void testExistingPatientId(){
		SWTBotText patientId = bot.textWithId("txtPatientId");
		patientId.typeText("123");
		patientId.pressShortcut(Keystrokes.CR);
		
		checkMessageDialog(Messages.getString("patient.error.patientAlreadyExists.title"),
				MessageFormat.format(Messages.getString("patient.error.patientAlreadyExists"),
						"123"));
	}

	@After
	public void tearDown() {
		goToPharmacyWelcome();
	}
}
