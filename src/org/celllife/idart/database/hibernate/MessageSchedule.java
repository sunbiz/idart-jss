package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.celllife.idart.sms.SmsType;

// this class represents the messages that were scheduled 
@Entity
public class MessageSchedule {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="description", length=255)
	private String description;
	
	@Column(name="messagetype", nullable=false, length=255)
	@Enumerated(EnumType.STRING)
	private SmsType messageType;

	@Column(name="messagenumber", nullable=false)
	private int messageNumber;
	
	@Column(name="scheduledate", nullable=false)
	private Date scheduleDate;
	
	@Column(name="daystoschedule")
	private int daysToSchedule;
	
	@Column(name="scheduledsuccessfully")
	private boolean scheduledSuccessfully;
	
	@Column(name="senttoalerts")
	private boolean sentToAlerts;

	@Column(name="language")
	private String language;
	
	public MessageSchedule() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the messageType
	 */
	public SmsType getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(SmsType messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the scheduleDate
	 */
	public Date getScheduleDate() {
		return scheduleDate;
	}

	/**
	 * @param scheduleDate the scheduleDate to set
	 */
	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	/**
	 * @return the daysToSchedule
	 */
	public int getDaysToSchedule() {
		return daysToSchedule;
	}

	/**
	 * @param daysToSchedule the daysToSchedule to set
	 */
	public void setDaysToSchedule(int daysToSchedule) {
		this.daysToSchedule = daysToSchedule;
	}

	/**
	 * @return the scheduledSuccessfully
	 */
	public boolean isScheduledSuccessfully() {
		return scheduledSuccessfully;
	}

	/**
	 * @param scheduledSuccessfully the scheduledSuccessfully to set
	 */
	public void setScheduledSuccessfully(boolean scheduledSuccessfully) {
		this.scheduledSuccessfully = scheduledSuccessfully;
	}

	/**
	 * @return the messageText
	 */
	public int getMessageNumber() {
		return messageNumber;
	}

	/**
	 * @param messageNumber the messageText to set
	 */
	public void setMessageNumber(int messageNumber) {
		this.messageNumber = messageNumber;
	}

	/**
	 * @return the sentToAlerts
	 */
	public boolean isSentToAlerts() {
		return sentToAlerts;
	}

	/**
	 * @param sentToAlerts the sentToAlerts to set
	 */
	public void setSentToAlerts(boolean sentToAlerts) {
		this.sentToAlerts = sentToAlerts;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}
	
}
