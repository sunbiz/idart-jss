package model.manager.exports.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import model.manager.excel.interfaces.GenerateExcelReportInterface;
import model.manager.excel.reports.out.CohortDrugCollectionsReport;
import model.manager.exports.DataExportFunctions;
import model.nonPersistent.EntitySet;

import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class RowPerPatientExcelExporter extends ExcelExporter {
	
	private final EntitySet patients;

	public RowPerPatientExcelExporter(EntitySet patients) {
		this.patients = patients;
	}

	@Override
	protected GenerateExcelReportInterface getExcelReport(
			ExcelReportObject report) {
		 return new CohortDrugCollectionsReport(report.getPath(), report);
	}
	
	@Override
	protected int getNumberOfExtraColumn() {
		int size = 0;
		for (List<Integer> packages : patientPackageMap.values()) {
			if (packages.size() > size) {
				size = packages.size();
			}
		}
		return size;
	}
	
	@Override
	protected void setupFunctions(DataExportFunctions functions,
			ExcelReportObject report) {
		functions.setAllPatients(false);
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

		Criteria critPackg = session.createCriteria(Packages.class, "package")
				.addOrder(Order.asc("package.packDate"))
				.add(Restrictions.isNotNull("package.prescription"));
		critPackg.createAlias("package.prescription", "prescription")
				.createAlias("prescription.patient", "patient");
		critPackg.add(Restrictions.isNotNull("package.pickupDate"))
				.add(Restrictions.in("patient.id", patients.getEntityIds()));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("patient.id"));
		projectionList.add(Projections.property("package.id"));
		critPackg.setProjection(projectionList);

		@SuppressWarnings("rawtypes")
		List list = critPackg.list();

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
			excelReport.writeRow(functions);
			for (int i = 0; i < packageIds.size(); i++) {
				functions.setPackageId(packageIds.get(i));
				excelReport.writeExtraColumns(functions, i);
			}
			excelReport.incrementRowCounter();
		}
	}
}
