package org.celllife.idart.gui.dataQualityexports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.manager.excel.conversion.exceptions.ReportException;

import org.apache.log4j.Logger;
import org.celllife.idart.misc.AbstractCancellableJob;
import org.eclipse.core.runtime.IProgressMonitor;

public abstract class DataQualityBase extends AbstractCancellableJob implements
		DataQualityInterface {
	
	private final Logger log = Logger.getLogger(this.getClass());

	private String fileName;

	// List with query data
	protected List<Object[]> data = new ArrayList<Object[]>();

	protected String[] headings;

	private boolean reportSuccessfullyCompleted = true;
	// Needs to implemented per class
	@Override
	abstract public void getData();

	@Override
	public void performJob(final IProgressMonitor monitor)
			throws ReportException {

		FileWriter out = null;
		File csvFile = new File(fileName);
		try {
			out = new FileWriter(csvFile);
			
			for (String head : headings) {
				out.write(head);
				out.write(",");
			}
			out.write("\n");
			
			for (Object[] obj : data) {
				for (Object object : obj) {
					if (object != null) {
						out.write((object).toString());
					}
					else {
						out.write(" ");
					}
					out.write(",");
				}
				out.write("\n");
				
			}
		} catch (IOException e) {
			log.error("Error running data quality report: " + getFileName(),e);
			alreadyUsed();
		} finally {
			if (out != null) {
				try { out.close(); } catch (IOException e) {/*ignore */}
			}
		}
	}

	@Override
	public String alreadyUsed(){
		reportSuccessfullyCompleted = false;
		return "A file with this name is already open. If you want to ignore that file, please click Ok.\n\n Or, you can save the file you are currently working on with a new name, then come back here and click Ok to load the new file. ";
		
	}

	@Override
	public boolean isReportSuccessfullyCompleted() {
		return reportSuccessfullyCompleted;
	}
	
	public void setData(List<Object[]> data) {
		this.data = data;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	public String[] getHeadings() {
		return headings;
	}

	public void setHeadings(String[] headings) {
		this.headings = headings;
	}
}
