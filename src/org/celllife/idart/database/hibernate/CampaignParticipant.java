package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="campaign_participant")
public class CampaignParticipant {

	@Id
	@GeneratedValue
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="patient_id", nullable=false)
	Patient patient;
	
	@ManyToOne
	@JoinColumn(name="campaign_id", nullable=false)
	Campaign campaign;
	
	public CampaignParticipant() {
	}
	
	public CampaignParticipant(Campaign campaign, Patient patient){
		this.campaign = campaign;
		this.patient = patient;
	}
	
	public Campaign getCampaign() {
		return campaign;
	}
	
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
