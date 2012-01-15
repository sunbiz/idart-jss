package model.manager.exports.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import model.manager.AdministrationManager;
import model.manager.excel.interfaces.GenerateExcelReportInterface;
import model.manager.excel.reports.out.DrugDispensedReport;
import model.manager.exports.DataExportFunctions;
import model.nonPersistent.EntitySet;

import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Query;

public class RowPerPackageExcelExporter extends ExcelExporter {

	@Override
	protected GenerateExcelReportInterface getExcelReport(
			ExcelReportObject report) {
		 return new DrugDispensedReport(report.getPath(), report);
	}
	
	@Override
	public EntitySet getPatientSet(ExcelReportObject report) {
		if (patientPackageMap == null) {
			populatePatientPackageMap(report);
		}
		return new EntitySet(new ArrayList<Integer>(patientPackageMap.keySet()));
	}

	private void populatePatientPackageMap(ExcelReportObject report) {
		session = HibernateUtil.getNewSession();
		StockCenter stockCenter = AdministrationManager.getStockCenter(session,
				report.getPharmacy());
		if (stockCenter == null)
			stockCenter = AdministrationManager
					.getPreferredStockCenter(session);

		String queryString = "select pack.prescription.patient.id, pack.id from Packages pack"
				+ " join pack.packagedDrugs drug"
				+ " where pack.pickupDate is not null and pack.packDate between :startDate and :endDate"
				+ " and drug.stock.stockCenter = :stockCenter"
				+ " order by pack.packDate asc, pack.clinic asc";
		Query query = session.createQuery(queryString);
		query.setTimestamp("startDate", report.getStartDate());
		query.setTimestamp("endDate", report.getEndDate());
		query.setParameter("stockCenter", stockCenter);
		List list = query.list();

		patientPackageMap = new LinkedHashMap<Integer, List<Integer>>();
		for (Object object : list) {
			Object[] ar = (Object[]) object;
			Integer patid = (Integer) ar[0];
			Integer packid = (Integer) ar[1];
			if (patientPackageMap.containsKey(patid)) {
				List<Integer> packlist = patientPackageMap.get(patid);
				if (!packlist.contains(packid))
					packlist.add(packid);
			} else {
				ArrayList<Integer> packIds = new ArrayList<Integer>();
				packIds.add(packid);
				patientPackageMap.put(patid, packIds);
			}
		}
	}
	
	@Override
	protected void exportPage(EntitySet pagedEntitySet, DataExportFunctions functions, GenerateExcelReportInterface excelReport) throws Exception {
		functions.setPatientSet(pagedEntitySet);
		for (Integer patientId : pagedEntitySet.getEntityIds()) {
			functions.setPatientId(patientId);
			List<Integer> packageIds = patientPackageMap
					.get(patientId);
			for (Integer packageId : packageIds) {
				functions.setPackageId(packageId);
				excelReport.writeRow(functions);
			}
		}
	}
}
