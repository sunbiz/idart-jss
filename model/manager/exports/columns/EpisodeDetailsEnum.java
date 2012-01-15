package model.manager.exports.columns;

import model.manager.exports.EpisodeColumnsGroup;

/**
 * This enum represents all the details that can be requested for an enum
 * along with their heading text.
 * 
 * NOTE: the name of the enum elements must correspond with the exact names
 * of the fields in the Episode class.
 * 
 * @see EpisodeColumnsGroup
 */
public enum EpisodeDetailsEnum implements IColumnEnum{
	startDate("Start Date"),

	startReason("Start Reason"),

	startNotes("Start Notes"),

	stopDate("Stop Date"),

	stopReason("Stop Reason"),

	stopNotes("Stop Notes"),
	
	clinic("Clinic");

	public String heading;

	private EpisodeDetailsEnum(String name) {
		this.heading = name;
	}

	@Override
	public String getColumnName() {
		return heading;
	}
	
	@Override
	public String toString() {
		return heading;
	}
	
	@Override
	public String template() {
		if (this.equals(clinic)){
			return "clinic.clinicName";
		}
		return name();
	}
}