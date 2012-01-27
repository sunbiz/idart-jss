package model.manager.exports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.celllife.idart.misc.iDARTUtil;

import model.manager.exports.columns.EpisodeDetailsEnum;

public class EpisodeObject extends AbstractExportObject implements Cloneable {

	private EpisodeDetailsEnum columnEnum;
	private ArrayList<String> episodeDetails;
	private Date endDate;
	private Date startDate;

	public EpisodeObject() {
		super();
	}

	public EpisodeObject(String title, Class dataType) {
		super(title, dataType);
	}
	
	public EpisodeObject(EpisodeDetailsEnum enu){
		this(enu, null, null);
	}

	public EpisodeObject(EpisodeDetailsEnum enu, Date startDate, Date endDate) {
		super("Episode " + enu.heading, String.class);
		if (startDate != null)
			this.startDate = iDARTUtil.getBeginningOfDay(startDate);
		if (endDate != null)
			this.endDate = iDARTUtil.getEndOfDay(endDate);
		setColumnIndex(-1);
		setColumnWidth(20);
		this.columnEnum = enu;
	}

	@Override
	public Object getData(DataExportFunctions functions, int index) {
		initEpisodeDetails();
		if (columnEnum != null) {
			List<Object> details = functions.getOldestEpisodeDetailsBetweenDates(episodeDetails, startDate, endDate);
			switch (columnEnum) {
			case startDate:
				return details.get(0);
			case startNotes:
				return details.get(1);
			case startReason:
				return details.get(2);
			case stopDate:
				return details.get(3);
			case stopNotes:
				return details.get(4);
			case stopReason:
				return details.get(5);
			default:
				return null;
			}
		}
		return null;
	}

	private void initEpisodeDetails() {
		if (episodeDetails == null) {
			episodeDetails = new ArrayList<String>();
			EpisodeDetailsEnum[] values = EpisodeDetailsEnum.values();
			for (EpisodeDetailsEnum ed : values) {
				episodeDetails.add(ed.template());
			}
		}
	}
}
