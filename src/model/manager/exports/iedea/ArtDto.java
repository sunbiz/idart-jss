package model.manager.exports.iedea;

import java.util.Date;

public class ArtDto {

	private String form;
	private String code;
	private Date startdate;
	private Date enddate;

	public ArtDto() {
	}

	public ArtDto(String form, String code, Date startdate, Date enddate) {
		super();
		this.form = form;
		this.code = code;
		this.startdate = startdate;
		this.enddate = enddate;
	}

	public String getCode() {
		return code;
	}

	public Date getEnddate() {
		return enddate;
	}

	public String getForm() {
		return form;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
}
