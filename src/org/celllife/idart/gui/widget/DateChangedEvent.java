package org.celllife.idart.gui.widget;

import java.io.Serializable;
import java.util.Date;

public class DateChangedEvent implements Serializable {

	private static final long serialVersionUID = -5542162434407799430L;

	private final Date previousDate;
	private final Date newDate;

	public DateChangedEvent(Date newDate, Date previousDate) {
		this.previousDate = previousDate;
		this.newDate = newDate;
	}

	public Date getNewDate() {
		return newDate;
	}

	public Date getPreviousDate() {
		return previousDate;
	}

}
