package model.manager.exports.columns;

import model.manager.exports.PrescriptionColumnsGroup;


/**
 * This enum represents all the details that can be requested for an enum along
 * with their heading text.
 * 
 * NOTE: the name of the enum elements must correspond with the exact names of
 * the fields in the Prescription class.
 * 
 * @see PrescriptionColumnsGroup
 */
public enum PrescriptionDetailsEnum implements IColumnEnum{
	drugs("drugs"),

	date("Start Date"),

	clinicalStage("Clinical Stage"),

	current("Current"),

	duration("Duration"),

	reasonForUpdate("Reason For Update"),

	notes("Prescription Notes"),

	weight("Weight"),

	endDate("End Date");


	public String heading;

	private PrescriptionDetailsEnum(String name) {
		this.heading = name;
	}

	@Override
	public String getColumnName() {
		return heading;
	}
	
	@Override
	public String template() {
		return name();
	}
}