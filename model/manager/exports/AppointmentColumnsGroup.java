package model.manager.exports;

import java.io.Serializable;

import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.IColumnEnum;

import org.celllife.idart.database.hibernate.APIException;

public class AppointmentColumnsGroup extends AbstractColumnsGroup implements
		ExportColumnGroup, Serializable {

	public static final long serialVersionUID = 987654323L;

	public static final String COLUMN_TYPE = "Appointments";

	private final String columnName = "Appointment";

	public AppointmentColumnsGroup() {
		super();
	}

	public AppointmentColumnsGroup(ColumnModifier modifier, Integer modifierNum,
			IColumnEnum[] columns) {
		super(modifier, modifierNum, columns);
	}

	@Override
	protected String getDataExportMethodName(ColumnModifier colModifier) {
		String function = " ";
		switch (colModifier) {
		case MODIFIER_NEWEST:
			function += "$fn.getNewestAppointmentDetails";
			break;
		case MODIFIER_OLDEST:
			function += "$fn.getOldestAppointmentDetails";
			break;
		case MODIFIER_NEWEST_NUM:
			function += "$fn.getNewestNAppointmentDetails";
			break;
		case MODIFIER_OLDEST_NUM:
			function += "$fn.getOldestNAppointmentDetails";
			break;
		case MODIFIER_SHOW_ACTIVE_ONLY:
			function += "$fn.getActiveAppointmentDetails";
			break;
		default:
			throw new APIException("Unknown column modifer.");
		}
		return function;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String getColumnType() {
		return COLUMN_TYPE;
	}
}