package model.nonPersistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.DrugManager;
import model.manager.PatientSetManager;

import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.Prescription;
import org.hibernate.Session;

public class AggregateRegimenInfo {

	public static Date START_DATE;
	public static int UPPER_AGE;
	public static int LOWER_AGE;
	public static Date END_DATE;

	private String drugListString;
	private Date prescriptionDate;
	private final Set<Integer> drugIdSet;
	private final Set<Integer> patientIdSet;
	private HashSet<String> regimens;

	/**
	 * 0 - M < LOWER_AGE <br/>
	 * 1 - M < UPPER_AGE <br/>
	 * 2 - M >= UPPER_AGE <br/>
	 * 3 - F < LOWER_AGE <br/>
	 * 4 - F < UPPER_AGE <br/>
	 * 5 - F >= UPPER_AG <br/>
	 * 6 - U < LOWER_AGE <br/>
	 * 7 - U < UPPER_AGE <br/>
	 * 8 - U >= UPPER_AG <br/>
	 * 9 - F < LOWER_AGE (pregnant) <br/>
	 * 10 - F < UPPER_AGE (pregnant) <br/>
	 * 11 - F >= UPPER_AG (pregnant) <br/>
	 */
	private final int[] stats = new int[12];

	public AggregateRegimenInfo(Prescription script) {
		drugIdSet = new HashSet<Integer>();
		patientIdSet = new HashSet<Integer>();
		Set<Drug> drugSet = script.getARVDrugSet();
		if (!drugSet.isEmpty()) {
			List<Drug> drugList = new ArrayList<Drug>();
			drugList.addAll(drugSet);
			Collections.sort(drugList);
			drugListString = DrugManager.getDrugListString(drugList, " + ", true);
			for (Drug drug : drugList) {
				drugIdSet.add(drug.getId());
			}
		}
		prescriptionDate = script.getDate();
		addScript(script);
	}

	public void addScript(Prescription script) {
		Patient patient = script.getPatient();
		patientIdSet.add(patient.getId());
		int age = patient.getAgeAt(END_DATE);
		switch (patient.getSex()) {
		case 'M':
			updateStats(0, age);
			break;
		case 'F':
			updateStats(3, age);
			if (patient.isPregnantAtDate(END_DATE)) {
				updateStats(9, age);
			}
			break;
		default:
			updateStats(6, age);
		}
	}

	private void updateStats(int i, int age) {
		if (age < LOWER_AGE) {
			stats[i] += 1;
		}
		if (age < UPPER_AGE) {
			stats[i + 1] += 1;
		} else {
			stats[i + 2] += 1;
		}
	}

	public String getDrugListString() {
		return drugListString;
	}

	public Set<Integer> getDrugIdSet() {
		return drugIdSet;
	}

	public void addRegimen(String drugGroup) {
		if (regimens == null) {
			regimens = new HashSet<String>();
		}
		regimens.add(drugGroup);
	}

	private String getRegString() {
		if (regimens != null && regimens.size() > 0) {
			String regString = "";
			for (String reg : regimens) {
				regString += reg + ";";
			}
			regString = regString.substring(0, regString.length() - 1) + "";
			return regString;
		}
		return "";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRegString());
		sb.append(",");
		sb.append(drugListString);
		sb.append(",");
		for (int i = 0; i < stats.length; i++) {
			sb.append(stats[i]);
			sb.append(",");
		}
		sb.append(stats[1] + stats[2]); // total M
		sb.append(",");
		sb.append(stats[4] + stats[5]); // total F
		sb.append(",");
		sb.append(stats[7] + stats[8]); // total U
		sb.append("\n");
		return sb.toString();
	}

	public static String getHeaderString(){
		String[] headers = getHeaders();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < headers.length; i++) {
			sb.append(headers[i]);
			if (i< headers.length-1) {
				sb.append(",");
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	public static String[] getHeaders() {
		return new String[] { "Regimen", "Drug Combo",
				"M (< " + LOWER_AGE + ")",
				"M (< " + UPPER_AGE + ")", "M (>= " + UPPER_AGE + ")",
				"F (< " + LOWER_AGE + ")", "F (< " + UPPER_AGE + ")",
				"F (>= " + UPPER_AGE + ")", "U (< " + LOWER_AGE + ")",
				"U (< " + UPPER_AGE + ")", "U (>= " + UPPER_AGE + ")",
				"Pregnant (< " + LOWER_AGE + ")",
				"Pregnant (< " + UPPER_AGE + ")",
				"Pregnant (>= " + UPPER_AGE + ")", "Total M", "Total F",
				"Total U", };
	}

	public Set<RegimenInfo> getRegimenInfos(Session session){
		List<Patient> patients = PatientSetManager.getPatientsInSet(session, patientIdSet);
		Set<RegimenInfo> infos = new HashSet<RegimenInfo>();
		for (Patient patient : patients) {
			RegimenInfo info = new RegimenInfo();
			info.setPatientId(patient.getPatientId());
			info.setFirstName(patient.getFirstNames());
			info.setLastName(patient.getLastname());
			info.setDob(patient.getDateOfBirth());
			int age = patient.getAgeAt(END_DATE);
			info.setAge(age);
			info.setAgeCategory(getAgeCategory(age));
			info.setPrescriptionDate(getPrescriptionDate());
			info.setSex(patient.getSex());
			info.setDrugListString(drugListString);
			info.setRegimens(getRegString());
			info.setPregnant(patient.isPregnantAtDate(END_DATE));
			info.setEpisodeStartReason(patient.getEpisdodeStartReasonInPeriod(
					START_DATE, END_DATE));
			infos.add(info);
		}
		return infos;
	}

	private String getAgeCategory(int age) {
		if (age < LOWER_AGE)
			return "<" + LOWER_AGE;
		else if (age < UPPER_AGE)
			return LOWER_AGE + "-" + UPPER_AGE;
		else
			return ">" + UPPER_AGE;
	}

	/**
	 * @return the prescriptionDate
	 */
	public Date getPrescriptionDate() {
		return prescriptionDate;
	}

	/**
	 * @param prescriptionDate the prescriptionDate to set
	 */
	public void setPrescriptionDate(Date prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}
	
	
}
