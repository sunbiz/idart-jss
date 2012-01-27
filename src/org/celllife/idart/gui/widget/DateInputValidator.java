package org.celllife.idart.gui.widget;

import java.util.Date;

import org.celllife.function.IRule;

public class DateInputValidator implements IObjectInputValidator {

	private final IRule<Date> rule;

	public DateInputValidator(IRule<Date> rule) {
		this.rule = rule;
	}

	@Override
	public String isValid(Object newObject) {
		if (newObject instanceof Date) {
			Date date = (Date) newObject;
			if (!rule.evaluate(date))
				return rule.getDescription();
		}
		return null;
	}
}
