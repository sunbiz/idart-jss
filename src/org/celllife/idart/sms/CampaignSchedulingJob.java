package org.celllife.idart.sms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import model.manager.AdministrationManager;
import model.manager.SmsManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.integration.mobilisr.MobilisrManager;
import org.celllife.idart.misc.iDARTUtil;
import org.celllife.mobilisr.api.rest.CampaignDto;
import org.celllife.mobilisr.api.rest.ContactDto;
import org.celllife.mobilisr.api.rest.ErrorDto;
import org.celllife.mobilisr.api.rest.MessageDto;
import org.celllife.mobilisr.api.rest.PagedListDto;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.hibernate.Session;

public class CampaignSchedulingJob {
	
	private static final Logger log = Logger.getLogger(CampaignSchedulingJob.class.getName());

	public CampaignSchedulingJob() {
		super();
	}

	/**
	 * Creates the mobilisr campaign, adds the messages and contacts to it and
	 * schedules it using the mobilisr api.
	 * 
	 * @param hSession
	 * @param messageNumber
	 * @param noOfDays
	 * @param messageType
	 * @param contacts
	 */
	protected void createAndScheduleCampaign(Session hSession,
			int messageNumber, int noOfDays, SmsType messageType,
			List<StudyParticipant> participants) {
		
		Map<String, Collection<StudyParticipant>> languageGroups = new HashMap<String, Collection<StudyParticipant>>();
		for (StudyParticipant p : participants) {
			String language = p.getLanguage();
			Collection<StudyParticipant> group = languageGroups.get(language);
			if (group == null){
				group = new HashSet<StudyParticipant>();
				languageGroups.put(language, group);
			}
			group.add(p);
		}
		
		for (Entry<String, Collection<StudyParticipant>> group : languageGroups.entrySet()) {
			String language = group.getKey();
			Collection<StudyParticipant> groupParticipants = group.getValue();
			boolean success = createAndScheduleCampaign(hSession,
					messageNumber, noOfDays, messageType, groupParticipants,
					language);
			saveMessageSchedule(hSession, noOfDays, messageType, messageNumber,
					language, success);
		}

	}

	protected boolean createAndScheduleCampaign(Session hSession,
			int messageNumber, int noOfDays, SmsType messageType,
			Collection<StudyParticipant> participants,
			String language) {
		
		if (!PropertiesManager.sms().languages().contains(language)){
			log.error("Unknown language: " + language);
			return false;
		}
		String propertyName = messageType.getPropertyName(messageNumber, language);
		log.debug("Looking for property of name: " + propertyName);
		String messageText = (String) PropertiesManager.smsRaw().get(propertyName);
		if (messageText == null || messageText.isEmpty()){
			log.error("No property found for name: " + propertyName);
			return false;
		}
		
		List<ContactDto> contacts = getContacts(participants);
		
		MessageDto message = new MessageDto();
		message.setText(messageText);
		message.setDate(new Date());
		message.setTime(new Date());

		// Firstly create a new campaign
		CampaignDto campaign = new CampaignDto();
		if (campaign.getMessages() == null) {
			campaign.setMessages(new ArrayList<MessageDto>());
		}
		campaign.getMessages().add(message);
		campaign.setName(AdministrationManager.getPharmacyDetails(hSession)
				.getPharmacyName()
				+ ": "
				+ noOfDays
				+ " day "
				+ messageType.getLabel()
				+ " "
				+ language
				+ " (" + iDARTUtil.format(new Date()) + ")");

		campaign.setContacts(contacts);

		// Only schedule if there are contacts to receive the sms
		if (campaign.getContacts().size() > 0) {

			try {
				MobilisrManager.createNewCampaign(campaign);
				return true;
			} catch (RestCommandException e) {
				log.warn("Failed to create new Just SMS Campaign on Mobiliser.");
				PagedListDto<ErrorDto> errors = e.getErrors();
				if (errors != null && !errors.isEmpty()){
					log.warn("Request errors: ");
					for (ErrorDto error : errors.getElements()) {
						log.warn(error.getErrorCode() + " - " + error.getMessage());
					}
				}
				return false;
			}
		}
		return false;
	}

	private void saveMessageSchedule(Session hSession, int noOfDays,
			SmsType messageType, int messageNumber, String language, boolean success) {
		MessageSchedule ms = new MessageSchedule();
		ms.setDaysToSchedule(noOfDays);
		ms.setMessageType(messageType);
		ms.setLanguage(language);
		ms.setMessageNumber(messageNumber);
		switch (messageType){
		case MESSAGETYPE_APPOINTMENT_REMINDER:
			ms.setDescription("Send sms appointment reminders for patients expected in "
					+ noOfDays + " days time");
			break;
		case MESSAGETYPE_MISSED_APPOINTMENT:
			ms.setDescription("Send sms to patients who missed their appointment "
					+ noOfDays + " days ago");
			break;
		}
		ms.setScheduleDate(new Date());
		ms.setScheduledSuccessfully(success);
		hSession.save(ms);
	}

	protected List<StudyParticipant> getContactsForCampaign(Session hSession, int noOfDays, SmsType messageType) {
		switch(messageType){
		case MESSAGETYPE_APPOINTMENT_REMINDER:
			return SmsManager.getContactsExpected(hSession,noOfDays);
		case MESSAGETYPE_MISSED_APPOINTMENT:
			return SmsManager.getContactsDefaulted(hSession,noOfDays);
		default:
			return Collections.emptyList();
		}
	}
	
	protected List<ContactDto> getContacts(Collection<StudyParticipant> participants) {
		List<ContactDto> result = new ArrayList<ContactDto>();
		for (StudyParticipant campaignParticipant : participants) {
			ContactDto contact = new ContactDto();
			contact.setFirstName(LocalObjects.pharmacy.getPharmacyName());
			contact.setLastName("" + campaignParticipant.getPatient().getId());
			contact.setMsisdn(campaignParticipant.getPatient().getCellphone());

			result.add(contact);
		}
		return result;
	}

}