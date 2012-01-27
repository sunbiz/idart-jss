package model.manager;

import org.celllife.idart.test.HibernateTest;
import org.testng.annotations.Test;

public class SearchManagerTest extends HibernateTest {
	
	@Test
	public void testGetPatient(){
		SearchManager.getPatientIdentifiersWithAwiatingPackages(getSession(), "");
	}

}
