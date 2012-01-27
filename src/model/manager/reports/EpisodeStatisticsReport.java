package model.manager.reports;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.eclipse.swt.widgets.Shell;

public class EpisodeStatisticsReport extends AbstractJasperReport {

	private final String clinicName;
	private final int upperAgeCutOff;
	private final int lowerAgeCutOff;
	private final boolean showStartReasons;
	private final Date startDate;
	private final Date endDate;

	public EpisodeStatisticsReport(Shell parent, String clinicName,
			Date theStartDate, Date theEndDate, int upperAgeCutOff,
			int lowerAgeCutOff, boolean showStartReasons) {
		super(parent);
		this.clinicName = clinicName;
		this.upperAgeCutOff = upperAgeCutOff;
		this.lowerAgeCutOff = lowerAgeCutOff;
		this.showStartReasons = showStartReasons;
		this.startDate = getBeginningOfDay(theStartDate);
		this.endDate = getEndOfDay(theEndDate);
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		Map<String, Object> map = new HashMap<String, Object>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.YEAR, -upperAgeCutOff);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);
		cal2.add(Calendar.YEAR, -lowerAgeCutOff);

		map.put("clinicname", clinicName);
		map.put("startdate", startDate);
		map.put("enddate", endDate);
		map.put("upperagecutoffdate", new Timestamp(cal.getTimeInMillis()));
		map.put("upperagecutoff", String.valueOf(upperAgeCutOff));
		map.put("loweragecutoffdate", new Timestamp(cal2.getTimeInMillis()));
		map.put("loweragecutoff", String.valueOf(lowerAgeCutOff));
		return map;
	}

	@Override
	protected String getReportFileName() {
		return showStartReasons ? "episode_start_stats" : "episode_stop_stats";
	}

}
