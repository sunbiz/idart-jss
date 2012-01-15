package org.celllife.function;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.celllife.idart.database.hibernate.APIException;
import org.celllife.idart.misc.DateFieldComparator;
import org.celllife.idart.misc.iDARTUtil;

/**
 * <code>DateRangeRule</code> represents a rule that can be evaluated against a
 * given date to determine whether the given date falls within the defined
 * range.
 * 
 * A <code>DateRangeRule</code> consists of a Date representing a boundry, a
 * boolean which denotes whether that boundry is inclusive or not and a
 * <code>RangeSelectionEnum</code> which denotes which side of the boudry is
 * valid.
 * 
 * Example usage:
 * 
 * <pre>
 * /// define range before boundry and exclude boundry as part of range.
 * DateRangeRule beforeExclusive = new DateRangeRule(false, boundry,
 * 		RangeSelectionEnum.BEFORE_BOUNDRY);
 * /// define range between upper and lower boundry of which both are inclusive
 * DateRangeRule lower = new DateRangeRule(inclusiveLower, lowerBoundry,
 * 		RangeSelectionEnum.AFTER_BOUNDRY);
 * DateRangeRule upper = new DateRangeRule(inclusiveUpper, upperBoundry,
 * 		RangeSelectionEnum.BEFORE_BOUNDRY);
 * IRule&lt;Date&gt; between = new AndRule&lt;Date&gt;(lower, upper);
 * </pre>
 * 
 * @see DateRuleFactory
 * @see IRule
 */
public class DateRule extends AbstractRule<Date> {

	public enum RangeSelectionEnum {
		BEFORE_BOUNDRY,

		BEFORE_BOUNDRY_INCLUSIVE,

		AFTER_BOUNDRY,

		AFTER_BOUNDRY_INCLUSIVE,
	}

	private Date boundry;
	private RangeSelectionEnum rangeSelection;
	private Comparator<Date> comparator;

	public DateRule() {
		super();
	}

	public DateRule(Date boundry, RangeSelectionEnum restriction) {
		super();
		this.boundry = boundry;
		this.rangeSelection = restriction;
	}

	@Override
	public boolean evaluate(Date candidate) {
		boolean evaluation;
		Date boundryEvaluation;
		if (boundry == null) {
			boundryEvaluation = new Date();
		} else {
			boundryEvaluation = boundry;
		}
		switch (rangeSelection) {
		case BEFORE_BOUNDRY:
			evaluation = compare(candidate, boundryEvaluation) < 0;
			break;
		case BEFORE_BOUNDRY_INCLUSIVE:
			evaluation = compare(candidate, boundryEvaluation) <= 0;
			break;
		case AFTER_BOUNDRY:
			evaluation = compare(candidate, boundryEvaluation) > 0;
			break;
		case AFTER_BOUNDRY_INCLUSIVE:
			evaluation = compare(candidate, boundryEvaluation) >= 0;
			break;
		default:
			throw new APIException("Unknown date restriction.");
		}

		return evaluation;
	}

	private int compare(Date candidate, Date boundryEvaluation) {
		if (comparator == null)
			return candidate.compareTo(boundryEvaluation);
		else
			return comparator.compare(candidate, boundryEvaluation);
	}

	public Date getBoundry() {
		return boundry == null ? new Date() : boundry;
	}

	/**
	 * Generates a text message that describes this restriction or uses the
	 * description if one has been set.
	 * 
	 * @param fmt
	 * @return description of the restriction.
	 */
	@Override
	public String getDescription() {
		String date = "";
		if (description != null && description.length() > 0)
			return parseDescriptionAndInsertDate(iDARTUtil.format(boundry));

		if (boundry == null
				|| DateFieldComparator.compare(boundry, new Date(),
						Calendar.DAY_OF_MONTH) == 0) {
			date = "today";
		} else {
			date = iDARTUtil.format(boundry);
		}

		String message = "The date must be ";
		switch (rangeSelection) {
		case AFTER_BOUNDRY:
			message += "after ";
			break;
		case AFTER_BOUNDRY_INCLUSIVE:
			message += "on or after ";
			break;
		case BEFORE_BOUNDRY:
			message += "before ";
			break;
		case BEFORE_BOUNDRY_INCLUSIVE:
			message += "on or before ";
			break;
		default:
			break;
		}

		return message + date;
	}

	private String parseDescriptionAndInsertDate(String date) {
		return description.replaceAll("<date>", date);
	}

	public RangeSelectionEnum getRangeSelection() {
		return rangeSelection;
	}

	public void setBoundry(Date boundry) {
		this.boundry = boundry;
	}

	public void setRangeSelection(RangeSelectionEnum rangeSelection) {
		this.rangeSelection = rangeSelection;
	}

	public void setDateComparator(Comparator<Date> comparator) {
		this.comparator = comparator;
	}
}
