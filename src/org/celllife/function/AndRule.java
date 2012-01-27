package org.celllife.function;

public class AndRule<T> extends AbstractRule<T> {
	private final IRule<? super T>[] rules;

	public AndRule(IRule<? super T>... argRules) {
		this.rules = argRules;
	}

	@Override
	public boolean evaluate(T candidate) {
		boolean evaluation = true;
		for (IRule<? super T> rule : rules) {
			evaluation &= rule.evaluate(candidate);
		}
		return evaluation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.function.AbstractRule#getDescription(java.lang.Object)
	 */
	@Override
	public String getDescription() {
		String desc = rules[0].getDescription();
		for (int i = 1; i < rules.length; i++) {
			desc += " AND " + rules[i].getDescription();
		}
		return desc;
	}
}
