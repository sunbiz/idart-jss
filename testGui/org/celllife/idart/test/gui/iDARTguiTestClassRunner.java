package org.celllife.idart.test.gui;

import org.celllife.idart.start.PharmacyApplication;
import org.eclipse.swtbot.swt.finder.junit.SWTBotApplicationLauncherClassRunner;

public class iDARTguiTestClassRunner extends SWTBotApplicationLauncherClassRunner {

	public iDARTguiTestClassRunner(Class<?> klass) throws Exception {
		super(klass);
	}

	@Override
	public void startApplication() {
		PharmacyApplication.main(null);
	}
}
