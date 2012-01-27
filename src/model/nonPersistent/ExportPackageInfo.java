package model.nonPersistent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.exports.columns.PackageDetailsEnum;

public class ExportPackageInfo {

	private Date dateCollected;
	private Integer patientID;
	private String packageID;
	private List<ExportDrugInfo> lstExportDrugInfo;
	private String regimen;
	private String drugGroupName;
	private Date dispenseDate;
	private Date expectedRunoutDate;

	public ExportPackageInfo() {
	}

	public ExportPackageInfo(Date dateCollected, Integer patientID,
			String packageID, List<ExportDrugInfo> lstExportDrugInfo) {
		this.dateCollected = dateCollected;
		this.patientID = patientID;
		this.packageID = packageID;
		this.lstExportDrugInfo = lstExportDrugInfo;
	}

	/**
	 * @return the dateCollected
	 */
	public Date getDateCollected() {
		return dateCollected;
	}

	/**
	 * @param dateCollected
	 *            the dateCollected to set
	 */
	public void setDateCollected(Date dateCollected) {
		this.dateCollected = dateCollected;
	}

	/**
	 * @return the patientID
	 */
	public Integer getPatientID() {
		return patientID;
	}

	/**
	 * @param patientID
	 *            the patientID to set
	 */
	public void setPatientID(Integer patientID) {
		this.patientID = patientID;
	}

	/**
	 * @return the lstExportDrugInfo
	 */
	public List<ExportDrugInfo> getListOfExportDrugInfo() {
		if (lstExportDrugInfo == null) {
			setListOfExportDrugInfo(new ArrayList<ExportDrugInfo>());
		}
		return lstExportDrugInfo;
	}

	/**
	 * @param lstExportDrugInfo
	 *            the lstExportDrugInfo to set
	 */
	public void setListOfExportDrugInfo(List<ExportDrugInfo> lstExportDrugInfo) {
		this.lstExportDrugInfo = lstExportDrugInfo;
	}

	/**
	 * @return the packageID
	 */
	public String getPackageID() {
		return packageID;
	}

	/**
	 * @param packageID
	 *            the packageID to set
	 */
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}

	@Override
	public String toString() {
		String tmp = "ExportPackageDrug";
		tmp += "\nDateCollected: " + dateCollected.toString();
		tmp += "\nPatientID: " + patientID.toString();
		tmp += "\nPackage ID: " + packageID;
		for (ExportDrugInfo edi : lstExportDrugInfo) {
			tmp += "\nExportDrugInfo: " + edi.toString();
		}
		return tmp;
	}

	public Set<Integer> getARVDrugIdSet() {
		Set<Integer> arvIdSet = new HashSet<Integer>();
		for (ExportDrugInfo edi : getListOfExportDrugInfo()) {
			if (edi.isARV()) {
				arvIdSet.add(edi.getDrugId());
			}
		}
		return arvIdSet;
	}

	public void setRegimen(String regimen) {
		this.regimen = regimen;
	}

	public String getRegimen() {
		return regimen;
	}

	public void setDrugGroupName(String drugGroupName) {
		this.drugGroupName = drugGroupName;
	}

	public String getDrugGroupName() {
		return drugGroupName;
	}

	public List<ExportDrugInfo> getDrugsWithId(int drugId) {
		List<ExportDrugInfo> listOfExportDrugInfo = getListOfExportDrugInfo();
		List<ExportDrugInfo> exportDrugsWithDrug = new ArrayList<ExportDrugInfo>();
		for (ExportDrugInfo exportDrugInfo : listOfExportDrugInfo) {
			if (exportDrugInfo.getDrugId() == drugId)
				exportDrugsWithDrug.add(exportDrugInfo);
		}
		return exportDrugsWithDrug;
	}

	public Object getFieldValue(PackageDetailsEnum field){
		switch (field) {
		case COLLECTION_DATE:
			return getDateCollected();
		case COLLECTED_DRUGS:
			return getCollectedDrugs();
		case DRUG_GROUP_NAME:
			return getDrugGroupName();
		case PACKAGE_ID:
			return getPackageID();
		case REGIMEN:
			return getRegimen();
		case DATE_DISPENSED:
			return getDispenseDate();
		case EXPECTED_RUNOUT_DATE:
			return getExpectedRunoutDate();
		default:
			return null;
		}
	}

	public Date getDispenseDate() {
		return dispenseDate;
	}
	
	public void setDispenseDate(Date date){
		this.dispenseDate = date;
	}

	private String getCollectedDrugs() {
		List<ExportDrugInfo> drugsInPackageList = getListOfExportDrugInfo();
		String drugsInPackageString = "";

		for (ExportDrugInfo currentDrug : drugsInPackageList) {

			drugsInPackageString += currentDrug.getChemicalCompoundName()
					+ " (" + currentDrug.getQuantityDispensed();
			if (currentDrug.getAccumulatedDrugs() != 0) {
				drugsInPackageString += " + "
						+ currentDrug.getAccumulatedDrugs();
			}
			drugsInPackageString += "); ";
		}
		return drugsInPackageString.trim();
	}

	public Date getExpectedRunoutDate() {
		if (expectedRunoutDate == null){
			calculateRunoutDate();
		}
		return expectedRunoutDate;
	}

	private void calculateRunoutDate() {
		Date collectionDate = getDateCollected();
		if (collectionDate != null) {

			int mindaysLeft = Integer.MAX_VALUE;
			for (ExportDrugInfo edi : lstExportDrugInfo) {
				Double daysLeft = edi.getDaysTillRunout();
				if (daysLeft < mindaysLeft) {
					mindaysLeft = daysLeft.intValue();
				}
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(collectionDate);
			cal.add(Calendar.DAY_OF_MONTH, mindaysLeft);
			this.expectedRunoutDate = cal.getTime();
		}
	}
}
