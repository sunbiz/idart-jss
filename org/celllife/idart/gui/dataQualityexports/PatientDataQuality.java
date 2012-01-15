package org.celllife.idart.gui.dataQualityexports;

import java.text.SimpleDateFormat;
import java.util.Date;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
public class PatientDataQuality extends DataQualityBase {

	private final String[] patientHeadings = new String[] { "Possible Patient Duplicates \n\n" +
			"This is a list of possible duplicate patients in the iDART system. It lists those patients who have the same first name AND last name.\n" +
			"These are only POSSIBLE duplicates as two people can have the same name!\n" +
			"If two of these patient records are in fact duplicates you can merge them using the 'Merge Duplicate Patient Records' screen.\n" +
			"Note that MERGING cannot be undone so be very sure they are in fact duplicate patients by checking their details in 'Update Existing Patient'.\n"+
			"\nFolder Number", "First Name", "Last Name", "Date Of Birth", "Sex", "Duplicate Count" };

	Date date = new Date();
	@SuppressWarnings("unchecked")
	@Override
	public void getData() {
		setHeadings(patientHeadings);

		data = HibernateUtil
				.getNewSession()
				.createSQLQuery(
						"select records.patientid, p.firstnames, "
								+ "p.lastname, to_char(p.dateofbirth, 'MM-Mon-YYYY'), "
								+ "p.sex, records.amount "
								+ "from patient p ,(select a.patientid, "
								+ "a.firstnames, "
								+ "a.lastname, "
								+ "count(a.patientid)  as amount "
								+ "from patient a,patient b "
								+ "where a.firstnames = b.firstnames	"
								+ "and a.lastname = b.lastname "
								+ "GROUP BY a.patientid,a.firstnames,a.lastname) as records "
								+ "where p.patientid = records.patientid "
								+ "and records.amount > 1 "
								+ "GROUP BY records.patientid, p.firstnames, p.lastname,records.amount, " 
								+ "p.dateofbirth, p.sex ORDER BY p.lastname, p.firstnames")
				.list();
	}

	@Override
	public String getFileName() {
		
		return new SimpleDateFormat("dd.MM.yyyy").format(date)+" - iDART (patient duplications)" ;

	}

	@Override
	public void performJob(IProgressMonitor monitor) throws ReportException {
		super.performJob(monitor);

	}

	@Override
	public String getMessage() {
		return "Patient Duplicates";
	}

}
