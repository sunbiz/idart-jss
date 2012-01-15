package org.celllife.idart.gui.dataQualityexports;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class UnusedPrescriptions extends DataQualityBase {

	private final String[] patientHeadings = new String[] {
			"Unused Prescriptions \n\nUnused prescriptions are prescriptions from which no drugs have been dispensed." +
			"\nThey should be deleted from the system as they affect some reports." +
			"\nTo delete these prescriptions use the 'Stock Prescription & Package Deletions' screen." +
			"\n\nFolder Number",
			"First Name", "Last Name", "Date of Birth", "Sex",
			"Date Unused Prescription was Created", "Prescription ID" };
	Date date = new Date();

	@SuppressWarnings("unchecked")
	@Override
	public void getData() {
		setHeadings(patientHeadings);

		data = HibernateUtil
				.getNewSession()
				.createSQLQuery(
						"select pat.patientid, "
								+ "pat.firstnames, "
								+ "pat.lastname, to_char(pat.dateofbirth, 'DD-Mon-YYYY' ) as dob, pat.sex, "
								+ "to_char(pres.date,'DD-Mon-YYYY' ) as date, pres.prescriptionid "
								+ "from prescription pres, patient pat "
								+ "where pres.id in (select pr.id from prescription pr "
								+ "EXCEPT "
								+ "select p.id "
								+ "from prescription p "
								+ "where p.id IN (select distinct prescription from package))"
								+ "and pres.patient = pat.id order by pat.patientid").list();
	}

	@Override
	public String getFileName() {
		return new SimpleDateFormat("dd.MM.yyyy").format(date) + " - iDART (unused prescriptions)" ;
	}

	@Override
	public String getMessage() {
		return "Unused Prescriptions";
	}

}
