package model.manager.excel.reports.in;

import model.manager.excel.interfaces.ImportConverter;

public class DoubleConverter implements ImportConverter<Double> {

	@Override
	public Double convert(String rawValue) {
		try {
			return Double.parseDouble(rawValue);
		} catch (NumberFormatException ne) {
			return null;
		}
	}
	
	@Override
	public String getDescription() {
		return "A number";
	}

}
