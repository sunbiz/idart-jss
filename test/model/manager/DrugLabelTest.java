package model.manager;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.test.HibernateTest;
import org.testng.annotations.Test;

public class DrugLabelTest extends HibernateTest {

	@Test()
	public void testGetQuantityDispensedForLabel() {
		/*
		 * Expecting a package with accumulated drugs for the previous package.
		 */
		Set<AccumulatedDrugs> accumulatedDrugs = PackageManager.getPackage(
				getSession(), "TEST123").getAccumulatedDrugs();

		for (AccumulatedDrugs accumDrugs : accumulatedDrugs) {
			Drug drug = accumDrugs.getPillCount().getDrug();
			int accum = accumDrugs.getPillCount().getAccum();

			String test = PackageManager.getQuantityDispensedForLabel(
					accumulatedDrugs, drug.getPackSize(), drug.getName(), drug.getPackSize(),
					false, true);
			
			String expected ="";
			if (accum == 0){
				expected = "(" + drug.getPackSize() + ")";
			} else {
				expected = "(" + drug.getPackSize() + " + " + accum + ")";
			}
			assertTrue(expected + " expected", test.equals(expected));
		}

	}

	public void createData() {
		startTransaction();
		utils.createPackage(utils.createPatient("1234"), "TEST123", 4, utils
				.getDrugs(3), new int[]{0,1,2});
		getSession().flush();
		endTransactionAndCommit();
	}
}
