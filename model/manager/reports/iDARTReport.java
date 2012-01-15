package model.manager.reports;

import model.manager.excel.conversion.exceptions.ReportException;

import org.eclipse.core.runtime.IProgressMonitor;

public interface iDARTReport {

	public abstract void fillReport(IProgressMonitor monitor)
			throws ReportException;

	public abstract void viewReport();

}