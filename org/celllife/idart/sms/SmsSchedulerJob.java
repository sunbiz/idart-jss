package org.celllife.idart.sms;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import model.manager.AlertManager;
import model.manager.SmsManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SmsSchedulerJob extends CampaignSchedulingJob implements Job {

	private static final Logger log = Logger.getLogger(SmsSchedulerJob.class.getName());
	
	public static final String JOB_NAME = "smsJob";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("SMS job scheduling starting");

		Transaction tx = null;
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			tx = hSession.beginTransaction();

			// Firstly, we try to schedule new jobs

			// Appointment reminders
			if (PropertiesManager.sms().appointmentReminderValue1() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentReminderValue1();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER)) {
					scheduleMessages(hSession, noOfDays, 1, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER);
				}
			}
			
			if (PropertiesManager.sms().appointmentReminderValue2() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentReminderValue2();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER)) {
					scheduleMessages(hSession, noOfDays, 2, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER);
				}
			}
			
			if (PropertiesManager.sms().appointmentReminderValue3() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentReminderValue3();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER)) {
					scheduleMessages(hSession, noOfDays, 3, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER);
				}
			}
			
			if (PropertiesManager.sms().appointmentReminderValue4() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentReminderValue4();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER)) {
					scheduleMessages(hSession, noOfDays, 4, SmsType.MESSAGETYPE_APPOINTMENT_REMINDER);
				}
			}
			
			// Missed Appointment 
			if (PropertiesManager.sms().appointmentMissedValue1() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentMissedValue1();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_MISSED_APPOINTMENT)) {
					scheduleMessages(hSession, noOfDays, 1, SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
				}
			}
			
			if (PropertiesManager.sms().appointmentMissedValue2() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentMissedValue2();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_MISSED_APPOINTMENT)) {
					scheduleMessages(hSession, noOfDays, 2, SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
				}
			}
			
			if (PropertiesManager.sms().appointmentMissedValue3() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentMissedValue3();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_MISSED_APPOINTMENT)) {
					scheduleMessages(hSession, noOfDays, 3, SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
				}
			}
			
			if (PropertiesManager.sms().appointmentMissedValue4() != -1) {
				int noOfDays = PropertiesManager.sms().appointmentMissedValue4();

				if (!SmsManager.checkIfScheduled(hSession, noOfDays, SmsType.MESSAGETYPE_MISSED_APPOINTMENT)) {
					scheduleMessages(hSession, noOfDays, 4, SmsType.MESSAGETYPE_MISSED_APPOINTMENT);
				}
			}
			
			// Check Yesterdays messages and send to alerts table if messages were not successful
			List<MessageSchedule> messages = SmsManager.getFailedMessages(hSession);
			
			// Send these messages to the alerts table and update their alerts field
			for (MessageSchedule messageSchedule : messages) {
				String message = "";
				switch (messageSchedule.getMessageType()){
				case MESSAGETYPE_APPOINTMENT_REMINDER:
					message = "Failed to schedule Just SMS Campaign on " + 
					new SimpleDateFormat("dd MMM yyyy").format(messageSchedule.getScheduleDate()) +  
					" for Appointment Reminders " + messageSchedule.getDaysToSchedule() + " days before "
					+ "(" + messageSchedule.getLanguage() + ")";
					break;
				case MESSAGETYPE_MISSED_APPOINTMENT:
					message = "Failed to schedule Just SMS Campaign on " + 
					new SimpleDateFormat("dd MMM yyyy").format(messageSchedule.getScheduleDate()) +  
					" for Missed Appointments " + messageSchedule.getDaysToSchedule() + " days ago "
					+ "(" + messageSchedule.getLanguage() + ")";
					break;
				}
				AlertManager.createAlert(messageSchedule.getMessageType().getLabel(), message, hSession);
				messageSchedule.setSentToAlerts(true);
				hSession.save(messageSchedule);
				
			}
			
			
			
			tx.commit();

		} catch (Exception e) {
			log.error("Error submitting data to Mobilisr", e);
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			if (hSession != null) {
				hSession.close();
			}

		}
	}

	private void scheduleMessages(Session hSession, int noOfDays, int messageNumber, SmsType messageType) {
		List<StudyParticipant> participants = getContactsForCampaign(hSession, noOfDays,  messageType);
		if (!participants.isEmpty()){
			log.info(MessageFormat.format("Scheduling messages [noOfDays={0}] [messageNumber={1}]" +
					" [messageType={2}] [numParticipants={3}]", noOfDays, messageNumber, messageType, participants.size()));
			createAndScheduleCampaign(hSession, messageNumber, noOfDays, messageType, participants);
		}
	}
}
