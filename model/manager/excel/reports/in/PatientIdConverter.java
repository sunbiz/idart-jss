package model.manager.excel.reports.in;

import model.manager.excel.interfaces.ImportConverter;

import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.PatientIdentifier;

public class PatientIdConverter implements ImportConverter<PatientIdentifier> {

	private final IdentifierType type;

	public PatientIdConverter(IdentifierType type) {
		this.type = type;
	}

	@Override
	public PatientIdentifier convert(String rawValue) {
		PatientIdentifier identifier = new PatientIdentifier();
		identifier.setType(type);
		identifier.setValue(rawValue.trim());
		return identifier;
	}
	
	@Override
	public String getDescription() {
		return "A patient number";
	}

}
