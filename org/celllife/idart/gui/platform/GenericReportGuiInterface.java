package org.celllife.idart.gui.platform;

import org.celllife.idart.messages.Messages;


/**
 */
public interface GenericReportGuiInterface {

	static String OPTION_reportType = "reportType"; //$NON-NLS-1$
	static String OPTION_packageStage = "packageStage"; //$NON-NLS-1$

	static int REPORTTYPE_PATIENT = 1, REPORTTYPE_CLINICMANAGEMENT = 2,
	REPORTTYPE_STOCK = 3, REPORTTYPE_MONITORINGANDEVALUATION = 4;

	static String REPORT_PATIENT_HISTORY = Messages.getString("reports.patientHistory");  //$NON-NLS-1$
	static String REPORT_PEPFAR = Messages.getString("reports.pepfar"); //$NON-NLS-1$
	static String REPORT_MISSED_APPOINTMENTS = Messages.getString("reports.missedAppointments");  //$NON-NLS-1$
	static String REPORT_DAILY_DISPENSING_TOTALS = Messages.getString("reports.dailyDispensingTotals");  //$NON-NLS-1$
	static String REPORT_TRANSACTION_LOG = Messages.getString("reports.transactionLog");  //$NON-NLS-1$
	static String REPORT_PRESCRIBING_DOCTORS = Messages.getString("reports.prescribingDoctors");  //$NON-NLS-1$
	static String REPORT_PACKAGE_TRACKING = Messages.getString("reports.pacakgeTracking"); //$NON-NLS-1$
	static String REPORT_MONTHLY_STOCK_RECEIPTS = Messages.getString("reports.monthlyStockReceipts");  //$NON-NLS-1$
	static String REPORT_CLINIC_INDICATORS = Messages.getString("reports.clinicIndicators");  //$NON-NLS-1$
	static String REPORT_PATIENTS_EXPECTED_ON_A_DAY = Messages.getString("reports.patientsExpectedOnADay");  //$NON-NLS-1$
	static String REPORT_ARV_DRUG_USAGE = Messages.getString("reports.ARVDrugUsage");  //$NON-NLS-1$
	static String REPORT_DRUGS_DISPENSED = Messages.getString("reports.drugsDispensed");  //$NON-NLS-1$
	static String REPORT_MONTHLY_RECEIPT_ISSUE = Messages.getString("reports.montlyIssuesAndReceipts");  //$NON-NLS-1$
	static String REPORT_STOCK_TAKE = Messages.getString("reports.stockTake");  //$NON-NLS-1$
	static String REPORT_PACKAGES_CREATED = Messages.getString("reports.packagesCreated");  //$NON-NLS-1$
	static String REPORT_PACKAGES_LEAVING = Messages.getString("reports.packageLeaving");  //$NON-NLS-1$
	static String REPORT_PACKAGES_RECEIVED = Messages.getString("reports.packagesReceivedAtClinic");  //$NON-NLS-1$
	static String REPORT_PACKAGES_RETURNED = Messages.getString("reports.packagesReturned");  //$NON-NLS-1$
	static String REPORT_PACKAGES_COLLECTED = Messages.getString("reports.packagesCollected");  //$NON-NLS-1$
	static String REPORT_PACKAGES_AWAITING_PICKUP = Messages.getString("reports.packagesAwaiting");  //$NON-NLS-1$
	static String REPORT_PACKAGES_SCANNED_OUT = Messages.getString("reports.packagesScannedOut");  //$NON-NLS-1$
	static String REPORT_EPISODES_STARTED_OR_ENDED = Messages.getString("reports.episodesStartedAndEnded");  //$NON-NLS-1$
	static String REPORT_EPISODES_STATS = Messages.getString("reports.episodeStatistics");  //$NON-NLS-1$
	static String REPORT_DRUG_COMBINATIONS = Messages.getString("reports.drugCombinations");  //$NON-NLS-1$
	static String REPORT_COHORT_COLLECTIONS = Messages.getString("reports.cohortCollections");  //$NON-NLS-1$
}
