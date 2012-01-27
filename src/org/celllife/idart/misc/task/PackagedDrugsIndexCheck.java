/**
 * This class checks all the PackagedDrug records in the database and ensures that each
 * Package's PackagedDrugs have indices numbered from 0,1,..., n-1 where n is number of
 * PackagedDrugs in a Package
 */
package org.celllife.idart.misc.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author ilda
 *
 */
public class PackagedDrugsIndexCheck extends JDBCTask {

	private static Statement statement;

	private static ResultSet results;

	private static Logger log = Logger.getLogger(PackagedDrugsIndexCheck.class);

	@Override
	public boolean init(String[] args) {

		return true;
	}

	@Override
	public void runTask(IProgressMonitor monitor)
	throws TaskException {

		try {
			statement = getConnection().createStatement();

			// fetch all the Packages ids in the database
			List<Integer> allPackageIds = new ArrayList<Integer>();
			String allPackageIdsQuery = "select distinct id from package";
			results = statement.executeQuery(allPackageIdsQuery);

			while (results.next()) {
				allPackageIds.add(results.getInt("id"));
			}

			// fetch all the PackagedDrugs indices for each package
			List<Integer> packagedDrugIndices;

			for (int p : allPackageIds) {
				monitor.setTaskName("Checking package indexes: " + p);
				packagedDrugIndices = getPackagedDrugsForPackage(p, false);

				// check whether this packages's packagedrugs' indices are
				// correctly numbered from 0 to n-1, if they are incorrectly
				// numbered then reset them
				boolean indicesOk = indicesOk(packagedDrugIndices);
				if (!indicesOk) {
					resetIndices(p);
					log.info("Indices reset for package " + p);
					packagedDrugIndices = getPackagedDrugsForPackage(p, false);
					if (!indicesOk(packagedDrugIndices))
						throw new TaskException(
						"PackagedDrugsIndexCheck task failed.");
				}
			}
		} catch (SQLException se) {
			log.error("PackagedDrugsIndexCheck unsuccessful", se);
			throw new TaskException("PackagedDrugsIndexCheck task failed.", se);
		}
	}

	private List<Integer> getPackagedDrugsForPackage(int p, boolean fetchIds)
	throws SQLException {
		List<Integer> packagedDrugs;
		packagedDrugs = new ArrayList<Integer>();

		String columnToFetch = fetchIds ? "id" : "packageddrugsindex";
		String allPackagedDrugsIndicesQuery = "select " + columnToFetch
		+ " from packageddrugs where parentPackage = " + p
		+ " order by packageddrugsindex asc";
		results = statement.executeQuery(allPackagedDrugsIndicesQuery);

		while (results.next()) {
			packagedDrugs.add(results.getInt(1));
		}
		return packagedDrugs;
	}

	public boolean indicesOk(List<Integer> pdList) {
		for (int i = 0; i < pdList.size(); i++) {
			if (pdList.get(i) != i)
				return false;
		}
		return true;
	}

	public void resetIndices(int packageId) throws SQLException {

		List<Integer> pdList = getPackagedDrugsForPackage(packageId, true);

		for (int i = 0; i < pdList.size(); i++) {
			String pdIndexUpdate = "update packageddrugs set packageddrugsindex = "
				+ i + " where id = " + pdList.get(i);
			statement.executeUpdate(pdIndexUpdate);
		}
	}

	@Override
	public String getHelpText() {
		return "No arguements required.";
	}

	@Override
	public String getDescription() {
		return "checks all the PackagedDrug records in the "
		+ "database and ensures their indicies are numbered correctly";
	}
}
