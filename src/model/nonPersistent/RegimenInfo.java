package model.nonPersistent;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.celllife.idart.misc.iDARTUtil;

public class RegimenInfo {

	public static Date END_DATE;

	private String patientId;
	private String firstName;
	private String lastName;
	private Date dob;
	private int age;
	private Date prescriptionDate;
	private String ageCategory;
	private char sex;
	private String drugListString;
	private String regimens;
	private boolean isPregnant;
	private String startReason;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");

	public RegimenInfo() {
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public char getSex() {
		return sex;
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

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getDrugListString() {
		return drugListString;
	}

	public void setDrugListString(String drugListString) {
		this.drugListString = drugListString;
	}

	public String getRegimens() {
		return regimens;
	}

	public void setRegimens(String regimens) {
		this.regimens = regimens;
	}

	public boolean isPregnant() {
		return isPregnant;
	}

	public void setPregnant(boolean isPregnant) {
		this.isPregnant = isPregnant;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(patientId);
		sb.append(",");
		sb.append(replaceSeperator(firstName));
		sb.append(",");
		sb.append(replaceSeperator(lastName));
		sb.append(",");
		sb.append(sex);
		sb.append(",");
		sb.append(iDARTUtil.format(dob));
		sb.append(",");
		sb.append(age);
		sb.append(",");
		sb.append("'" +ageCategory);
		sb.append(",");
		sb.append("'" +sdf.format(prescriptionDate));
		sb.append(",");
		sb.append(drugListString);
		sb.append(",");
		sb.append(regimens);
		sb.append(",");
		sb.append(startReason);
		sb.append(",");
		sb.append(isPregnant);
		sb.append("\n");
		return sb.toString();
	}

	public static String getHeaderString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PatientId");
		sb.append(",");
		sb.append("First Name(s)");
		sb.append(",");
		sb.append("Last Name");
		sb.append(",");
		sb.append("Sex");
		sb.append(",");
		sb.append("Date of Birth");
		sb.append(",");
		sb.append("Age on " + iDARTUtil.format(END_DATE));
		sb.append(",");
		sb.append("Age Category");
		sb.append(",");
		sb.append("Prescription Date");
		sb.append(",");
		sb.append("Drug Combination");
		sb.append(",");
		sb.append("Regimens");
		sb.append(",");
		sb.append("Episode start reason in period");
		sb.append(",");
		sb.append("Was pregnant on " + iDARTUtil.format(END_DATE));
		sb.append("\n");
		return sb.toString();
	}

	public String replaceSeperator(String str) {
		if (str == null)
			return "";
		return str.replace(',', ';').trim();
	}

	public void setAgeCategory(String ageCategory) {
		this.ageCategory = ageCategory;
	}

	public String getAgeCategory() {
		return ageCategory;
	}

	public void setEpisodeStartReason(String startReason) {
		this.startReason = startReason;
	}
}
