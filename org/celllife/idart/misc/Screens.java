package org.celllife.idart.misc;

import org.celllife.idart.messages.Messages;


/**
 * Enum used for GUI testing to aid accessing screens
 * 
 * @author Simon Kelly
 *
 */
public enum Screens {
	
	LOGIN("login.screen.title"),
	PHARMACY_WELCOME(LOGIN, null, "welcome.screen.name"),
	GENERAL_ADMIN(PHARMACY_WELCOME, "btnGeneralAdmin", "GeneralAdmin.title.update"),
	PATIENT_ADMIN(PHARMACY_WELCOME, "btnPatientAdmin", "PatientAdmin.shell.title"),
	STOCK_CONTROL(PHARMACY_WELCOME, "btnStockControl","StockControl.shell.title"),
	REPORTS(PHARMACY_WELCOME, "btnReports", "NewReports.shell.title"),
	ADD_PATIENT(PATIENT_ADMIN, "btnAddPatient", "patient.screen.title.add"),
	UPDATE_PATIENT(PATIENT_ADMIN, "btnUpdatePatient", "patient.screen.title.update"),
	UPDATE_PRESCRIPTION(PATIENT_ADMIN, "btnUpdatePrescription", "addPrescription.title"),
	PATIENT_HISTORY_REPORT(PATIENT_ADMIN, "btnPatientHistory", "reports.patientHistory"),
	PATIENT_MERGE(PATIENT_ADMIN, "btnPatientMerge", "MergePatients.title"),
	PATIENT_VISITS(PATIENT_ADMIN, "btnVisits", "PatientStats.title");
	
	private Screens parent;
	private String accessButtonId;
	private final String shellTitleKey;
	
	private Screens(String shellTitleKey){
		this.shellTitleKey = shellTitleKey;
	}
	
	private Screens(Screens parent, String accessButtonId, String shellTitleKey) {
		this.parent = parent;
		this.accessButtonId = accessButtonId;
		this.shellTitleKey = shellTitleKey;
	}
	
	public Screens getParent() {
		return parent;
	}
	
	public String getAccessButtonId() {
		return accessButtonId;
	}
	
	public String getShellTitleKey() {
		return shellTitleKey;
	}
	
	public String getShellTitle(){
		return Messages.getString(shellTitleKey);
	}

}
