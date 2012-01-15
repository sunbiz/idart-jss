package model.manager.exports;

import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.IColumnEnum;

public interface ExportColumnGroup extends ExportColumn {

	public abstract ColumnModifier getModifier();

	public abstract void setModifier(ColumnModifier modifier);

	public abstract IColumnEnum[] getColumns();

	public abstract void setColumns(IColumnEnum[] columns);

	public abstract Integer getModifierNum();

	public abstract void setModifierNum(Integer modifierNum);

}