package model.manager.excel.reports.in;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.manager.PatientManager;
import model.manager.excel.conversion.exceptions.PatientException;
import model.manager.excel.interfaces.ImportColumn;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.misc.iDARTUtil;

public class PatientSheet extends BaseImportSheet {
	
	private static final Logger log = Logger.getLogger(PatientSheet.class);
	
	private List<ImportColumn<?>> columns = new ArrayList<ImportColumn<?>>();

	public PatientSheet(String sheetName) {
		super(sheetName);
	}
	
	@Override
	public void init() {

		List<IdentifierType> identifierTypes = PatientManager.getAllIdentifierTypes(getSession());
		MultiImportColumn<PatientIdentifier> identifiersColumn = new MultiImportColumn<PatientIdentifier>("ID","patientIdentifiers", false);
		for (IdentifierType type : identifierTypes) {
			identifiersColumn.addColumn(new SingleImportColumn<PatientIdentifier>(type.getName(), true, new PatientIdConverter(type), new PatientIdValidator(), null));
		}
		columns.add(identifiersColumn);
		columns.add(new SingleImportColumn<String>("First Name", true, null, null,"firstNames"));
		columns.add(new SingleImportColumn<String>("Last Name", true, null, null,"lastname"));
		columns.add(new SingleImportColumn<Date>("DOB", true, new DateConverter(), null,"dateOfBirth"));
		columns.add(new SingleImportColumn<Clinic>("Clinic", false, new ClinicConverter(), null,"mostRecentEpisode.clinic"));
		columns.add(new SingleImportColumn<Character>("Sex", true, new SexConverter(), null,"sex",'U'));
		columns.add(new SingleImportColumn<String>("Address 1","address1"));
		columns.add(new SingleImportColumn<String>("Address 2","address2"));
		columns.add(new SingleImportColumn<String>("Address 3","address3"));
		columns.add(new SingleImportColumn<String>("Cell Phone Number","cellphone"));
		columns.add(new SingleImportColumn<String>("Home Phone Number","homePhone"));
		columns.add(new SingleImportColumn<String>("Work Phone Number","workPhone"));
		columns.add(new SingleImportColumn<String>("Next of kin name","nextOfKinName"));
		columns.add(new SingleImportColumn<String>("Next of kin contact number","nextOfKinPhone"));

		List<AttributeType> attTypes = PatientManager.getAllAttributeTypes(getSession());
		MultiImportColumn<PatientAttribute> attribsColumn = new MultiImportColumn<PatientAttribute>("","attributes", true);
		for (AttributeType type : attTypes) {
			attribsColumn.addColumn(new SingleImportColumn<PatientAttribute>(type.getName(), true, new PatientAttributeConverter(type), null, null));
		}
		columns.add(attribsColumn);
		columns.add(new SingleImportColumn<Date>("Episode Start Date", true, new DateConverter(), new DateValidator(),"mostRecentEpisode.startDate", new Date()));
		columns.add(new SingleImportColumn<String>("Episode Start Reason", true, null, new EpisodeStartReasonValidator(),"mostRecentEpisode.startReason",Episode.REASON_NEW_PATIENT));
		columns.add(new SingleImportColumn<String>("Episode Start Notes","mostRecentEpisode.startNotes"));
		columns.add(new SingleImportColumn<Date>("Episode Stop Date", true, new DateConverter(), new DateValidator(),"mostRecentEpisode.stopDate"));
		columns.add(new SingleImportColumn<String>("Episode Stop Reason", true, null, new EpisodeStopReasonValidator(),"mostRecentEpisode.stopReason"));
		columns.add(new SingleImportColumn<String>("Episode Stop Notes","mostRecentEpisode.stopNotes"));
		columns.add(new SingleImportColumn<String>("Province", true, null, new ProvinceValidator(),"province"));
		
	}
	
	@Override
	protected List<String> getTemplateHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		for (ImportColumn<?> col : columns) {
			String[] split = col.getHeader().split("\\|");
			for (String string : split) {
				headers.add(string);
			}
		}
		return headers;
	}
	
	@Override
	protected List<String> getTemplateCompulsoryValues() {
		ArrayList<String> headers = new ArrayList<String>();
		for (ImportColumn<?> col : columns) {
			if (col instanceof MultiImportColumn<?>){
				boolean allowBlank = col.isAllowBlank();
				int size = ((MultiImportColumn<?>)col).getSize();
				String prefix = ((MultiImportColumn<?>)col).getHeaderPrefix();
				for (int i = 0; i < size; i++) {
					headers.add(allowBlank ? "" : "At least one of the '" + prefix
							+ "' columns must be present");
				}
			} else {
				headers.add(col.isAllowBlank() ? "" : "Compulsory");
			}
		}
		return headers;
	}
	
	@Override
	protected List<String> getTemplateColumnTypes() {
		ArrayList<String> headers = new ArrayList<String>();
		for (ImportColumn<?> col : columns) {
			String description = col.getConverterDescription();
			String[] split = description.split("\\|");
			for (String string : split) {
				headers.add(string);
			}
		}
		return headers;
	}
	
	/**
	 * Reads in a specific row, if the row entries are like the ones in memory,
	 * don't add them,
	 * 
	 * @param row
	 *            int
	 * @param session
	 *            Session
	 * @param monitor
	 * @return 
	 */
	@Override
	protected boolean readRow(int rowNumber,List<String> row) {
		Patient currentPatient = new Patient();
		currentPatient.setModified('T');
		PatientManager.addEpisodeToPatient(currentPatient, new Episode());
		
		try {
	
			for (ImportColumn<?> column : columns) {
				if (column.process(row, getSession())){
					column.applyValue(currentPatient);
				}
			}
			
			Map<String, String> map = PatientManager.validateEpisode(currentPatient.getMostRecentEpisode());
			if (map.get("result").equals("false")){
				throw new PatientException(map.get("message"));
			}
			currentPatient.accountStatusCheck();
			
			currentPatient.setPatientId(currentPatient.getPreferredIdentifier().getValue());
	
			validateARVStartDate(currentPatient);
	
			currentPatient.updateClinic();

			PatientManager.savePatient(getSession(), currentPatient);
			getSession().flush();
	
			log.warn("Patient " + currentPatient.getPatientId()
						+ " has been imported successfully! ");
			return true;
		} catch (PatientException e) {
			copyRowToErrorFile(rowNumber, e.getMessage());
			return false;
		}
	}
	
	@Override
	protected void findColumns(List<String> rowList) {
		for (ImportColumn<?> column : columns) {
			column.findColumn(rowList);
		}
	}

	@Override
	public String checkColumns() {
		StringBuilder sb = new StringBuilder("Import is missing compulsory columns: ");
		boolean missingComupulsoryColumns = false;
		for (ImportColumn<?> column : columns) {
			if (!column.checkColumn()){
				sb.append(column.getHeader()).append(", ");
				log.error("Import is missing the " + column.getHeader() + " column");
				missingComupulsoryColumns = true;
			}
		}
		if (missingComupulsoryColumns){
			return sb.substring(0, sb.length()-2);
		}
		return null;
	}

	void validateARVStartDate(Patient patient) throws PatientException {
		// Check if date is after today
		PatientAttribute arvStartDateAttrib = patient.getAttributeByName(PatientAttribute.ARV_START_DATE);
		if (arvStartDateAttrib == null || arvStartDateAttrib.getObjectValue() == null){
			return;
		}
		
		Date arvStartDate = (Date) arvStartDateAttrib.getObjectValue();
		if (arvStartDate.after(new Date())) {
			throw new PatientException(
			"Invalid ARV start date. Date cannot be in the future.");
		}

		String startReason = patient.getMostRecentEpisode().getStartReason();
		Date startDate = patient.getMostRecentEpisode().getStartDate();
		//Check if the date is before or on the episode start date for new patients
		if (Episode.REASON_NEW_PATIENT.equalsIgnoreCase(startReason)) {
			if (iDARTUtil.before(startDate, arvStartDate))
				throw new PatientException("Invalid ARV start date. Date must be before or on the episode start date.");
		}
		//Check if the date is before the episode start date for new patients
		else if ("Transferred In".equalsIgnoreCase(startReason)) {
			if (!iDARTUtil.before(arvStartDate, startDate))
				throw new PatientException("Invalid ARV start date. Date must be before the transferred in date.");
		}
	}
}