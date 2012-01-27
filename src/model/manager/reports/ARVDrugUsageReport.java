package model.manager.reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Drug;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;

public class ARVDrugUsageReport extends AbstractJasperReport {

	private final String stockCenterName;
	private final List<Drug> drugList;
	private final Date endDate;
	private final Date startDate;
	private File csvFile;
	private String[] totalString;

	public ARVDrugUsageReport(Shell parent, String stockCenterName,
			List<Drug> drugList, Date theStartDate, Date theEndDate) {
		super(parent);
		this.stockCenterName = stockCenterName;
		this.drugList = drugList;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {
		final List<String[]> theStringList = new ArrayList<String[]>();
		theStringList.addAll(getARVDrugUsagePerDay());

		// print the header
		theStringList.add(0, new String[] { "date", "drug1", "drug2", "drug3",
				"drug4", "drug5", "drug6", "drug7", "drug8", "drug9", "drug10",
		"drug11" });

		// take off the last row - this is the totals, passed as parameters
		// later
		totalString = theStringList.get(theStringList.size() - 1);
		theStringList.remove(theStringList.size() - 1);

		csvFile = createCSVFile("tmpARVDrugUsage.csv", theStringList, true);
	}

	@Override
	protected Object getDataSource() throws ReportException {
		try {
			JRCsvDataSource jcvs = new JRCsvDataSource(new File("Reports"
					+ java.io.File.separator + csvFile.getName()));
			jcvs.setUseFirstRowAsHeader(true);
			return jcvs;
		} catch (Exception e) {
			throw new ReportException("Error getting data source", e);
		}
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("path", getReportPath());
		map.put("facilityName", LocalObjects.pharmacy.getPharmacyName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());

		Iterator it2 = drugList.iterator();
		int count = 1;
		while (it2.hasNext()) {
			map.put("drug" + count + "Name", ((Drug) it2.next()).getName());
			map.put("drug" + count + "Count", totalString[count]);
			count++;
		}
		return map;
	}

	@Override
	protected String getReportFileName() {
		return "ARVDrugUsage";
	}

	public List<String[]> getARVDrugUsagePerDay() throws HibernateException {
		List<String[]> theReturnList = new ArrayList<String[]>();

		Date theDate = startDate;
		String[] usageArray = new String[12];
		int[] totalArray = new int[11]; // totals in pills

		Integer stockCenterId = (Integer) hSession
		.createQuery(
		"select id from StockCenter sc where sc.stockCenterName = :name")
		.setString("name", stockCenterName).uniqueResult();

		while (!theDate.after(endDate)) {
			// write the date as the first string in the array
			usageArray[0] = (new SimpleDateFormat("dd MMM yy")).format(theDate);

			// write the totals for each drug in the drug list
			Iterator<Drug> it = drugList.iterator();

			for (int i = 1; i < 12; i++) {
				if (it.hasNext()) {
					Drug theDrug = it.next();

					Integer[] dispQuantities = getTotalForDrugForDay(theDrug,
							theDate, stockCenterId);
					usageArray[i] = (dispQuantities[1] == 0 ? ""
							+ dispQuantities[0] : "" + dispQuantities[0] + " ("
							+ dispQuantities[1] + ")");

					totalArray[i - 1] = totalArray[i - 1]
					                               + (dispQuantities[0] * theDrug.getPackSize())
					                               + dispQuantities[1];

				}
			}

			// increment the date by 1 day
			Calendar cal = Calendar.getInstance();
			cal.setTime(theDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			theDate.setTime(cal.getTimeInMillis());

			theReturnList.add(usageArray);
			usageArray = new String[12];

		}

		// add the total strings to the end of the string list

		usageArray = new String[12];
		usageArray[0] = "";

		Iterator<Drug> it2 = drugList.iterator();
		int count = 0;

		while (it2.hasNext()) {
			Drug theDrug = it2.next();
			int fullPacks = totalArray[count] / theDrug.getPackSize();
			int loosePills = totalArray[count] % theDrug.getPackSize();
			usageArray[count + 1] = loosePills == 0 ? "" + fullPacks : ""
				+ fullPacks + " (" + loosePills + ")";
			count++;
		}

		theReturnList.add(usageArray);
		return theReturnList;
	}

	public Integer[] getTotalForDrugForDay(Drug theDrug, Date theDate,
			Integer stockCenterId)
	throws HibernateException {
		Integer[] totalDispensedInteger = new Integer[2];
		Calendar cal = Calendar.getInstance();
		cal.setTime(theDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		Date startDate = cal.getTime();

		cal.add(Calendar.DAY_OF_MONTH, 1);

		Date endDate = cal.getTime();

		Long sum = ((Long) hSession
				.createQuery(
						"select sum(pd.amount) "
						+ "from PackagedDrugs pd where pd.stock.drug = :drug "
						+ "and pd.parentPackage.packDate between :startDate and :endDate "
						+ "and pd.parentPackage.prescription is not null "
						+ "and pd.stock.stockCenter = :stockCenter")
						.setInteger("drug", theDrug.getId()).setInteger("stockCenter",
								stockCenterId).setDate("startDate", startDate).setDate(
										"endDate", endDate).uniqueResult());

		if (sum == null) {
			totalDispensedInteger[0] = 0;
			totalDispensedInteger[1] = 0;

		} else {
			int totalDrugsPackaged = sum.intValue();

			int fullContainersDispensed = totalDrugsPackaged
			/ theDrug.getPackSize();
			int loosePillsDispensed = totalDrugsPackaged
			% theDrug.getPackSize();

			if (loosePillsDispensed == 0) {
				totalDispensedInteger[0] = fullContainersDispensed;
				totalDispensedInteger[1] = 0;
			} else {
				totalDispensedInteger[0] = fullContainersDispensed;
				totalDispensedInteger[1] = loosePillsDispensed;
			}

		}

		return totalDispensedInteger;
	}

}
