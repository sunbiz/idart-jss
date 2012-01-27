package model.manager.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import model.manager.PrescriptionManager;
import model.manager.excel.conversion.exceptions.ReportException;
import model.nonPersistent.AggregateRegimenInfo;
import model.nonPersistent.RegimenInfo;

import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class DrugCombinationReport extends AbstractJasperReport {

	private final Clinic clinic;
	private final int lowerAgeBoundry;
	private final int upperAgeBoundry;
	private String filename;
	private final Date endDate;
	private final Date startDate;

	public DrugCombinationReport(Shell parent, Date theStartDate,
			Date theEndDate, Clinic clinic, int lowerAgeBoundry,
			int upperAgeBoundry, String filename) {
		super(parent);
		this.clinic = clinic;
		this.lowerAgeBoundry = lowerAgeBoundry;
		this.upperAgeBoundry = upperAgeBoundry;
		this.filename = filename;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {
		List<Prescription> scripts = PrescriptionManager.getValidPrescriptions(
				hSession, clinic.getId(), false, startDate, endDate, null, false);

		List<AggregateRegimenInfo> regInfos = getAggregateDrugCombos(scripts);

		FileWriter out = null;
		try {
			if (filename.lastIndexOf(".") == -1) {
				filename += ".csv";
			} else if (!filename.endsWith(".csv")) {
				filename = filename.substring(0, filename.lastIndexOf("."))
				+ ".csv";
			}

			File csvFile = new File(filename);
			out = new FileWriter(csvFile);

			out.write("iDART Drug Combinations\n");
			out.write("This report shows all patients who are on treatment (i.e. they have a valid prescription) during the time period specified. \n" +
					"Please note that the patients may not have received all of the drugs that have been prescribed!\n");
			out.write("-----------------------\n");
			out.write("\nClinic: " + clinic.getClinicName());
			out.write("\nStart date: " + iDARTUtil.format(startDate));
			out.write("\nEnd date: " + iDARTUtil.format(endDate) + "\n\n");

			out.write("\n\n\n");
			RegimenInfo.END_DATE = AggregateRegimenInfo.END_DATE;
			out.write(RegimenInfo.getHeaderString());
			for (AggregateRegimenInfo regimenInfo : regInfos) {
				Set<RegimenInfo> regimenInfos = regimenInfo
				.getRegimenInfos(hSession);
				for (RegimenInfo info : regimenInfos) {
					out.write(info.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		return null;
	}

	@Override
	protected String getReportFileName() {
		return null;
	}

	@Override
	public void viewReport() {
		MessageDialog.openInformation(parent, "CSV Report complete",
				"Report save to " + filename);
	}

	@Override
	public void fillReport(IProgressMonitor monitor) throws ReportException {
		monitor.worked(20);
		monitor.setTaskName("Generating report");
		generateData();
		monitor.done();
	}

	private List<AggregateRegimenInfo> getAggregateDrugCombos(
			List<Prescription> scripts) {
		Map<Integer, AggregateRegimenInfo> regMap = new HashMap<Integer, AggregateRegimenInfo>();
		AggregateRegimenInfo.LOWER_AGE = lowerAgeBoundry;
		AggregateRegimenInfo.UPPER_AGE = upperAgeBoundry;
		AggregateRegimenInfo.START_DATE = startDate;
		AggregateRegimenInfo.END_DATE = endDate;

		for (Prescription script : scripts) {
			Integer hashCode = script.getARVDrugSet().hashCode();
			if (regMap.containsKey(hashCode)) {
				regMap.get(hashCode).addScript(script);
			} else {
				regMap.put(hashCode, new AggregateRegimenInfo(script));
			}
		}

		List<AggregateRegimenInfo> regInfos = new ArrayList<AggregateRegimenInfo>();
		regInfos.addAll(regMap.values());
		Map<Regimen, Set<Integer>> regimenIdMap = PrescriptionManager
		.getRegimenIdMap(hSession);
		for (AggregateRegimenInfo info : regInfos) {
			Set<Integer> drugIdSet = info.getDrugIdSet();
			for (Entry<Regimen, Set<Integer>> entry : regimenIdMap.entrySet()) {
				if (drugIdSet.containsAll(entry.getValue())) {
					Regimen theReg = entry.getKey();
					info.addRegimen(theReg.getDrugGroup());
				}
			}
		}

		return regInfos;
	}
}
