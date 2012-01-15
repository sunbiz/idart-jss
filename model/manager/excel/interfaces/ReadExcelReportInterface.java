package model.manager.excel.interfaces;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;

/**
 * 
 */
public interface ReadExcelReportInterface {

	/**
	 * Step 1: Open and Get the Sheet
	 * 
	 * @param stream
	 * @return 
	 */
	public boolean openSheet(File file);

	/**
	 * Step 3: Find all the columns and setup parameters
	 */
	public void findColumns();
	
	/**
	 * Step 4. Check that the compulsory columns are present
	 * @return 
	 */
	public String checkColumns();

	/**
	 * Step 5: Persist the information to the Database
	 * 
	 * @param sess
	 * @param monitor
	 */
	public void persistData(IProgressMonitor monitor);

	/**
	 * Step : Close all the objects in memory and put the sheet to sleep
	 * 
	 */

	public void closeSheet();

	public void setSession(Session hSession);

	public void init();
	
	public int getErrorCount();
	
	public File getErrorFile();

}
