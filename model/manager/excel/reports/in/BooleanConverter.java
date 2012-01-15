package model.manager.excel.reports.in;

import java.util.HashSet;
import java.util.Set;

import model.manager.excel.conversion.exceptions.PatientException;
import model.manager.excel.interfaces.ImportConverter;

public class BooleanConverter implements ImportConverter<Boolean> {

	private final Set<String> trueValues = new HashSet<String>();
	private final Set<String> falseValues = new HashSet<String>();
	
	public BooleanConverter() {
		trueValues.add("y");
		trueValues.add("yes");
		trueValues.add("true");
		trueValues.add("t");
		
		falseValues.add("n");
		falseValues.add("no");
		falseValues.add("false");
		falseValues.add("f");
	}
	
	@Override
	public Boolean convert(String rawValue) throws PatientException {
		if (trueValues.contains(rawValue.toLowerCase())){
			return true;
		} else if (falseValues.contains(rawValue.toLowerCase())){
			return false;
		}
		
		throw new PatientException("Unknown boolean value: " + rawValue);
	}
	
	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder("Any of the following values: ");
		for (String s : trueValues) {
			sb.append(s).append(",");
		}
		for (String s : falseValues) {
			sb.append(s).append(",");
		}
		return sb.substring(0, sb.length()-1);
	}

}
