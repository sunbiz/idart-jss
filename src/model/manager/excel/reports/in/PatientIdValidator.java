package model.manager.excel.reports.in;

import java.text.MessageFormat;

import model.manager.PatientManager;
import model.manager.excel.interfaces.SessionBasedImportValidator;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.Session;

public class PatientIdValidator implements SessionBasedImportValidator<PatientIdentifier> {

	private Session session;

	@Override
	public String validate(PatientIdentifier value){
		if (value.getValue() == null || value.getValue().isEmpty())
			return "Patient Number can not be empty";
		
		String illegalText = iDARTUtil.checkPatientId(value.getValue());
		if(illegalText != null){
			return MessageFormat.format(Messages.getString("patient.error.badCharacterInPatientId"), //$NON-NLS-1$
					iDartProperties.illegalPatientIdChars);
		}
		
		if (PatientManager.checkPatientIdentifier(session, value.getPatient(), value.getType(), value.getValue())){
			return "Another patient already has an identifier of type '" +
				value.getType().getName() + "' with value '"
				+ value.getValue() + "'";
		}
		
		return null;
	}
	
	@Override
	public void initialise(Session hsession) {
		this.session = hsession;
	}
}
