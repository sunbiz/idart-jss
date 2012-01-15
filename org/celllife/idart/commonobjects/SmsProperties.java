package org.celllife.idart.commonobjects;

import java.util.List;

import com.pholser.util.properties.BoundProperty;
import com.pholser.util.properties.DefaultsTo;
import com.pholser.util.properties.ValuesSeparatedBy;

/**
 * 
 * These properties configure iDART's sms features. Apart from the general
 * settings there are 2 repeating sections, one for appointment reminders and
 * one for missed appointment notifications.
 * 
 * <h3>Appointment reminders</h3>
 * 
 * You can configure three different appointment reminders each with its own
 * message and each one configured to remind people a certain number of days
 * before their appointment.
 * 
 * For each reminder there are two properties that are required:
 * <ul>
 * <li>appointmentReminder{number}_daysBefore
 * <li>appointmentReminder{number}_message_{language}
 * </ul>
 * {number} is used to number the different properties groups and can be either
 * 1, 2, 3 or 4<br/>
 * {language} is used to allow multiple language options per message and must be
 * one of the values in the "languages" property. Note that this must match one
 * of the languages exactly including case.
 * 
 * <h3>Missed appointments</h3>
 * 
 * You can configure three different missed appointment notifications each with
 * its own message and each one configured to remind people a certain number of
 * days after their appointment.
 * 
 * For each reminder there are two properties that are required:
 * <ul>
 * <li>missedAppointmentSms{number}_daysLate
 * <li>missedAppointmentSms{number}_message_{language}
 * </ul>
 * {number} is used to number the different properties groups and can be either
 * 1, 2, 3 or 4<br/>
 * {language} is used to allow multiple language options per message and must be
 * one of the values in the "languages" property. Note that this must match one
 * of the languages exactly including case.
 * 
 * <h3>Languages</h3>
 * 
 * As has already been discussed you can configure different messages for
 * different languages. The list of languages is configured using the languages
 * property which is a comma-separated list of language names. There must be a
 * message property for each of the languages in this list.
 * 
 * As well as being used to get the correct message text the values in this
 * property will appear in the language selection box on the 'Add participants
 * to study' screen
 * 
 * <h3>Example</h3>
 * 
 * <pre>
 *  languages=English,French
 * 
 *  appointmentReminder1_daysBefore = 14
 *  appointmentReminder1_message_English=Your appointment is in 14 days time
 *  appointmentReminder2_message_French=Votre nomination est dans 14 jours le temps
 * 
 *  appointmentReminder2_daysBefore = 7
 *  appointmentReminder2_message_English=Your appointment is in 7 days time
 *  appointmentReminder2_message_French=Votre nomination est dans 7 jours le temps
 *  
 *  missedAppointmentSms1_daysLate = 1
 *  missedAppointmentSms1_message_English=You missed your appointment yesterday
 *  missedAppointmentSms1_message_French=Vous avez manqué votre rendez-vous hier
 *  
 *  missedAppointmentSms2_daysLate = 7
 *  missedAppointmentSms2_message_English=You missed your appointment 7 days ago
 *  missedAppointmentSms2_message_French=Vous avez raté votre rendez-vous il ya 7 jours
 * </pre>
 * 
 * @see sms.properties file
 */
public interface SmsProperties {

	public String MSISDN_REGEX = "msisdnRegex";

	@DefaultsTo("-1")
	@BoundProperty("appointmentReminder1_daysBefore")
	public int appointmentReminderValue1();

	@DefaultsTo("-1")
	@BoundProperty("appointmentReminder2_daysBefore")
	public int appointmentReminderValue2();

	@DefaultsTo("-1")
	@BoundProperty("appointmentReminder3_daysBefore")
	public int appointmentReminderValue3();

	@DefaultsTo("-1")
	@BoundProperty("appointmentReminder4_daysBefore")
	public int appointmentReminderValue4();

	@DefaultsTo("-1")
	@BoundProperty("missedAppointmentSms1_daysLate")
	public int appointmentMissedValue1();

	@DefaultsTo("-1")
	@BoundProperty("missedAppointmentSms2_daysLate")
	public int appointmentMissedValue2();

	@DefaultsTo("-1")
	@BoundProperty("missedAppointmentSms3_daysLate")
	public int appointmentMissedValue3();

	@DefaultsTo("-1")
	@BoundProperty("missedAppointmentSms4_daysLate")
	public int appointmentMissedValue4();

	@BoundProperty("communicateUrl")
	public String mobilisrurl();

	@BoundProperty("communicateUsername")
	public String mobilisrusername();

	@BoundProperty("communicatePassword")
	public String mobilisrpassword();

	/**
	 * All participants in the control group are added to this campaign. Leave
	 * as -1 this is not desired.
	 */
	@DefaultsTo("-1")
	@BoundProperty("controlCampaignId")
	public Long controlcampaignid();

	/**
	 * The default value for the custom message time on the 'Add participants to
	 * study' screen
	 */
	@DefaultsTo("07:30")
	@BoundProperty("defaultCustomMsgTime")
	public String defaultCustomMsgTime();

	@BoundProperty("languages")
	@ValuesSeparatedBy(pattern = "\\s*,\\s*")
	public List<String> languages();

	/**
	 * A comma-sparated list of mobile networks that will be displayed in the
	 * Networks selection box on the 'Add participants to study' screen
	 */
	@BoundProperty("mobileNetworks")
	@ValuesSeparatedBy(pattern = "\\s*,\\s*")
	public List<String> networks();

	@BoundProperty("msisdnPrefix")
	public String msisdnPrefix();
	
	/**
	 * Note that this property does not work due to parsing issues.
	 * Use {@link PropertiesManager#smsRaw()} to get the property
	 */
	@BoundProperty("msisdnRegex")
	@Deprecated
	public String msisdnRegex();
}
