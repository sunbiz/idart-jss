package model.manager.excel.reports.in;

import model.manager.excel.interfaces.ImportConverter;

public class CharConverter implements ImportConverter<Character> {

	@Override
	public Character convert(String rawValue) {
		if ((rawValue != null) && (rawValue.length() == 1))
			return rawValue.charAt(0);
		return null;
	}
	
	@Override
	public String getDescription() {
		return "A single character";
	}

}
