package model.manager.excel.reports.in;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.manager.excel.interfaces.ImportConverter;

import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.misc.iDARTUtil;

public class DateConverter implements ImportConverter<Date> {
	
	private final SimpleDateFormat sdf = new SimpleDateFormat(iDartProperties.importDateFormat);
	
	@Override
	public Date convert(String rawValue) {
		Date date = null;

		if (rawValue == null)
			return date;

		date = iDARTUtil.parse(Date.class, rawValue);
		
		if (date == null)
			date = parse(null, rawValue);
		
		if (date == null)
			date = parse("yyyy/MM/dd", rawValue);
		
		if (date == null)
			date = parse("dd-MM-yyyy", rawValue);
			
		return date;
	}

	private Date parse(String pattern, String rawValue) {
		if (pattern != null){
			sdf.applyPattern(pattern);
		}
		try {
			return sdf.parse(rawValue);
		} catch (ParseException pee) {
			return null;
		}
	}
	
	@Override
	public String getDescription() {
		return "A date";
	}
}
