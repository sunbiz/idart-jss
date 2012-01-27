package model.manager.exports;

import java.io.Serializable;

import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.IColumnEnum;

import org.celllife.idart.database.hibernate.APIException;

public class EpisodeColumnsGroup extends AbstractColumnsGroup implements
		ExportColumnGroup, Serializable {

	public static final long serialVersionUID = 987654323L;

	public static final String COLUMN_TYPE = "Episodes";

	private final String columnName = "Episode";

	public EpisodeColumnsGroup() {
		super();
	}

	public EpisodeColumnsGroup(ColumnModifier modifier, Integer modifierNum,
			IColumnEnum[] columns) {
		super(modifier, modifierNum, columns);
	}

	@Override
	protected String getDataExportMethodName(ColumnModifier colModifier) {
		String function = " ";
		switch (colModifier) {
		case MODIFIER_NEWEST:
			function += "$fn.getNewestEpisodeDetails";
			break;
		case MODIFIER_OLDEST:
			function += "$fn.getOldestEpisodeDetails";
			break;
		case MODIFIER_NEWEST_NUM:
			function += "$fn.getNewestNEpisodeDetails";
			break;
		case MODIFIER_OLDEST_NUM:
			function += "$fn.getOldestNEpisodeDetails";
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