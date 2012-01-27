package org.celllife.idart.sms;

import java.text.MessageFormat;

public enum SmsType {
	
	MESSAGETYPE_MISSED_APPOINTMENT("Missed Appointment","missedAppointmentSms{0,number}_message_{1}"),
	MESSAGETYPE_APPOINTMENT_REMINDER("Appointment Reminder","appointmentReminder{0,number}_message_{1}"); 

	private String label;
	private final String propertyPrefix;
	
	private SmsType(String name, String propertyPrefix) {
		this.label = name;
		this.propertyPrefix = propertyPrefix;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getPropertyName(int number, String language){
		return MessageFormat.format(propertyPrefix, number, language);
	}
}
