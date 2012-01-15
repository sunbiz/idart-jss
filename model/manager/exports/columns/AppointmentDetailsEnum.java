package model.manager.exports.columns;

import model.manager.exports.AppointmentColumnsGroup;

/**
 * This enum represents all the details that can be requested for an enum along
 * with their heading text.
 * 
 * NOTE: the name of the enum elements must correspond with the exact names of
 * the fields in the Appointment class.
 * 
 * @see AppointmentColumnsGroup
 */
public enum AppointmentDetailsEnum implements IColumnEnum{
	appointmentDate("Appointment Date"),
	
	visitDate("Visit Date") ;

	public String heading;

	private AppointmentDetailsEnum(String name) {
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