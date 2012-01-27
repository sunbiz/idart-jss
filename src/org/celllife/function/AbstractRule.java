package org.celllife.function;

public abstract class AbstractRule<T> implements IRule<T> {

	protected String description;

	public AbstractRule() {
		super();
	}

	public String getDescription(Object boundryFormat) {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean evaluate(T candidate) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescription(null);
	}

}