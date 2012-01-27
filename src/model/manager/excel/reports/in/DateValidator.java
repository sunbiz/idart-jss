package model.manager.excel.reports.in;

import java.util.Calendar;
import java.util.Date;

import model.manager.excel.interfaces.ImportValidator;

import org.celllife.idart.misc.iDARTUtil;

public class DateValidator implements ImportValidator<Date> {

	@Override
	public String validate(Date episodeDate) {
		if (iDARTUtil.before(new Date(), episodeDate))
			return "Episode date can not be in the future.";

		// why?
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1990);
		cal.set(Calendar.DATE, 1);
		
		if (iDARTUtil.before(episodeDate, cal.getTime()))
			return "Episode date can not be before 1990";
		
		return null;
	}

}
