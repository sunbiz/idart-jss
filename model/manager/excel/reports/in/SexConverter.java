package model.manager.excel.reports.in;

import model.manager.excel.conversion.exceptions.PatientException;
import model.manager.excel.interfaces.ImportConverter;

public class SexConverter implements ImportConverter<Character> {

	@Override
	public Character convert(String rawValue) throws PatientException {
		char result = 'U';

		rawValue = rawValue.toLowerCase();
		
		if (rawValue.startsWith("m")) {
			result = 'M';
		} else if (rawValue.startsWith("f")) {
			result = 'F';
		} else if (rawValue.startsWith("u")) {
			result = 'U';
		} else {
			throw new PatientException("Unknown value for sex.");
		}

		return result;
	}
	
	@Override
	public String getDescription() {
		return "One of the following: F, Female, M, Male, U, Unknown";
	}

}
