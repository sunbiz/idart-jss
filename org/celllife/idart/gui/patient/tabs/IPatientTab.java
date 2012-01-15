package org.celllife.idart.gui.patient.tabs;

import java.util.Map;

import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.misc.IGenericTab;
import org.celllife.idart.gui.patient.AddPatient;
import org.eclipse.swt.graphics.Color;

/**
 * Interface for adding tabs in {@link AddPatient}
 */
public interface IPatientTab extends IGenericTab{

	/**
	 * Populates the interface with details from the patient.
	 * 
	 * @param sess
	 * @param patient
	 * @param isPatientActive
	 */
	public void loadPatientDetails(Patient patient, boolean isPatientActive);

	/**
	 * Updates the patient with the data from the interface.
	 * 
	 * @param patient
	 */
	public void setPatientDetails(Patient patient);

	/**
	 * Enables or disables the interface elements.
	 * 
	 * @param enable
	 * @param color
	 */
	public void enable(boolean enable, Color color);

	/**
	 * Checks if any changes have been made to the data on the interface.
	 * 
	 * @param patient
	 * @return true if any changes have been made to the data on the interface.
	 */
	public boolean changesMade(Patient patient);

	/**
	 * Clears the data from the interface
	 */
	public void clear();

	/**
	 * Submit any tab specific information to the database. Implimentations of
	 * this method should only submit data that has not already been submitted
	 * by updating the Patient.
	 * @param patient
	 */
	public void submit(Patient patient);

	/**
	 * Validates the fields in the tab. 
	 * @param patient 
	 * @return a Map with the following keys:<br/>
	 * <ul>
	 * 	<li>boolean result = true if all fields are valid</li>
	 * 	<li>String title = message box title if validation fails</li>
	 * 	<li>String message = message box message if validation fails</li>
	 * </ul
	 */
	public Map<String, String> validateFields(Patient patient);
	
}