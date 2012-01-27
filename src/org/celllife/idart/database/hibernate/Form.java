package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 */
@Entity
public class Form {

	@Id
	@GeneratedValue
	private Integer id;

	private String form;

	private String actionLanguage1; // eg.take

	private String actionLanguage2;

	private String actionLanguage3;

	private String formLanguage1; // eg.tablets

	private String formLanguage2;

	private String formLanguage3;

	private String dispInstructions1;

	private String dispInstructions2;

	public Form() {
		super();

	}

	/**
	 * Method getForm.
	 * @return String
	 */
	public String getForm() {
		return form;
	}

	/**
	 * Method setForm.
	 * @param form String
	 */
	public void setForm(String form) {
		this.form = form;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method getActionLanguage1.
	 * @return String
	 */
	public String getActionLanguage1() {
		return actionLanguage1;
	}

	/**
	 * Method setActionLanguage1.
	 * @param actionLanguage1 String
	 */
	public void setActionLanguage1(String actionLanguage1) {
		this.actionLanguage1 = actionLanguage1;
	}

	/**
	 * Method getActionLanguage2.
	 * @return String
	 */
	public String getActionLanguage2() {
		return actionLanguage2;
	}

	/**
	 * Method setActionLanguage2.
	 * @param actionLanguage2 String
	 */
	public void setActionLanguage2(String actionLanguage2) {
		this.actionLanguage2 = actionLanguage2;
	}

	/**
	 * Method getActionLanguage3.
	 * @return String
	 */
	public String getActionLanguage3() {
		return actionLanguage3;
	}

	/**
	 * Method setActionLanguage3.
	 * @param actionLanguage3 String
	 */
	public void setActionLanguage3(String actionLanguage3) {
		this.actionLanguage3 = actionLanguage3;
	}

	/**
	 * Method getFormLanguage1.
	 * @return String
	 */
	public String getFormLanguage1() {
		return formLanguage1;
	}

	/**
	 * Method setFormLanguage1.
	 * @param formLanguage1 String
	 */
	public void setFormLanguage1(String formLanguage1) {
		this.formLanguage1 = formLanguage1;
	}

	/**
	 * Method getFormLanguage2.
	 * @return String
	 */
	public String getFormLanguage2() {
		return formLanguage2;
	}

	/**
	 * Method setFormLanguage2.
	 * @param formLanguage2 String
	 */
	public void setFormLanguage2(String formLanguage2) {
		this.formLanguage2 = formLanguage2;
	}

	/**
	 * Method getFormLanguage3.
	 * @return String
	 */
	public String getFormLanguage3() {
		return formLanguage3;
	}

	/**
	 * Method setFormLanguage3.
	 * @param formLanguage3 String
	 */
	public void setFormLanguage3(String formLanguage3) {
		this.formLanguage3 = formLanguage3;
	}

	/**
	 * Method getDispInstructions1.
	 * @return String
	 */
	public String getDispInstructions1() {
		return dispInstructions1;
	}

	/**
	 * Method setDispInstructions1.
	 * @param dispensingInstructionsLanguage1 String
	 */
	public void setDispInstructions1(String dispensingInstructionsLanguage1) {
		this.dispInstructions1 = dispensingInstructionsLanguage1;
	}

	/**
	 * Constructor for Form.
	 * @param form String
	 * @param actionLanguage1 String
	 * @param actionLanguage2 String
	 * @param actionLanguage3 String
	 * @param formLanguage1 String
	 * @param formLanguage2 String
	 * @param formLanguage3 String
	 * @param dispensingInstructionsLanguage1 String
	 * @param dispensingInstructionsLanguage2 String
	 */
	public Form(String form, String actionLanguage1, String actionLanguage2,
			String actionLanguage3, String formLanguage1, String formLanguage2,
			String formLanguage3, String dispensingInstructionsLanguage1,
			String dispensingInstructionsLanguage2) {
		super();
		this.form = form;
		this.actionLanguage1 = actionLanguage1;
		this.actionLanguage2 = actionLanguage2;
		this.actionLanguage3 = actionLanguage3;
		this.formLanguage1 = formLanguage1;
		this.formLanguage2 = formLanguage2;
		this.formLanguage3 = formLanguage3;
		this.dispInstructions1 = dispensingInstructionsLanguage1;
		this.dispInstructions2 = dispensingInstructionsLanguage2;
	}

	/**
	 * Method equals.
	 * @param form Form
	 * @return boolean
	 */
	public boolean equals(Form formCompare) {
		if (this.form.equalsIgnoreCase(formCompare.form))
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.form.hashCode();
	}

	/**
	 * Method getDispInstructions2.
	 * @return String
	 */
	public String getDispInstructions2() {
		return dispInstructions2;
	}

	/**
	 * Method setDispInstructions2.
	 * @param dispInstructions2 String
	 */
	public void setDispInstructions2(String dispInstructions2) {
		this.dispInstructions2 = dispInstructions2;
	}
}
