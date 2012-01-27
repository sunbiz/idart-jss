package org.celllife.idart.gui.widget;

/**
 *The IObjectInputValidator is the interface for object validators.
 * @see org.idart.ui.widget.DateButton
 */
public interface IObjectInputValidator {

	/**
	 * Validates the given object.  Returns an error message to display
	 * if the new object is invalid.  Returns <code>null</code> if there
	 * is no error.  Note that the empty string is not treated the same
	 * as <code>null</code>; it indicates an error state but with no message
	 * to display.
	 *
	 * @param newObject the object to check for validity
	 *
	 * @return an error message or <code>null</code> if no error
	 */
	public String isValid(Object newObject);
}
