package org.celllife.idart.gui.utils;

import org.eclipse.swt.widgets.Text;

/**
 * Utility class for input checking on forms
 * 
 * @author melissa
 * 
 */
public class InputVerificationUtils {

	public static Integer getIntegerValue(Text txtBox)
			throws NumberFormatException {
		return Integer.parseInt(txtBox.getText());
	}

	public static Double getDoubleValue(Text txtBox)
			throws NumberFormatException {
		return Double.parseDouble(txtBox.getText());
	}

	public static boolean checkNumericValue(Text txtBox) {
		try {
			getDoubleValue(txtBox);

		} catch (NumberFormatException nfe) {

			return false;
		}
		return true;
	}

	public static boolean checkPositiveNumericValue(Text txtBox) {
		try {
			Double dbl = getDoubleValue(txtBox);

			if (dbl < 0)
				return false;

		} catch (NumberFormatException nfe) {

			return false;
		}
		return true;
	}

	public static boolean checkPositiveIntegerValue(Text txtBox) {
		try {
			Integer integer = getIntegerValue(txtBox);

			if (integer < 0)
				return false;

		} catch (NumberFormatException nfe) {

			return false;
		}
		return true;
	}
}
