package org.celllife.idart.gui.dataQuality;

import model.nonPersistent.EntitySet;

public interface DataQualityReport {

	
	public EntitySet runReport();
	
	public String getReportMessage();
	
	public String getFileName();
}
