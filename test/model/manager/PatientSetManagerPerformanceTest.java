package model.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import model.nonPersistent.ExportPackageInfo;

import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.test.HibernateTest;
import org.dbunit.DatabaseUnitException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.Test;

public class PatientSetManagerPerformanceTest extends HibernateTest {

	@SuppressWarnings( { "unused" })
	private static final Class<PatientSetManager> testClass = PatientSetManager.class;

	@Test(invocationCount = 1)
	public static void getExportPackageInfoTest() {
		// Getting test patients

		Map<Integer, Map<Integer, ExportPackageInfo>> mapExportPackgInfo = null;
		// mapExportPackgInfo = PatientSetManager.getPackageDetail(sess, null);
		mapExportPackgInfo = PatientSetManager.getPackageDetail(getSession(), null,
				1,null,null);
		// Fail test if mapExportPackgInf is null
		List list = getSession().createCriteria(Packages.class, "package").createAlias(
				"package.prescription", "prescription").createAlias(
						"prescription.patient", "patient").add(
								Restrictions.isNotNull("package.prescription"))
								.add(Restrictions.isNotNull("package.pickupDate")).setProjection(
										Projections.countDistinct("patient.id")).list();
		int count = (Integer) list.get(0);

		Assert.assertNotNull(mapExportPackgInfo);
		Assert.assertEquals(count, mapExportPackgInfo.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.test.HibernateTest#safeEmptyDatabase()
	 */
	@Override
	public void safeEmptyDatabase() throws SQLException, DatabaseUnitException,
	IOException {
		// Don't empty db
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.test.HibernateTest#insertTestData()
	 */
	@Override
	protected void insertTestData() throws Exception {
		// don't insert test data
	}
}
