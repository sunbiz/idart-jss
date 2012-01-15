package model.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.sms.SmsType;
import org.hibernate.Session;

public class SmsManager {
	
	/**
	 * * This method gets the active patients on a campaign with an appointment in
	 * a number of days time 
	 * @param session
	 * @param noOfDays
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<StudyParticipant> getContactsExpected(
			Session session, int noOfDays) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, noOfDays);
		Date appDate = cal.getTime();
		
		List<StudyParticipant> participants = session
				.createQuery(
						"select sp from StudyParticipant sp, Patient p, Appointment a "
								+ "where p.id = sp.patient and p.id = a.patient and p.accountStatus = true "
								+ "and  date(a.appointmentDate) = :appDate "
								+ "and a.visitDate is null "
								+ "and sp.studyGroup = :activeGroup "
								+ "and sp.endDate is null")
				.setDate("appDate", appDate)
				.setString("activeGroup", StudyParticipant.GP_ACTIVE)
				.list();
		
		
		return participants;
	}

	/**
	 * * This method gets the active patients on a campaign who
	 * missed an appointment a number of days ago 
	 * @param session
	 * @param noOfDays
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<StudyParticipant> getContactsDefaulted(
			Session session, int noOfDays) {

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -noOfDays);
		Date appDate = cal.getTime();
		
		List<StudyParticipant> participants = session
				.createQuery(
						"select sp from StudyParticipant sp, Patient p, Appointment a "
								+ "where p.id = sp.patient and p.id = a.patient and p.accountStatus = true "
								+ "and date(a.appointmentDate) = :appDate "
								+ "and a.visitDate is null "
								+ "and sp.studyGroup = :activeGroup "
								+ "and sp.endDate is null")
				.setDate("appDate", appDate)
				.setString("activeGroup", StudyParticipant.GP_ACTIVE)
				.list();
		
		return participants;
	}

	/** Checks if the specific messages have already been scheduled today
	 * 
	 * @param session
	 * @param days
	 * @return
	 */
	public static boolean checkIfScheduled(Session session, int days, SmsType messageType) {
		
		@SuppressWarnings("unchecked")
		List<MessageSchedule> result = session.createQuery("select ms from MessageSchedule ms " +
				"where date(ms.scheduleDate) = current_date and ms.daysToSchedule = :days " +
				"and ms.messageType = :messageType").setInteger("days", days)
				.setParameter("messageType", messageType).list();
		
		
		if( result == null || result.isEmpty()) 
			return false;
		
		return true;
		
	}
	
	/**
	 * Get messages which were scheduled to be sent but not received by Mobiliser
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<MessageSchedule> getUnsuccessfulMessagesToday(Session session) {
		
		List<MessageSchedule> messages = new ArrayList<MessageSchedule>();
		
		messages = session.createQuery("select ms from MessageSchedule ms " + 
				"where current_date = date(ms.scheduleDate) and ms.scheduledSuccessfully = false ").list();
		
		return messages;
		
	}
	
	/**
	 * Get messages which were scheduled to be sent yesterday but not received by Mobiliser   
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<MessageSchedule> getFailedMessages(Session session) {
		
		List<MessageSchedule> messages = new ArrayList<MessageSchedule>();
		
		messages = session.createQuery("select ms from MessageSchedule ms " + 
				"where current_date - date(ms.scheduleDate) > 0 and ms.scheduledSuccessfully = false " +
				"and ms.sentToAlerts = false").list();
		
		return messages;
		
	}
	
	/**
	 * This method updates the MessageSchedule so that these messages will not be scheduled again
	 */
	public static void updateMessageSchedule(Session session, MessageSchedule messageSchedule) {
		messageSchedule.setScheduledSuccessfully(true);
		session.save(messageSchedule);
	}
	
}

