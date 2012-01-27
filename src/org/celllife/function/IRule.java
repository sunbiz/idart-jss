package org.celllife.function;

/**
 * Generic interface for greating Rules.
 * 
 * @param <T>
 */
public interface IRule<T> {

	public String getDescription();

	public void setDescription(String description);

	public boolean evaluate(T candidate);
}
