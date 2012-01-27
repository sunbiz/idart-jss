package org.celllife.idart.gui.dataExports;

import model.manager.exports.ExportColumn;

import org.eclipse.swt.widgets.Composite;

public interface iDataExport {

	public void createView(Composite compDetails);
	public void updateView(ExportColumn column);
	public ExportColumn getColumn();
	public boolean fieldsOk();
	public void clearForm();
}
