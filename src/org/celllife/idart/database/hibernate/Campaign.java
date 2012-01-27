package org.celllife.idart.database.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name="campaign")
public class Campaign {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false, unique=true)
	private Integer id;
	
	@Column(name="mobilisrid", nullable=false, unique=true)
	private Long mobilisrId;
	
	@Column(name="name", nullable=false, length=255)
	private String name;
	
	@Column(name="description", nullable=true, length=255)
	private String description;
	
	@Column(name="duration", nullable=true)
	private int duration;
	
	@Column(name="timesperday", nullable=true)
	private int timesperday;
	
	@Column(name="status", nullable=true)
	private String status;
	
	@Column(name="type", nullable=true)
	private String type;
	
	@Version
	@Column(name="version", nullable=true)
	private Long version;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="startdate")
	private Date startdate;
	
	public Campaign() {
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startdate;
	}

	public void setStartDate(Date startDate) {
		this.startdate = startDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTimesPerDay() {
		return timesperday;
	}

	public void setTimesPerDay(int timesPerDay) {
		this.timesperday = timesPerDay;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public void setMobilisrId(Long mobilisrId) {
		this.mobilisrId = mobilisrId;
	}

	public Long getMobilisrId() {
		return mobilisrId;
	}	
	
}
