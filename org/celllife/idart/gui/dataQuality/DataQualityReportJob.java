package org.celllife.idart.gui.dataQuality;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.exports.PatientExportObject;
import model.manager.exports.columns.SimpleColumnsEnum;
import model.nonPersistent.EntitySet;

import org.celllife.idart.gui.dataExports.DataExportJob;
import org.celllife.idart.gui.dataQualityexports.DataQualityBase;
import org.celllife.idart.misc.AbstractCancellableJob;
import org.eclipse.core.runtime.IProgressMonitor;

public class DataQualityReportJob extends AbstractCancellableJob { 

	private final DataQualityBase dqr;
	private String filename;
	private int numberOfErrors;
	private boolean reportSuccessfullyCompleted = true;

	public DataQualityReportJob(DataQualityBase dqr, String filename) {
		this.dqr = dqr;
		this.filename = filename;
	}

	public void publish(EntitySet set, IProgressMonitor monitor)
			 {
		try{
		if (!set.isEmpty()) {
			PatientExportObject peo = new PatientExportObject();
			peo.setPatientSet(set);
			peo.addSimpleColumn(SimpleColumnsEnum.patientId);
			peo.addSimpleColumn(SimpleColumnsEnum.firstNames);
			peo.addSimpleColumn(SimpleColumnsEnum.lastName);
			peo.addSimpleColumn(new SimpleColumnsEnum("DataQualityError", dqr
					.getMessage()));
			DataExportJob dataExportJob = new DataExportJob(filename, peo);
			monitor.subTask("Exporting data");
			dataExportJob.performJob(monitor);
		}
		}catch(ReportException re1){
			alreadyUsed();
		}
	}

	@Override
	public void performJob(IProgressMonitor monitor) throws ReportException {
		//EntitySet exportSet = dqr.runReport();
		//numberOfErrors = exportSet.getSize();
		//publish(exportSet, monitor);

	}

	public int getNumberOfErrors() {
		return numberOfErrors;
	}
	
	public String alreadyUsed(){
		reportSuccessfullyCompleted = false;
		return "File '"+ filename+"' already in use";
		
	}

	public boolean isReportSuccessfullyCompleted() {
		return reportSuccessfullyCompleted;
	}

	public void setReportSuccessfullyCompleted(boolean reportSuccessfullyCompleted) {
		this.reportSuccessfullyCompleted = reportSuccessfullyCompleted;
	}
}
