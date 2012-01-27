package org.celllife.idart.integration.eKapa;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import model.manager.DrugManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.database.hibernate.tmp.DeletedItem;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;

/**
 * This class contains the methods to call the relevant stored procedures from
 * an oracle database. It should only be called from the {@link EKapa} class.
 * <p>
 * This class is needed for the integration of iDart with the eKapa system. The
 * specifications are based on the ICD of eKapa II.
 * 
 * All information needs to be converted to uppercase before it is inserted into
 * the eKapa database.
 * 
 * @see EKapa
 */
public class StoredProcs {

	private static Logger log = Logger.getLogger(StoredProcs.class.getName());

	private Connection connection;

	private String username;

	private String password;

	private String driver;

	private String dialect;

	private String url;

	private Session hSession = null;

	public StoredProcs() {
		hSession = HibernateUtil.getNewSession();
	}

	private void getConnectionSettings() {
		username = iDartProperties.eKapa_user;
		password = iDartProperties.eKapa_password;
		String tmpurl = iDartProperties.eKapa_dburl;
		String port = iDartProperties.eKapa_dbport;
		String type = iDartProperties.eKapa_dbtype;
		String dbName = iDartProperties.eKapa_dbname;
		if (type.toLowerCase().equals("postgresql")) {
			dialect = "org.hibernate.dialect.PostgreSQLDialect";
			driver = "org.postgresql.Driver";
			url = "jdbc:postgresql://" + tmpurl + ":" + port + "/" + dbName;
		} else if (type.toLowerCase().equals("oracle")) {
			dialect = "org.hibernate.dialect.OracleDialect";
			driver = "oracle.jdbc.driver.OracleDriver";
			url = "jdbc:oracle:thin:@" + tmpurl + ":" + port + ":" + dbName;
		}
		log.debug("Connecting to ekapa database with:");
		log.debug("\tUsername : " + username);
		log.debug("\tPassword : " + password);
		log.debug("\tDialect  : " + dialect);
		log.debug("\tURL      : " + url);
		log.debug("\tDriver   : " + driver);
	}

	/**
	 * Initialise the connection to the database
	 * 
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean init() throws SQLException {
		try {
			log.debug("Trying to establish stored procs connection to eKapa");

			getConnectionSettings();
			Class.forName(driver);
			DriverManager.setLoginTimeout(2);
			connection = DriverManager.getConnection(url, username, password);

		} catch (ClassNotFoundException e) {
			log.fatal("Could not find driver class", e);
		}
		return (connection == null) ? false : true;

	}

	public void closeConnection() {
		if (connection == null)
			return;
		try {
			connection.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			connection = null;
		}
	}

	/**
	 * This method corresponds to 2.8.2 in the ekapaII ICD
	 * 
	 * Calls a stored procedure to search the eKapa database for patients
	 * matching the parameters
	 * 
	 * @param folderNo
	 *            - The search number
	 * @param numberType
	 *            - One of the global class strings defined at the top of this
	 *            class
	 * @return - ResultSet containing the search results
	 */
	public ResultSet search(String folderNo, EKapa.NumberType numberType) {
		if (connection == null)
			return null;

		ResultSet rs = null;
		try {
			// call the stored proc search_number
			CallableStatement cs = connection
			.prepareCall("{ call patient.search_number(?,?,?) }");
			cs.setString(1, folderNo);
			cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
			cs.setString(3, numberType.type());
			cs.execute();

			// receive the result from the stored proc
			rs = (ResultSet) cs.getObject(2);
			cs.close();
		} catch (SQLException e) {
			log.error("SQL error in searching for patient", e);
		}
		return rs;
	}

	/**
	 * This method corresponds to 2.8.1 in the ekapaII ICD
	 * 
	 * Calls a stored procedure to search the eKapa database for patients
	 * matching the parameters
	 * 
	 * @param firstName
	 *            String
	 * @param lastName
	 *            String
	 * @param dateOfBirth
	 *            String
	 * @param ageRange
	 *            int
	 * @param gender
	 *            char
	 * @param sequence
	 *            int
	 * @return ResultSet
	 */
	public ResultSet search(String firstName, String lastName,
			String dateOfBirth, int ageRange, char gender, int sequence) {
		if (connection == null)
			return null;
		// convert the parameters to uppercase
		firstName = firstName.toUpperCase();
		lastName = lastName.toUpperCase();
		gender = Character.toUpperCase(gender);
		String errorMessage = null;
		if ((errorMessage = searchOk(firstName, lastName, dateOfBirth,
				ageRange, gender, sequence)) != null) {
			// print out error message and quit
			log.error("Alpha Search\n" + errorMessage);
			return null;
		}

		ResultSet rs = null;
		try {
			// call the stored proc search_alpha
			CallableStatement cs = connection
			.prepareCall("{ call patient.search_alpha(?,?,?,?,?,?,?) }");
			cs.setString(1, lastName);
			cs.setString(2, firstName);
			cs.setString(3, dateOfBirth);
			cs.registerOutParameter(4, oracle.jdbc.OracleTypes.CURSOR);

			// Deal with optional variables
			cs.setString(5, gender + "");
			cs.setInt(6, sequence);
			cs.setInt(7, ageRange);

			cs.execute();

			// receive the result from the stored proc
			rs = (ResultSet) cs.getObject(4);
			cs.close();
		} catch (SQLException e) {
			log.error("SQL error in searching for patient", e);
		}

		return rs;
	}

	/**
	 * Check information for submitPill count.
	 * 
	 * @param refNo
	 * @param pawcNumber
	 * @param countDate
	 * @param daysSinceVisit
	 * @param daysSupplied
	 * @param daysAccumulated
	 * @param daysInHand
	 * @param adherence
	 * @param user
	 * @return String
	 */
	private String searchOk(int refNo, String pawcNumber, Date countDate,
			int daysSinceVisit, int daysSupplied, int daysAccumulated,
			int daysInHand, double adherence, String user) {
		if (refNo <= 0)
			return ("Internal error. The reference number cannot be less that 0");
		if (pawcNumber.equals(""))
			return ("Internal error. A valid PAWC number must be issued");
		if (daysSinceVisit < 0)
			return ("Internal error. Days since visit cannot be less than 0");
		if (daysSupplied < 0)
			return ("Internal error. Days supplied must be greater than 0");
		if (daysAccumulated < 0)
			return ("Internal error. Days accumulated must be greater than 0");
		if (daysInHand < 0)
			return ("Internal error. Days in hand must be greater than 0");
		if (adherence < 0)
			return ("Internal error. Adherence cannot be less than 0");
		return null;
	}

	/**
	 * Checks all the alpha search parameters. Returns error message on error,
	 * and null on ok.
	 * 
	 * @param firstName
	 * @param lastName
	 * @param dateOfBirth
	 * @param ageRange
	 * @param gender
	 * @param sequence
	 * @return String
	 */
	private String searchOk(String firstName, String lastName,
			String dateOfBirth, int ageRange, char gender, int sequence) {
		// Error checks
		if (lastName.length() < 2)
			return "Last name requires a minimum of 2 letters";
		else if (firstName.length() < 1)
			return "First name requires a minimum of 1 letter";
		else if (dateOfBirth.length() < 4)
			return "Date of birth must contain at least the year (4 digits)";
		else if (!((dateOfBirth.length() == 4) || (dateOfBirth.length() == 6) || (dateOfBirth
				.length() == 8)))
			return "Date of birth must contain 4 or 6 or 8 digits (yyyymmdd)";
		else if ((gender != 'M') && (gender != 'F'))
			return "The sex must be either m or f";
		else if (sequence < 0)
			return "The sequence must be a positive number";
		else if ((ageRange < 0) || (ageRange > 9))
			return "The age range must be between 0 and 9";
		return null;
	}

	/**
	 * Checks all the dispensing info parameters.
	 * 
	 * @param refNo
	 * @param pawcNumber
	 * @param tradeName
	 * @param strength
	 * @param chemicalName
	 * @param packSize
	 * @param quantity
	 * @param repeats
	 * @param unitsPerDose
	 * @param doseUnits
	 * @param doseRoute
	 * @param doseInterval
	 * @param instructions
	 * @param indication
	 * @param user
	 * @return String
	 */
	private String searchOk(int refNo, String pawcNumber, String tradeName,
			double strength, String chemicalName, int packSize, int quantity,
			int repeats, double unitsPerDose, String doseUnits,
			String doseRoute, String doseInterval, String instructions,
			String indication, String user) {

		if (refNo <= 0)
			return ("Internal error. The reference number cannot be less that 0");
		if (pawcNumber.equals(""))
			return ("Internal error. A valid PAWC number must be issued");
		if (tradeName.equals(""))
			return ("Internal error. A valid trade name must be issued");
		if (strength < 0)
			return ("Internal error. Strength must be greater than 0");
		if (chemicalName.equals(""))
			return ("Internal error. A valid chemical name must be issued");
		if (packSize < 0)
			return ("Internal error. Pack size must be greater than 0");
		if (repeats < 0)
			return ("Internal error. Repeats cannot be less than 0");
		if (unitsPerDose < 0)
			return ("Internal error. Units per dose must be greater than 0");
		if (doseUnits.equals(""))
			return ("Internal error. Dose units must be indicated");
		if (doseRoute.equals(""))
			return ("Internal error. Dose route must be indicated");
		if (doseInterval.equals(""))
			return ("Internal error. Dose interval must be indicated");
		// if (instructions.equals(""))
		// return ("Internal error. Valid instructions must be indicated");
		// if (indication.equals(""))
		// return ("Internal error. A valid indication must be given");
		return null;
	}

	/**
	 * This method corresponds to 2.8.1 in the ekapaII ICD *
	 * 
	 * Calls a stored procedure to search the eKapa database for patients
	 * matching the parameters
	 * 
	 * @param refNo
	 *            int
	 * @param pawcNumber
	 *            String
	 * @param tradeName
	 *            String
	 * @param strength
	 *            int
	 * @param chemicalName
	 *            String
	 * @param packSize
	 *            int
	 * @param quantity
	 *            int
	 * @param repeats
	 *            int
	 * @param unitsPerDose
	 *            double
	 * @param doseUnits
	 *            String
	 * @param doseRoute
	 *            String
	 * @param doseInterval
	 *            String
	 * @param doseDuration
	 *            int
	 * @param instructions
	 *            String
	 * @param indication
	 *            String
	 * @param user
	 *            String
	 * @param dispDate
	 *            Date
	 * @return ResultSet containing the search results
	 * @throws SQLException
	 */
	private boolean submitDispensingInfo(int refNo, String pawcNumber,
			String tradeName, double strength, String chemicalName,
			int packSize, int quantity, int repeats, double unitsPerDose,
			String doseUnits, String doseRoute, String doseInterval,
			int doseDuration, String instructions, String indication,
			String user, Date dispDate) throws SQLException {

		String errorMessage = null;
		if ((errorMessage = searchOk(refNo, pawcNumber, tradeName, strength,
				chemicalName, packSize, quantity, repeats, unitsPerDose,
				doseUnits, doseRoute, doseInterval, instructions, indication,
				user)) != null) {
			// print out error message and quit
			log.error("Submit Dispensing Information\n" + errorMessage);
			return false;
		}

		// convert strings to uppercase
		pawcNumber = pawcNumber.toUpperCase();
		tradeName = tradeName.toUpperCase();
		chemicalName = chemicalName.toUpperCase();
		doseUnits = doseUnits.toUpperCase();
		doseRoute = doseRoute.toUpperCase();
		doseInterval = doseInterval.toUpperCase();
		instructions = instructions.toUpperCase();
		indication = indication.toUpperCase();
		user = user.toUpperCase();

		// sorting out each paramater for the proc
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy"); // @jve:decl-
		// index=0:
		Date saveDate = new Date();
		int returned = -1;
		log.info("submitting eKapa dispensing record for: " + tradeName);

		// call the stored proc search_number
		CallableStatement cs = connection
		.prepareCall("{ call dispensing.save_dispensed_item(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");

		cs.registerOutParameter(19, java.sql.Types.INTEGER);

		cs.setInt(1, refNo); // reference id - for updating info later
		cs.setString(2, pawcNumber); // pawc number
		cs.setString(3, tradeName); // trade name
		cs.setDouble(4, strength); // trade strength
		cs.setString(5, chemicalName); // chemical name
		cs.setInt(6, packSize); // trade pack size
		cs.setInt(7, quantity); // dispensed quantity (in dosing units)
		cs.setString(8, df.format(dispDate)); // date dispensed
		cs.setInt(9, repeats); // repeat number
		cs.setDouble(10, unitsPerDose); // units per dose
		cs.setString(11, doseUnits); // dose units (tablets etc)
		cs.setString(12, doseRoute); // dose route (per mouth)
		cs.setString(13, doseInterval); // interval (3 times per day)
		cs.setFloat(14, doseDuration); // duration in days
		cs.setString(15, instructions); // special instructions (with meals)
		cs.setString(16, indication); // indication (for pain)
		cs.setString(17, df.format(saveDate)); // date created
		cs.setString(18, user); // user - name of user who created
		cs.setInt(19, -1);

		cs.execute();

		// receive the result from the stored proc

		returned = cs.getInt(19);

		cs.close();
		return (returned >= 0 ? true : false);
	}

	/**
	 * This is the public interface for the iDART patientPackaging gui.
	 * 
	 * @param pdi
	 *            PackageDrugInfo
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean submitDispensingInfo(PackageDrugInfo pdi)
	throws SQLException {
		if (connection == null)
			return false;
		int refNo = 0;
		String tradeName = "";
		double strength = 0;
		String chemicalName = "";
		String pawcNo = "";
		int quantity = 0;
		int packSize = 0;
		int repeat = 0;
		Double unitsPerDose = new Double(0);
		int doseDuration = 0;
		String doseUnits = "";
		String doseRoute = "per mouth";
		String doseInterval = "";
		String instructions = "";
		String indication = "NOT AVAILABLE";
		String user = "";
		Date dispensedDate;

		Drug td = null;
		// populate fields for the stored proc

		refNo = pdi.getPackagedDrug().getId();
		quantity = pdi.getDispensedQty();

		tradeName = pdi.getDrugName();
		log.info("preparing eKapa dispensing record for: " + tradeName);
		td = DrugManager.getDrug(hSession, tradeName);

		pawcNo = pdi.getPatientId();
		user = pdi.getCluser().getUsername();
		packSize = td.getPackSize();
		unitsPerDose = Double.valueOf(pdi.getAmountPerTime());

		dispensedDate = pdi.getDispenseDate();

		instructions = pdi.getSpecialInstructions1()
		+ pdi.getSpecialInstructions2();
		doseUnits = pdi.getFormLanguage1();
		repeat = pdi.getPackageIndex();
		doseInterval = "" + pdi.getTimesPerDay();
		doseDuration = pdi.getWeeksSupply() * 7;

		Set<ChemicalDrugStrength> cds = td.getChemicalDrugStrengths();

		if (cds.size() == 0) // submit but don't know strength or chemical
			// drug
		{
			if (!submitDispensingInfo(refNo, pawcNo, tradeName, 0, "Unknown",
					packSize, quantity, repeat, unitsPerDose, doseUnits,
					doseRoute, doseInterval, doseDuration, instructions,
					indication, user, dispensedDate))
				return false;
		}
		Iterator<ChemicalDrugStrength> it = cds.iterator();
		boolean alreadySubmittedQuantity = false;

		while (it.hasNext()) {
			ChemicalDrugStrength cd = it.next();
			chemicalName = cd.getChemicalCompound().getName();
			strength = cd.getStrength();

			if (alreadySubmittedQuantity) {
				quantity = 0;
			}

			if (log.isDebugEnabled()) {
				log.debug(refNo + ":" + pawcNo + ":" + tradeName + ":"
						+ strength + ":" + chemicalName + ":" + packSize + ":"
						+ quantity + ":" + repeat + ":" + unitsPerDose + ":"
						+ doseUnits + ":" + doseRoute + ":" + doseInterval
						+ ":" + doseDuration + ":" + instructions + ":"
						+ indication + ":" + user + ":" + dispensedDate);
			}

			if (!submitDispensingInfo(refNo, pawcNo, tradeName, strength,
					chemicalName, packSize, quantity, repeat, unitsPerDose,
					doseUnits, doseRoute, doseInterval, doseDuration,
					instructions, indication, user, dispensedDate))
				return false;
			alreadySubmittedQuantity = true;

		}
		return true;

	}

	/**
	 * Submit pill count to eKapa database according to section 2.8.6 in the
	 * eKapa II ICD.
	 * 
	 * <pre>
	 *   PROCEDURE save_pill_count (
	 *       p_dispensing_id          IN       NUMBER,
	 *       p_pawc_patient_id        IN       VARCHAR2,
	 *       p_date_counted           IN       DATE,
	 *       p_days_elapsed           IN       NUMBER,
	 *       p_days_supplied          IN       NUMBER,
	 *       p_days_accumalated       IN       NUMBER,
	 *       p_days_in_hand           IN       NUMBER,
	 *       p_percentage_adherance   IN       NUMBER,
	 *       p_deviation_reason       IN       VARCHAR2,
	 *       p_date_created           IN       DATE,
	 *       p_user_created           IN       VARCHAR2,
	 *       p_result                 OUT      INTEGER
	 *    )
	 * </pre>
	 * 
	 * @param refNo
	 * @param pawcNumber
	 * @param countDate
	 * @param daysSinceVisit
	 * @param daysSupplied
	 * @param daysAccumulated
	 * @param daysInHand
	 * @param adherence
	 * @param adherenceReason
	 *            String
	 * @param user
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean submitPillCount(int refNo, String pawcNumber,
			Date countDate, int daysSinceVisit, int daysSupplied,
			int daysAccumulated, int daysInHand, double adherence,
			String adherenceReason, String user) throws SQLException {

		String errorMessage = null;
		if ((errorMessage = searchOk(refNo, pawcNumber, countDate,
				daysSinceVisit, daysSupplied, daysAccumulated, daysInHand,
				adherence, user)) != null) {
			// print out error message and quit
			log.error("Submit Pill Count\n" + errorMessage);
			return false;
		}

		// convert strings to upper case
		pawcNumber = pawcNumber.toUpperCase();
		user = user.toUpperCase();

		// sorting out each paramater for the proc
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy"); // @jve:decl-
		// index=0:
		int returned = -1;
		// call the stored proc search_number
		CallableStatement cs = connection
		.prepareCall("{ call dispensing.save_pill_count(?,?,?,?,?,?,?,?,?,?,?,?) }");

		cs.registerOutParameter(12, java.sql.Types.INTEGER);

		cs.setInt(1, refNo); // reference id
		cs.setString(2, pawcNumber); // pawc number
		cs.setString(3, df.format(countDate)); // date counted
		cs.setInt(4, daysSinceVisit); // days since patient's last visit
		cs.setInt(5, daysSupplied); // days dispensed at this visit
		cs.setInt(6, daysAccumulated); // days accumulated so far
		cs.setInt(7, daysInHand); // days all drugs will last
		cs.setFloat(8, (float) adherence); // percent
		cs.setString(9, adherenceReason);
		cs.setString(10, df.format(countDate)); // date created ??? why
		cs.setString(11, user); // user who created the entry
		cs.setInt(12, -1);

		cs.execute();

		returned = cs.getInt(12);

		cs.close();

		return (returned >= 0 ? true : false);

	}

	/**
	 * Public interface
	 * 
	 * @param adh
	 *            AdherenceRecord
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean submitPillCount(AdherenceRecord adh) throws SQLException {
		if (connection == null)
			return false;

		if (!submitPillCount(adh.getPillCountId(), adh.getPawcNo(), adh
				.getCountDate(), adh.getDaysSinceVisit(), adh
				.getDaysSinceVisit(), adh.getDaysCarriedOver(), adh
				.getDaysInHand(), adh.getAdherence(), "Not Available", adh
				.getCluser()))
			return false;

		return true;
	}

	/**
	 * Method deleteAdherenceRecord.
	 * 
	 * @param id
	 *            int
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean deleteAdherenceRecord(int id) throws SQLException {

		int returned = -1;
		// call the stored proc search_number
		CallableStatement cs = connection
		.prepareCall("{ call dispensing.del_pill_count(?,?) }");

		cs.registerOutParameter(2, java.sql.Types.INTEGER);

		cs.setInt(1, id); // reference id
		cs.setInt(2, -1);

		cs.execute();

		returned = cs.getInt(2);

		cs.close();

		return (returned >= 0 ? true : false);

	}

	/**
	 * Public interface
	 * 
	 * @param del
	 *            DeletedItem
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean deleteAdherenceRecord(DeletedItem del) throws SQLException {
		if (connection == null)
			return false;

		if (!deleteAdherenceRecord(del.getDeletedItemId()))
			return false;

		return true;
	}

	/**
	 * Method deleteDispensingRecord.
	 * 
	 * @param id
	 *            int
	 * @return boolean
	 * @throws SQLException
	 */
	private boolean deleteDispensingRecord(int id) throws SQLException {

		int returned = -1;
		// call the stored proc search_number
		CallableStatement cs = connection
		.prepareCall("{ call dispensing.del_dispensed_item(?,?) }");

		cs.registerOutParameter(2, java.sql.Types.INTEGER);

		cs.setInt(1, id); // reference id
		cs.setInt(2, -1);

		cs.execute();

		returned = cs.getInt(2);

		cs.close();

		return (returned >= 0 ? true : false);

	}

	/**
	 * Public interface
	 * 
	 * @param del
	 *            DeletedItem
	 * @return boolean
	 * @throws SQLException
	 */
	public boolean deleteDispensingRecord(DeletedItem del) throws SQLException {
		if (connection == null)
			return false;

		if (!deleteDispensingRecord(del.getDeletedItemId()))
			return false;

		return true;
	}

	@Override
	public void finalize() {
		hSession.close();
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			} 
		}
	}
}
