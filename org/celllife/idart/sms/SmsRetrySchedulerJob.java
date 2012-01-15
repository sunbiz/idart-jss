package org.celllife.idart.sms;

import java.util.List;

import model.manager.SmsManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SmsRetrySchedulerJob extends CampaignSchedulingJob implements Job {

	private static final Logger log = Logger.getLogger(SmsRetrySchedulerJob.class.getName());
	
	public static final String JOB_NAME = "smsReminderJob";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("SMS Retry job scheduling starting");

		Transaction tx = null;
		Session hSession = null;
		try {
			hSession = HibernateUtil.getNewSession();
			tx = hSession.beginTransaction();

			// Firstly, get unsuccessful messages scheduled for today
			List<MessageSchedule> messages = SmsManager.getUnsuccessfulMessagesToday(hSession);

			// Try to send the messages again
			for (MessageSchedule messageSchedule : messages) {
				int noOfDays = messageSchedule.getDaysToSchedule();
				SmsType messageType = messageSchedule.getMessageType();
				int messageNumber = messageSchedule.getMessageNumber();
				final String language = messageSchedule.getLanguage();
				
				List<StudyParticipant> participants = getContactsForCampaign(hSession, noOfDays,  messageType);
				CollectionUtils.filter(participants, new Predicate() {
					@Override
					public boolean evaluate(Object p) {
						return ((StudyParticipant)p).getLanguage().equals(language);
					}
				});
				
				if (!participants.isEmpty()){
					boolean success = createAndScheduleCampaign(hSession, messageNumber, noOfDays, messageType, participants, language);
					if (success){
						SmsManager.updateMessageSchedule(hSession, messageSchedule);
					}
				}
			}
			
						
			tx.commit();

		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			log.error("Error submitting data to Mobilisr", e);
		} finally {
			if (hSession != null) {
				hSession.close();
			}

		}
		log.info("Mobilisr submission job completed");
	}
}

