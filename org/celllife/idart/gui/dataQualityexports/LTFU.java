package org.celllife.idart.gui.dataQualityexports;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;

import java.text.SimpleDateFormat;
import java.util.*;
public class LTFU extends DataQualityBase {

	int num = 0;
	Date date = new Date ();
	public LTFU(int querynum){
		this.num = querynum;
	}
	
	private final String[] patientHeadings = new String[] { "Patient","Firstname","Lastname" };
		
	@SuppressWarnings("unchecked")
	@Override
	public void getData() {
		setHeadings(patientHeadings);
		//	Uncollected Packages 
		if(num == 1 ){
			data = HibernateUtil.getNewSession().createSQLQuery("select eps.patient, " +
					"pat.firstnames, " +
					"pat.lastname " +
					"from episode eps, " +
					"package pack, " +
					"prescription pres, " +
					"patient pat " +
					"where pack.prescription = pres.id " +
					"and pres.patient = pat.id " +
					"and eps.patient = pat.id " +
					"and eps.stopreason is null " +
					"and pack.pickupdate is null " +
					"and extract(epoch from (now()-pack.packdate)) > (120*24*60*60)").list();


	//Active Patients
		}else if (num == 2){
			data = HibernateUtil.getNewSession().createSQLQuery("select eps.patient, " +
																	   "pat.firstnames, " +
																	   "pat.lastname " +
																	   "from episode eps,package pack, prescription pres, patient pat " +
																	   "where pack.prescription = pres.id " +
																	   "and pres.patient = pat.id " +
																	   "and eps.patient = pat.id " +
																	   "and eps.stopreason is null " +
																	   "and extract(epoch from (now()-pack.pickupdate)) > (120*24*60*60) " +
																	   "ORDER BY pack.pickupdate ASC").list();



		} 
	}

	@Override
	public String getFileName() {
		String mess=null;
		if(num == 2){
			
			mess="Active Uncollected Packages -"+ new SimpleDateFormat("dd.MM.yyyy").format(date)+" - IDART(active uncollected packages)";	
		}else if(num == 1){
			mess="Uncollected Packages -"+ new SimpleDateFormat("dd.MM.yyyy").format(date)+" - IDART(uncollected packages)";	
			
		}
		return mess;
	}

	@Override
	public void performJob(IProgressMonitor monitor) throws ReportException {
		super.performJob(monitor);

	}

	@Override
	public String getMessage() {
		String message=null;
		if(num == 2){
			message="Active Patients who are likely LTFU(over 120 days)";	
		}else if(num == 1){
			message="Uncollected Packages(over 120 days)";
		}
		return message;
	}

}
