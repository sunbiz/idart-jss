package org.celllife.idart.gui.dataQualityexports;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;

import java.text.SimpleDateFormat;
import java.util.*;
public class InaccurateEpisodes extends DataQualityBase{

	int num ;
	private final String[] newpatientHeadings = new String[] { "PATIENT","FIRSTNAME","LASTNAME" };
	private final String[] dpatientHeadings = new String[] { "Multiple New Patient Episodes\n\n"+
															 "The following patients have more than one episodes marked as 'New Patient'\n"+
															 "It's important that patients only have 1 'New Patient' episode (ror reporting purposes).\n"+
															 "To fix this go to 'Update Existing Patient' and select 'View or Edit All Previous Episodes '"+
															 "\n\nFolder Number","Firstname","Lastname","Date Of Birth","Sex" };
	
	private final String[] deadpatientHeadings = new String[] { "Multiple Deceased Episodes\n\n" +
																"The following patients have more than one episodes marked as 'Deceased' \n"+
																"It's important that patients only have 1 'Deceased' episode (for reporting purposes).\n"+
																"To fix this go to 'Update Existing Patient' and select 'View or Edit All Previous Episodes '"+
																"\n\nFolder Number","Firstname","Lastname","Date Of Birth","Sex" };
	
	private final String[] overlappingHeadings = new String[] { "Startdate","Stopdate","Folder Number","Firstname","Lastname" };
	private final String[] inconsistentStartStopHeadings = new String[] { "StartDate","StopDate","StartReason","StopReason","Folder Number","Firstname","Lastname" };
    Date date = new Date();
	public InaccurateEpisodes(int num){
		this.num = num;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void getData(){
		setHeadings(dpatientHeadings);
		if(num == 1){
			
			data = HibernateUtil.getNewSession().createSQLQuery("select	pat.patientid, " +
																"pat.firstnames, " +
																"pat.lastname, " +
																"to_char(pat.dateofbirth, 'DD-Mon-YYYY' ), "+
															    "pat.sex "+
																"from episode a,patient pat, (select patient," +
																									"count(patient) as pcount " +
																									"from episode " +
																									"where startreason = 'New Patient' " +
																									"GROUP BY patient) as episodes " +
																"where a.patient = episodes.patient " +
																"and pat.id = episodes.patient " +
																"and episodes.pcount >1 " +
																"GROUP BY pat.patientid,episodes.pcount,pat.firstnames,pat.lastname,pat.dateofbirth,pat.sex").list();

			
		}else if (num == 2){
			setHeadings(deadpatientHeadings);
			data = HibernateUtil.getNewSession().createSQLQuery("select pat.patientid, " +
																	   "pat.firstnames,	" +
																	   "pat.lastname, " +
																	   "to_char(pat.dateofbirth, 'DD-Mon-YYYY' ), "+
																       "pat.sex "+
																"from episode a, patient pat, (select patient, " +
																									 "count(patient) as pcount " +
																									 "from episode " +
																									 "where stopreason = 'Deceased' " +
																									 "GROUP BY patient) as episodes " +
																"where a.patient = episodes.patient " +
																"and pat.id = episodes.patient " +
																"and episodes.pcount >1 " +
																"GROUP BY pat.patientid,episodes.pcount,pat.firstnames,pat.lastname,pat.dateofbirth,pat.sex").list();


		}else if(num == 3){
			setHeadings(overlappingHeadings);
			data = HibernateUtil.getNewSession().createSQLQuery("select A.STARTDATE AS astart, " +
																	   "a.STOPDATE as astop, " +
																	   "pat.patientid, " +
																	   "pat.firstnames, " +
																	   "pat.lastname " +
																	   "FROM EPISODE A, EPISODE B, patient pat " +
																	   "where a.stopdate is not null " +
																	   "and pat.id = a.patient " +
																	   "and b.startdate < a.stopdate " +
																	   "and a.patient = b.patient " +
																	   "and a.index < b.index").list();


		}else if(num == 4){
			setHeadings(newpatientHeadings);
			data = HibernateUtil.getNewSession().createSQLQuery("select pat.patientid, " +
																	   "pat.firstnames,	" +
																	   "pat.lastname, " +
																	   "episodes.pcount " +
																	   "from episode a,PATIENT pat, (select patient, " +
																	   									   "count(patient) as pcount " +
																	   									   "from episode " +
																	   									   "where stopdate is null " +
																	   									   "GROUP BY patient) as episodes " +
																	   "where a.patient = episodes.patient " +
																	   "and pat.id = a.patient " +
																	   "and episodes.pcount >1 " +
																	   "GROUP BY pat.patientid,episodes.pcount,pat.firstnames,pat.lastname").list();


		}else if(num == 5){
			setHeadings(inconsistentStartStopHeadings);
			data = HibernateUtil.getNewSession().createQuery("select ep.startDate, " +
																	   "ep.stopDate, " +
																	   "ep.startReason, " +
																	   "ep.stopReason, " +
																	   "ep.patient.patientId, " +
																	   "ep.patient.firstNames, "+
																	   "ep.patient.lastname "+
																	   "from Episode ep " +
																	   "where startReason not in " +
																	   "(select value from SimpleDomain where name ='activation_reason' )" +
																	    " OR " +
																	    "(stopReason not in (select value from SimpleDomain where name ='deactivation_reason' ) " +
																	    "and stopReason is not null )").list();


		}
		
	}
	

	@Override
	public String getFileName() {
		String mess=null;
		if(num == 1){
			mess= new SimpleDateFormat("dd.MM.yyyy").format(date)+" - iDART (Episodes - Multiple New Patients)";	
		}else if(num == 2){
			mess= new SimpleDateFormat("dd.MM.yyyy").format(date)+" - iDART(Episodes - Multiple Deceased )";
		}else if(num == 3){
			mess="Inacccurate Overlapping Episodes -"+ new SimpleDateFormat("dd.MM.yyyy").format(date)+" - IDART(inacccurate overlapping episodes)";
		}else if(num == 4){
			mess="Inacccurate Open Episodes -"+ new SimpleDateFormat("dd.MM.yyyy").format(date)+" - IDART(inacccurate open episodes)";
			
		}else if(num == 5){
			mess="Inconsistent Start Stop Episodes -"+ new SimpleDateFormat("dd.MM.yyyy").format(date)+" - IDART(inconsistent start stop episodes)";
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
		if(num == 1){
			message="Episodes - Multiple 'New Patient'";	
		}else if(num == 2){
			message="Episodes - Multiple 'Deceased'";
		}else if(num == 3){
			message="Episodes - Overlapping in time";
		}else if(num == 4){
			message="Multiple Open Episodes";
		}else if(num == 5){
			message="Inconsistent Start an Stop Reasons";
		}
		return message;
	}


}

