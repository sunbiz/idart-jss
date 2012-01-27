package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="alerts")
public class Alerts {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="alertmessage", nullable=false)
	private String alertMessage;
	
	@Column(name="alertdate", nullable=false)
	private Date alertDate;
	
	@Column(name="alerttype", nullable = false)
	private String alertType;
	
	@Column(name="void", nullable=false)
	private Boolean Void = false;
	
	public Alerts() {
	}


	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the alertMessage
	 */
	public String getAlertMessage() {
		return alertMessage;
	}


	/**
	 * @param alertMessage the alertMessage to set
	 */
	public void setAlertMessage(String alertMessage) {
		this.alertMessage = alertMessage;
	}


	/**
	 * @return the alertDate
	 */
	public Date getAlertDate() {
		return alertDate;
	}


	/**
	 * @param alertDate the alertDate to set
	 */
	public void setAlertDate(Date alertDate) {
		this.alertDate = alertDate;
	}


	/**
	 * @return the alertType
	 */
	public String getAlertType() {
		return alertType;
	}


	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}


	public void setVoid(Boolean _void) {
		Void = _void;
	}


	public Boolean getVoid() {
		return Void;
	}
	
	
	
	
	

}
