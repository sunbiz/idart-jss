package model.manager.excel.reports.in;

import model.manager.excel.interfaces.ImportConverter;

public class IntegerConverter implements ImportConverter<Integer> {

	@Override
	public Integer convert(String rawValue) {
		try {
			return Integer.parseInt(rawValue);
		} catch (NumberFormatException ne) {
			return null;
		}
	}
	
	@Override
	public String getDescription() {
		return "A whole number";
	}

}
