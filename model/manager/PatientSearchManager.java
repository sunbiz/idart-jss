package model.manager;

import java.util.List;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.patient.AlternateIdSelector;
import org.celllife.idart.integration.eKapa.gui.SearchPatientGui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

/**
 * Class to manage how a patient is selected when a patient ID is scanned or
 * typed
 * 
 */
public class PatientSearchManager {

	private final Shell parent;

	private final String patientId;

	private final Session hSession;

	/**
	 * Constructor for PatientSearchManager.
	 * 
	 * @param parent
	 *            Shell
	 * @param patientId
	 *            String
	 * @param session
	 *            Session
	 */
	public PatientSearchManager(Shell parent, String patientId, Session session) {
		super();
		this.parent = parent;
		this.patientId = patientId.toUpperCase();
		this.hSession = session;
	}

	/**
	 * @param allowEkapaSearch
	 *            boolean
	 * @return the patient the user selected, or null if no patient was found
	 */
	public Patient getSelectedPatient(boolean allowEkapaSearch) {
		Patient localPatient = null;

		localPatient = PatientManager.getPatient(hSession, patientId);

		if (localPatient == null) {
			List<Patient> altPatients = PatientManager.getPatientsByAltId(hSession,null,patientId);
			if (!altPatients.isEmpty()) {
				AlternateIdSelector alt = new AlternateIdSelector(parent,
						patientId, altPatients);

				if (alt.getPatientSelected() != null) {
					localPatient = alt.getPatientSelected();
				} else {
					if (iDartProperties.isEkapaVersion) {
						MessageBox noPatient = new MessageBox(parent,
								SWT.ICON_ERROR | SWT.YES | SWT.NO);

						noPatient.setText("Patient not found in iDART");
						noPatient.setMessage("Would you like to search eKapa?");

						switch (noPatient.open()) {
						case SWT.YES:
							SearchPatientGui ps = new SearchPatientGui(
									hSession, parent, false);
							ps.searchNoGui(patientId);

							Patient p = ps.getPatient();
							// check our local database if this patient already
							// exists
							if (p != null) {
								Patient patient = PatientManager.getPatient(
										hSession, p.getPatientId());
								if (patient == null) {
									patient = p;

									MessageBox mSave = new MessageBox(parent,
											SWT.ICON_QUESTION | SWT.YES
											| SWT.NO);
									mSave.setText("Import eKapa Patient?");
									mSave
									.setMessage("Are you sure you want to import patient '"
											+ patient.getPatientId()
											+ "' ("
											+ patient.getLastname()
											+ ","
											+ patient.getFirstNames()
											+ ") into the iDART database?");
									switch (mSave.open()) {
									case SWT.YES:

										break;
									case SWT.NO:
										return null;
									}
									localPatient = patient;
								}
							}
							break;
						case SWT.NO:
							return null;
						}
					}
				}
			}

			else {
				if (iDartProperties.isEkapaVersion && allowEkapaSearch) {
					MessageBox noPatient = new MessageBox(parent,
							SWT.ICON_ERROR | SWT.YES | SWT.NO);
					noPatient.setText("Patient not in Database");
					noPatient
					.setMessage("There is no patient with ID '"
							+ patientId
							+ "' in the iDART database.\nWould you like to search eKapa?");
					switch (noPatient.open()) {
					case SWT.YES:
						SearchPatientGui ps = new SearchPatientGui(hSession,
								parent, false);
						ps.searchNoGui(patientId);
						Patient p = ps.getPatient();
						// check our local database if this patient already
						// exists
						if (p != null) {
							Patient patient = PatientManager.getPatient(
									hSession, p.getPatientId());
							if (patient == null) {
								patient = p;
								MessageBox mSave = new MessageBox(parent,
										SWT.ICON_QUESTION | SWT.YES | SWT.NO);
								mSave.setText("Import eKapa Patient?");
								mSave
								.setMessage("Are you sure you want to import patient '"
										+ patient.getPatientId()
										+ "' ("
										+ patient.getLastname()
										+ ","
										+ patient.getFirstNames()
										+ ") into the iDART database?");
								switch (mSave.open()) {
								case SWT.YES:
									break;
								case SWT.NO:
									return null;
								}
							}
							localPatient = patient;
						}
						break;
					case SWT.NO:
						return null;
					}
				} else {
					// Only if there is a patient id selected.
					if (patientId.length() > 0) {
						MessageBox noPatient = new MessageBox(parent,
								SWT.ICON_ERROR | SWT.OK);
						noPatient.setText("Patient not in Database");
						noPatient.setMessage("There is no patient with ID '"
								+ patientId + "' in the iDART database.");
						noPatient.open();
					}
				}
			}
		}
		return localPatient;
	}

}
