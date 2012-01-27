package org.celllife.function;

import java.util.Calendar;
import java.util.Date;

import org.celllife.function.DateRule.RangeSelectionEnum;
import org.celllife.idart.misc.DateFieldComparator;

public class DateRuleFactory {

	public static IRule<Date> after(Date boundry, boolean ignoreTimestamp) {
		DateRule rule = new DateRule(boundry, RangeSelectionEnum.AFTER_BOUNDRY);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> afterNow(boolean ignoreTimestamp) {
		DateRule rule = new DateRule(null, RangeSelectionEnum.AFTER_BOUNDRY);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> afterNowInclusive(boolean ignoreTimestamp) {
		DateRule rule = new DateRule(null,
				RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> afterInclusive(Date boundry,boolean ignoreTimestamp) {
		DateRule rule = new DateRule(boundry,
				RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> before(Date boundry,boolean ignoreTimestamp) {
		DateRule rule = new DateRule(boundry, RangeSelectionEnum.BEFORE_BOUNDRY);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> beforeNow(boolean ignoreTimestamp) {
		DateRule rule = new DateRule(null, RangeSelectionEnum.BEFORE_BOUNDRY);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> beforeNowInclusive(boolean ignoreTimestamp) {
		DateRule rule = new DateRule(null,
				RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> beforeInclusive(Date boundry,boolean ignoreTimestamp) {
		DateRule rule = new DateRule(boundry,
				RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE);
		addCustomComparator(ignoreTimestamp, rule);
		return rule;
	}

	public static IRule<Date> betweenExclusive(Date lowerBoundry,
			Date upperBoundry,boolean ignoreTimestamp) {
		return between(lowerBoundry, false, upperBoundry, false, ignoreTimestamp);
	}

	public static IRule<Date> betweenInclusive(Date lowerBoundry,
			Date upperBoundry,boolean ignoreTimestamp) {
		return between(lowerBoundry, true, upperBoundry, true, ignoreTimestamp);
	}

	@SuppressWarnings("unchecked")
	public static IRule<Date> between(Date lowerBoundry,
			boolean lowerInclusive, Date upperBoundry, boolean upperInclusive,boolean ignoreTimestamp) {
		DateRule lower = new DateRule(lowerBoundry,
				lowerInclusive ? RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE
						: RangeSelectionEnum.AFTER_BOUNDRY);
		addCustomComparator(ignoreTimestamp, lower);
		DateRule upper = new DateRule(upperBoundry,
				upperInclusive ? RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE
						: RangeSelectionEnum.BEFORE_BOUNDRY);
		addCustomComparator(ignoreTimestamp, upper);
		return new AndRule<Date>(lower, upper);
	}

	private static void addCustomComparator(boolean ignoreTimestamp,
			DateRule rule) {
		if (ignoreTimestamp) {
			rule.setDateComparator(new DateFieldComparator(
					Calendar.DAY_OF_MONTH));
		}
	}
}
