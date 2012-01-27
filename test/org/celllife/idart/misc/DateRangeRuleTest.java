package org.celllife.idart.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.celllife.function.AndRule;
import org.celllife.function.DateRule;
import org.celllife.function.DateRuleFactory;
import org.celllife.function.IRule;
import org.celllife.function.DateRule.RangeSelectionEnum;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link org.celllife.function.DateRule}
 */
public class DateRangeRuleTest {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	private Date centerBoundry;
	private Date upperBoundry;
	private Date lowerBoundry;
	private Date belowLower;
	private Date aboveUpper;
	private Date centerBoundryPlus;
	private Date upperBoundryMinus;

	@BeforeClass
	public void init() throws ParseException {
		centerBoundry = sdf.parse("20080101130000");
		centerBoundryPlus = sdf.parse("20080101130001");
		upperBoundry = sdf.parse("20080102150000");
		upperBoundryMinus = sdf.parse("20080102145900");
		lowerBoundry = sdf.parse("20071231090000");
		belowLower = sdf.parse("20071230100000");
		aboveUpper = sdf.parse("20080103210000");
	}

	@Test(dataProvider = "boudryProvider")
	public void testSingleRule(Date boundry, RangeSelectionEnum restriction,
			Date candidate, boolean expected, boolean ignoreTimestamp) {
		DateRule rule = new DateRule(boundry, restriction);
		if (ignoreTimestamp) {
			rule.setDateComparator(new DateFieldComparator(
					Calendar.DAY_OF_MONTH));
		}
		boolean result = rule.evaluate(candidate);
		Assert.assertEquals(result, expected);
	}

	@DataProvider(name = "boudryProvider")
	public Object[][] getRuleParams() {
		return new Object[][] {
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE,
						centerBoundry, true, false },
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY,
						centerBoundry, false, false },
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY,
						upperBoundry, true, false },
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE,
						upperBoundry, true, false },
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY,
						lowerBoundry, false, false },
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE,
						lowerBoundry, false, false },
				{ centerBoundry, RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE,
						centerBoundry, true, false },
				{ centerBoundry, RangeSelectionEnum.BEFORE_BOUNDRY,
						centerBoundry, false, false },
				{ centerBoundry, RangeSelectionEnum.BEFORE_BOUNDRY,
						upperBoundry, false, false },
				{ centerBoundry, RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE,
						upperBoundry, false, false },
				{ centerBoundry, RangeSelectionEnum.BEFORE_BOUNDRY,
						lowerBoundry, true, false },
				{ centerBoundry, RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE,
						lowerBoundry, true, false },

				// test with and without timestamp
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY,
						centerBoundryPlus, true, false },
				{ centerBoundry, RangeSelectionEnum.AFTER_BOUNDRY,
						centerBoundryPlus, false, true },
				{ upperBoundry, RangeSelectionEnum.BEFORE_BOUNDRY,
						upperBoundryMinus, true, false },
				{ upperBoundry, RangeSelectionEnum.BEFORE_BOUNDRY,
						upperBoundryMinus, false, true },
				{ upperBoundry, RangeSelectionEnum.BEFORE_BOUNDRY_INCLUSIVE,
						upperBoundryMinus, true, false },
				{ upperBoundry, RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE,
						upperBoundryMinus, false, false },
				{ upperBoundry, RangeSelectionEnum.AFTER_BOUNDRY_INCLUSIVE,
						upperBoundryMinus, true, true } };
	}

	@Test(dataProvider = "chainRuleProvider")
	public void testChainedRule(String code, IRule<Date> rule, Date candidate,
			boolean expected) {
		if (code.equals("")) {
			System.out.println("Break point: " + code);
		}
		boolean result = rule.evaluate(candidate);
		Assert.assertEquals(result, expected);
	}

	@SuppressWarnings("unchecked")
	@DataProvider(name = "chainRuleProvider")
	public Object[][] getChainedRules() {
		return new Object[][] {
				{
						"(-*-)",
						DateRuleFactory.betweenExclusive(lowerBoundry,
								upperBoundry, false), centerBoundry, true },
				{
						"(--*)",
						DateRuleFactory.betweenExclusive(lowerBoundry,
								upperBoundry, false), upperBoundry, false },
				{
						"(*--)",
						DateRuleFactory.betweenExclusive(lowerBoundry,
								upperBoundry, false), lowerBoundry, false },
				{
						"(*--)",
						DateRuleFactory.betweenExclusive(lowerBoundry,
								upperBoundry, false), aboveUpper, false },
				{
						"*(--)",
						DateRuleFactory.betweenExclusive(lowerBoundry,
								upperBoundry, false), belowLower, false },
				{
						"-) (*-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), upperBoundry, false },
				{
						"-*) (-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), lowerBoundry, false },
				{
						"*-) (-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), belowLower, false },
				{
						"-) (-*",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), aboveUpper, false },
				{
						"-)*(-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), centerBoundry, false },
				{
						"[-*-]",
						DateRuleFactory.betweenInclusive(lowerBoundry,
								upperBoundry, false), centerBoundry, true },
				{
						"[--*]",
						DateRuleFactory.betweenInclusive(lowerBoundry,
								upperBoundry, false), upperBoundry, true },
				{
						"[*--]",
						DateRuleFactory.betweenInclusive(lowerBoundry,
								upperBoundry, false), lowerBoundry, true },
				{
						"[*--]",
						DateRuleFactory.betweenInclusive(lowerBoundry,
								upperBoundry, false), aboveUpper, false },
				{
						"*[--]",
						DateRuleFactory.betweenInclusive(lowerBoundry,
								upperBoundry, false), belowLower, false },
				{
						"-] [*-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), upperBoundry, false },
				{
						"-*] [-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), lowerBoundry, false },
				{
						"*-] [-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), belowLower, false },
				{
						"-] [-*",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), aboveUpper, false },
				{
						"-]*[-",
						DateRuleFactory.betweenExclusive(upperBoundry,
								lowerBoundry, false), centerBoundry, false },
				{
						"-]*[-",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.after(upperBoundry,
								false)), lowerBoundry, false },
				{
						"(-(*-",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.after(upperBoundry,
								false)), upperBoundry, false },
				{
						"*(-(-",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.after(upperBoundry,
								false)), belowLower, false },
				{
						"(-(-*",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.after(upperBoundry,
								false)), aboveUpper, true },
				{
						"-*]-]",
						new AndRule<Date>(DateRuleFactory.beforeInclusive(
								lowerBoundry, false), DateRuleFactory
								.beforeInclusive(upperBoundry, false)),
						lowerBoundry, true },
				{
						"-]-*]",
						new AndRule<Date>(DateRuleFactory.beforeInclusive(
								lowerBoundry, false), DateRuleFactory
								.beforeInclusive(upperBoundry, false)),
						upperBoundry, false },
				{
						"*-]-]",
						new AndRule<Date>(DateRuleFactory.beforeInclusive(
								lowerBoundry, false), DateRuleFactory
								.beforeInclusive(upperBoundry, false)),
						belowLower, true },
				{
						"-]-]*",
						new AndRule<Date>(DateRuleFactory.beforeInclusive(
								lowerBoundry, false), DateRuleFactory
								.beforeInclusive(upperBoundry, false)),
						aboveUpper, false },
				{
						"(*--]",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), lowerBoundry, false },
				{
						"(--*]",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), upperBoundry, true },
				{
						"*(--]",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), belowLower, false },
				{
						"(--]*",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), aboveUpper, false },
				{
						"(-*-]",
						new AndRule<Date>(DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), centerBoundry, true },
				{
						"(-(-*-]",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), centerBoundry, true },
				{
						"(-(--*]",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), upperBoundry, true },
				{
						"(-(*--]",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), lowerBoundry, false },
				{
						"(-(--]*",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), aboveUpper, false },
				{
						"(*-(--]",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.after(lowerBoundry,
								false), DateRuleFactory.beforeInclusive(
								upperBoundry, false)), belowLower, false },
				{
						"(-[-(-[*-",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.afterInclusive(
								lowerBoundry, false), DateRuleFactory.after(
								upperBoundry, false), DateRuleFactory
								.afterInclusive(aboveUpper, false)),
						aboveUpper, true },
				{
						"(-[-(*-[-",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.afterInclusive(
								lowerBoundry, false), DateRuleFactory.after(
								upperBoundry, false), DateRuleFactory
								.afterInclusive(aboveUpper, false)),
						upperBoundry, false },
				{
						"(-[*-(-[-",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.afterInclusive(
								lowerBoundry, false), DateRuleFactory.after(
								upperBoundry, false), DateRuleFactory
								.afterInclusive(aboveUpper, false)),
						lowerBoundry, false },
				{
						"(*-[-(-[-",
						new AndRule<Date>(DateRuleFactory.after(belowLower,
								false), DateRuleFactory.afterInclusive(
								lowerBoundry, false), DateRuleFactory.after(
								upperBoundry, false), DateRuleFactory
								.afterInclusive(aboveUpper, false)),
						belowLower, false },

		};
	}
}
