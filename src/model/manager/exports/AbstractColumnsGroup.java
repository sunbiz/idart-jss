package model.manager.exports;

import java.util.Arrays;

import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.IColumnEnum;

import org.celllife.idart.database.hibernate.APIException;

public abstract class AbstractColumnsGroup implements ExportColumnGroup {

	private ColumnModifier modifier;
	private Integer modifierNum = null;
	private IColumnEnum[] columns = null;

	public AbstractColumnsGroup() {
	}

	public AbstractColumnsGroup(ColumnModifier modifier, Integer modifierNum,
			IColumnEnum[] columns) {
		this.modifier = modifier;
		this.modifierNum = modifierNum;
		this.columns = columns;
	}

	@Override
	public String toTemplateString() {
		String s = "";
		if (columns == null) {
			columns = new IColumnEnum[] {};
		}

		String function = getDataExportMethodName(modifier);

		if (ColumnModifier.MODIFIER_OLDEST_NUM.equals(modifier)
				|| ColumnModifier.MODIFIER_NEWEST_NUM.equals(modifier)
				|| ColumnModifier.MODIFIER_FIRST_AND_LAST.equals(modifier)) {
			Integer num = modifierNum == null ? 1 : modifierNum;

			s += "#set($arr = [";
			for (Integer x = 0; x < columns.length; x++) {
				s += "'" + columns[x].template() + "'";
				if (!x.equals(columns.length - 1)) {
					s += ",";
				}
			}
			s += "])";

			if (ColumnModifier.MODIFIER_OLDEST_NUM.equals(modifier)) {
				s += "#set($groupRow =" + function + "(" + num + ", $arr))";
			} else if (ColumnModifier.MODIFIER_NEWEST_NUM.equals(modifier)) {
				s += "#set($groupRow =" + function + "(" + num + ", $arr))";
			} else if (ColumnModifier.MODIFIER_FIRST_AND_LAST.equals(modifier)) {
				s += "#set($groupRow =" + function + "($arr))";
			}
			s += "#foreach($vals in $groupRow)";
			s += "#if($velocityCount > 1)";
			s += "$!{fn.getSeparator()}";
			s += "#end";
			s += "#foreach($val in $vals)";
			s += "#if($velocityCount > 1)";
			s += "$!{fn.getSeparator()}";
			s += "#end";
			s += "$!{fn.replaceSeperator($fn.getValueAsString($val))}";
			s += "#end";
			s += "#end\n";
		} else {
			if (columns.length < 1) {
				function = "$!{fn.replaceSeperator($fn.getValueAsString("
					+ function + "()))}";
				s += function; // if we don't have extras, just call the normal
				// function and print it
			} else {
				s += "#set($arr = [";
				for (Integer x = 0; x < columns.length; x++) {
					s += "'" + columns[x].template() + "'";
					if (!x.equals(columns.length - 1)) {
						s += ",";
					}
				}
				s += "])";

				function += "($arr)";

				s += "#set($groupRow =" + function + ")";
				s += "#foreach($val in $groupRow)";
				s += "#if($velocityCount > 1)";
				s += "$!{fn.getSeparator()}";
				s += "#end";
				s += "$!{fn.replaceSeperator($fn.getValueAsString($val))}";
				s += "#end\n";
			}
		}

		return s;
	}

	/**
	 * Returns the name of the appropriate method to call to get the data for
	 * the columns. The following method signatures are expected for the
	 * different modifiers:
	 * <ul>
	 * <li>{@link ColumnModifier#MODIFIER_NEWEST} and
	 * {@link ColumnModifier#MODIFIER_OLDEST}: <br/> a method with the
	 * signarture <strong>methodName(List&lt;String&gt; details)</strong>. The
	 * second parameter may be null of no details have been specified. The
	 * method should return a List or array of objects.</li>
	 * <li>{@link ColumnModifier#MODIFIER_NEWEST_NUM} and
	 * {@link ColumnModifier#MODIFIER_OLDEST_NUM}: <br/> a method with the
	 * signarture <strong>methodName(Integer num, List&lt;String&gt;
	 * details)</strong>. The second parameter may be null of no details have
	 * been specified. The method should return a List or array of objects.</li>
	 * </ul>
	 *
	 * @param colModifier
	 * @return the name of the method to call. i.e.
	 *         "$fn.getNewestEpisodeDetails"
	 */
	protected abstract String getDataExportMethodName(ColumnModifier colModifier);

	@Override
	public abstract String getColumnName();

	@Override
	public String getTemplateColumnName() {
		String s = getDetailTemplateColumnNames(false);

		if (ColumnModifier.MODIFIER_OLDEST_NUM.equals(modifier)
				|| ColumnModifier.MODIFIER_NEWEST_NUM.equals(modifier)
				|| ColumnModifier.MODIFIER_FIRST_AND_LAST.equals(modifier)) {

			if (modifierNum == null || modifierNum < 2) {
				s += "#foreach($o in []) ";
			} else {
				s += "#foreach($o in [1.." + (modifierNum - 1) + "]) ";
			}

			s += "$!{fn.getSeparator()}";
			s += getDetailTemplateColumnNames(true);
			s += "#end\n";
		}

		return s;
	}

	private String getDetailTemplateColumnNames(boolean multipleGroups) {
		String s = "";
		if (columns != null) {
			for (int i = 0; i < columns.length; i++) {
				if (i > 0) {
					s += "$!{fn.getSeparator()}";
				}
				s += getColumnName() + " " + columns[i].getColumnName();

				// numbering of the gourps
				if (multipleGroups) {
					// if this is the first detail of the current group
					// increase the counter
					// that will be used for numbering
					if (i == 0) {
						s += " #set($velocityCount = $velocityCount+1)";
					}

					s += " ($velocityCount)";
				} else {
					s += " (1)"; // add a label for the first group
				}
			}
		}
		return s;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.ExportColumnGroup#getModifier()
	 */
	@Override
	public ColumnModifier getModifier() {
		return modifier;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.ExportColumnGroup#setModifier(model.manager.exports.columns.ColumnModifier)
	 */
	@Override
	public void setModifier(ColumnModifier modifier) {
		this.modifier = modifier;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.ExportColumnGroup#getColumns()
	 */
	@Override
	public IColumnEnum[] getColumns() {
		return columns;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.ExportColumnGroup#setColumns(model.manager.exports.columns.IColumnEnum[])
	 */
	@Override
	public void setColumns(IColumnEnum[] columns) {
		this.columns = columns;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.ExportColumnGroup#getModifierNum()
	 */
	@Override
	public Integer getModifierNum() {
		return modifierNum;
	}

	/* (non-Javadoc)
	 * @see model.manager.exports.ExportColumnGroup#setModifierNum(java.lang.Integer)
	 */
	@Override
	public void setModifierNum(Integer modifierNum) {
		this.modifierNum = modifierNum;
	}

	@Override
	public String toString() {
		String s = "";
		switch (modifier) {
		case MODIFIER_NEWEST:
			s += "Most recent " + getColumnName();
			break;
		case MODIFIER_OLDEST:
			s += "Oldest " + getColumnName();
			break;
		case MODIFIER_NEWEST_NUM:
			s += "Most recent " + modifierNum + " " + getColumnName() + "s";
			break;
		case MODIFIER_OLDEST_NUM:
			s += "Oldest " + modifierNum + " " + getColumnName() + "s";
			break;
		case MODIFIER_FIRST_AND_LAST:
			s += "First and Last " + getColumnName();
			break;
		case MODIFIER_SHOW_ACTIVE_ONLY:
			s += "Active " + getColumnName();
			break;
		default:
			throw new APIException("Unknown modifier in AbstractColumnGroup "
					+ modifier);
		}
		s += " with values:\n";
		if (columns.length > 0) {
			for (int i = 0; i < columns.length - 1; i++) {
				s += "    " + columns[i].getColumnName() + "\n";
			}
			s += "    " + columns[columns.length - 1].getColumnName();
		}
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(columns);
		result = prime * result
		+ ((modifier == null) ? 0 : modifier.hashCode());
		result = prime * result
		+ ((modifierNum == null) ? 0 : modifierNum.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractColumnsGroup other = (AbstractColumnsGroup) obj;
		if (!Arrays.equals(columns, other.columns))
			return false;
		if (modifier == null) {
			if (other.modifier != null)
				return false;
		} else if (!modifier.equals(other.modifier))
			return false;
		if (modifierNum == null) {
			if (other.modifierNum != null)
				return false;
		} else if (!modifierNum.equals(other.modifierNum))
			return false;
		return true;
	}
}