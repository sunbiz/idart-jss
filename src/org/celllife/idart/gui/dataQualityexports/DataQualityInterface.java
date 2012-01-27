package org.celllife.idart.gui.dataQualityexports;

import org.eclipse.jface.operation.IRunnableWithProgress;


public interface DataQualityInterface extends IRunnableWithProgress {

	public void getData();
	
	public String getFileName();
	public void setFileName(String fileName);
	
	public String getMessage();
	
	public String alreadyUsed();
	public boolean isReportSuccessfullyCompleted();
	}
