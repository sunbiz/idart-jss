package org.celllife.idart.integration.eKapa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.misc.PatientBarcodeParser;
import org.hibernate.Session;

/**
 * The eKapa class represents the interface to the eKapa database.
 * <p>
 * There are two basic operations. The first is to search and retrieve a patient
 * from the database. The second operation is to save the dispensed drugs to the
 * database. This class facilitates both those operations using two methods.
 * <p>
 * In the future there will be a need to retrieve episodic information. The
 * interface can then be added to this class, and the implementation to the
 * {@link StoredProcs} class.
 * 
 * 
 */
public class EKapa {

	static Logger log = Logger.getLogger(EKapa.class.getName());

	/**
	 * This enum defines the three number searches that can be done on eKapa.
	 * 
	 */
	public enum NumberType {
		PWGC("Folder No", "P"), RSAID("RSA ID Number", "I"), OTHER("Passport",
				"O");

		private final String value; // name

		private final String type; // in meters

		/**
		 * Constructor for NumberType.
		 * @param value String
		 * @param type String
		 */
		NumberType(String value, String type) {
			this.value = value;
			this.type = type;
		}

		/**
		 * Method value.
		 * @return String
		 */
		public String value() {
			return value;
		}

		/**
		 * Method type.
		 * @return String
		 */
		public String type() {
			return type;
		}
	}

	/**
	 * Searches the eKapa database for a user with the specific number.
	 * <p>
	 * 
	 * @param number
	 *            a number that can identify the patient
	 * @param type NumberType
	 * @return a list of patients retrieved from the database
	 * @throws SQLException
	 *             when the method cannot connect to the ekapa database
	 * @exception null
	 *                pointer if either of the parameters are null, or if field
	 *                value doesn't exist
	 */
	public static List<Patient> search(String number, NumberType type)
			throws SQLException {

		if (type == null)
			throw new NullPointerException();
		if (number == null)
			throw new NullPointerException();

		// now we have the number and field, lets search
		StoredProcs sp = new StoredProcs();
		boolean connected = sp.init();
		if (!connected)
			return new ArrayList<Patient>();

		ResultSet rs = sp.search(number, type);
		if (rs == null) {
			sp.closeConnection();
			return new ArrayList<Patient>();
		}
		List<Patient> patients = populatePatients(rs);
		sp.closeConnection();
		return patients;
	}

	/**
	 * Method getNonEkapaPatients.
	 * @param hSession Session
	 * @param clinic Clinic
	 * @return List<Patient>
	 * @throws SQLException
	 */
	public static List<Patient> getNonEkapaPatients(Session hSession,
			Clinic clinic) throws SQLException {

		StoredProcs sp = new StoredProcs();
		boolean connected = sp.init();
		if (!connected)
			return new ArrayList<Patient>();

		List<Patient> patients = PatientManager
				.getAllPatients(hSession, clinic);
		List<Patient> nonEkapaPatients = new ArrayList<Patient>();

		for (Patient p : patients) {
			// first eliminates all non-numeric ids
			if (!p.getPatientId().toUpperCase().equals(
					PatientBarcodeParser.getPatientId(p.getPatientId()
							.toUpperCase())))
				nonEkapaPatients.add(p);
			else if (search(p.getPatientId(), NumberType.PWGC).size() == 0)
				nonEkapaPatients.add(p);
		}

		sp.closeConnection();
		return nonEkapaPatients;
	}

	/**
	 * Searches the eKapa database for patients matching the given criteria. The
	 * compulsory fields are as follows: firstName, surname, dateOfBirth.
	 * 
	 * @param firstName
	 *            the first name of the patient. At least one character must be
	 *            given
	 * @param surname
	 *            the last name of the patient. At least two characters must be
	 *            given
	 * @param dateOfBirth
	 *            a date in the format of yyyyMMdd. The calling method must
	 *            ensure that this is the correct format, otherwise the search
	 *            will fail. There is no way to enforce this as yet.
	 * @param ageRange
	 *            This is a number between 0-9. It is the variation of the years
	 *            in which to search. For example a date of 1980 with an age
	 *            Range of 3 will search from 1977 to 1983.
	 * @param sex
	 *            Either m or f
	 * @return a list of Patient objects found in the search.
	 * @throws SQLException
	 *             when the method cannot connect to the ekapa database
	 */
	public static List<Patient> search(String firstName, String surname,
			String dateOfBirth, int ageRange, char sex) throws SQLException {
		StoredProcs sp = new StoredProcs();

		boolean connected = sp.init();
		if (!connected)
			return new ArrayList<Patient>();

		ResultSet rs = sp.search(firstName, surname, dateOfBirth, ageRange,
				sex, 0);

		if (rs == null) {
			sp.closeConnection();
			return new ArrayList<Patient>();
		}
		List<Patient> patients = populatePatients(rs);
		sp.closeConnection();
		return patients;
	}

	/**
	 * Populates a list of <source>Patient</source> objects from the results
	 * retrieved from the database
	 * 
	 * @param rs
	 * @return List<Patient>
	 */
	private static List<Patient> populatePatients(ResultSet rs) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Patient> patients = new ArrayList<Patient>();
		Patient p = null;
		try {
			while (rs.next()) {
				// convert date
				String tmp = rs.getString("DATE_OF_BIRTH");
				Date date = null;
				try {
					date = df.parse(tmp.substring(0, 10));
				} catch (Exception err) {
					log.error("Date conversion error", err);
				}

				// populate each patient
				p = new Patient();
				p.setFirstNames(rs.getString("FIRST_NAMES"));
				p.setLastname(rs.getString("SURNAME"));
				p.setDateOfBirth(date);
				p.setSex(rs.getString("SEX").charAt(0));
				p.setPatientId(rs.getString("PAWC_PATIENT_ID"));
				if (LocalObjects.nationalIdentifierType != null){
					p.getPatientIdentifiers().add(
							new PatientIdentifier(p, rs.getString("ID_NUMBER"),
									LocalObjects.nationalIdentifierType));
				}
				p.setHomePhone(rs.getString("HOME_NUMBER"));
				p.setWorkPhone(rs.getString("WORK_NUMBER"));
				p.setCellphone(rs.getString("CELL_NUMBER"));
				p.setAddress1(rs.getString("HOME_ADDRESS_1"));
				p.setAddress2(rs.getString("HOME_ADDRESS_2"));
				p.setAddress3(rs.getString("HOME_ADDRESS_3"));
				p.setAddress2(p.getAddress2() + rs.getString("POST_CODE"));
				p.setModified('T');

				patients.add(p);
			}
		} catch (SQLException e) {
			log.error("SQL error in writing patient", e);
		}
		return patients;
	}
}
