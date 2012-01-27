package org.celllife.idart.gui;

import org.celllife.idart.gui.login.LoginTest;
import org.celllife.idart.gui.patient.AddPatientTest;
import org.celllife.idart.gui.welcome.PharmacyWelcomeTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  LoginTest.class,
  AddPatientTest.class,
  PharmacyWelcomeTest.class 
})
public class AllTests {


}
