package model.manager.reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.manager.PackageManager;
import model.manager.excel.conversion.exceptions.ReportException;
import net.sf.jasperreports.engine.data.JRCsvDataSource;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.model.utils.PackageLifeStage;
import org.eclipse.swt.widgets.Shell;

public class PackageProcessingReport extends AbstractJasperReport {

	private final Clinic clinic;
	private final PackageLifeStage lifeStage;
	private final Date startDate;
	private final Date endDate;
	private File csvFile;

	public PackageProcessingReport(Shell parent, Clinic clinic,
			Date theStartDate, Date theEndDate, PackageLifeStage lifeStage) {
		super(parent);
		this.clinic = clinic;
		this.lifeStage = lifeStage;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {
		final List<String[]> theStringList = new ArrayList<String[]>();
		theStringList.addAll(getPackageInfoStrings());
		csvFile = createCSVFile("tmpPackageProcessing.csv", theStringList, true);
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("headerTxt", lifeStage.getAction());
		map.put("dateName", lifeStage.getDateStringName());
		map.put("clinicName", clinic.getClinicName());
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("path", getReportPath());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		return map;
	}

	@Override
	protected String getReportFileName() {
		// Report to use
		if (lifeStage.equals(PackageLifeStage.PICKED_UP)
				|| lifeStage.equals(PackageLifeStage.SCANNED_OUT))
			return "packageCollections";
		else
			return "packageProcessing";
	}

	@Override
	public Object getDataSource() throws ReportException {
		try {
			JRCsvDataSource jcvs = new JRCsvDataSource(csvFile);
			jcvs.setUseFirstRowAsHeader(true);
			return jcvs;
		} catch (Exception e) {
			throw new ReportException("Error getting data source", e);
		}
	}

	private List<String[]> getPackageInfoStrings() {

		List<Packages> packageList = PackageManager.getPackagesProcessed(
				hSession, clinic, startDate, endDate, lifeStage);

		SimpleDateFormat packDateFormat = new SimpleDateFormat("dd/MM/yy");

		List<String[]> returnList = new ArrayList<String[]>();
		String[] header;

		if (lifeStage.equals(PackageLifeStage.PICKED_UP)
				|| lifeStage.equals(PackageLifeStage.SCANNED_OUT)) {
			header = new String[] { "index", "date",
					"patientInfo", "drugsAndQuantities", "prescriptionInfo",
					"packageId", };
			int counter = 1;
			String dateStr = "";
			String lastDateStr = "";
			for (Packages p : packageList) {
				Prescription pre = p.getPrescription();
				Patient pat = pre.getPatient();
				dateStr = packDateFormat.format(lifeStage.getPackageDate(p));

				if (!dateStr.equals(lastDateStr)) {
					lastDateStr = dateStr;
					counter = 1;
				} else {
					counter++;
				}

				String[] packageString = new String[7];
				packageString[0] = counter + "";
				packageString[1] = dateStr;
				packageString[2] = iDartProperties.patientNameOnReports ? pat
						.getPatientId()
						+ "\n"
						+ pat
						.getFirstNames()
						+ " "
						+ pat.getLastname()
						+ "\nDOB: "
						+ new SimpleDateFormat("dd/MM/yyyy ").format(pat
								.getDateOfBirth())
								+ "\n"
								+ iDARTUtil.getSexString(pat.getSex()) : pat
								.getPatientId();
				packageString[3] = PackageManager.getLongPackageContentsString(
						hSession, p);
				packageString[4] = "Prescribed By: "
					+ pre.getDoctor().getFullname()
					+ "\n("
					+ p.getPackageId()
					.charAt(p.getPackageId().length() - 1)
					+ " of "
					+ (pre.getDuration() >= 4 ? (pre.getDuration() / 4)
							+ " months)" : pre.getDuration() + " weeks)");
				packageString[5] = p.getPackageId();

								returnList.add(packageString);
			}

		} else {
			header = new String[] { "date", "packageid", "patientname",
			"patientid" };
			for (Packages p : packageList) {
				Patient pat = p.getPrescription().getPatient();
				String[] packageString = new String[4];
				packageString[0] = packDateFormat.format(lifeStage
						.getPackageDate(p));
				packageString[1] = p.getPackageId();
				packageString[2] = iDartProperties.patientNameOnReports ? pat
						.getLastname()
						+ ", " + pat.getFirstNames() : "";
						packageString[3] = pat.getPatientId();

						returnList.add(packageString);
			}

		}
		returnList.add(0, header);

		return returnList;
	}

}
