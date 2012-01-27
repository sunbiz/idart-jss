package org.celllife.idart.misc;

import java.lang.reflect.InvocationTargetException;

import org.celllife.idart.commonobjects.iDartProperties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class MessageUtil {

	public static void showError(Throwable e, final String title, String message) {
		if (e instanceof InvocationTargetException) {
			e = ((InvocationTargetException) e).getTargetException();
		}
		IStatus status = null;
		if (message == null) {
			message = e.getMessage();
		}
		if (message == null) {
			message = e.toString();
		}
		status = new Status(IStatus.ERROR, "iDART", IStatus.OK, message, e);
		new iDARTErrorDialog(null, title, null, status, IStatus.ERROR).open();
	}

	public static void showError(Throwable e) {
		showError(e, null, null);
	}

	public static String getCrashMessage() {
		String message = "An error has occurred in iDART that requires it to restart.\n\n "
			+ "If this same error happens regularly in iDART, please contact the iDART Help Desk. To see the technical reasons for this crash, "
			+ "please click on the 'Details' button.\n\n"
			+ "Version infromation:"
			+ "\niDART version: "
				+ iDartProperties.idartVersionNumber;
		return message;
	}

}
