package org.celllife.idart.gui.user;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.gui.login.Login;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

public class ConfirmWithPasswordDialogAdapter {

	private ConfirmWithPasswordDialog passwordDialog;

	public ConfirmWithPasswordDialogAdapter(Shell parent, Session sess) {
		passwordDialog = new ConfirmWithPasswordDialog(
				parent, sess);
	}

	public ConfirmWithPasswordDialogAdapter(Shell parent, String message,
			Session sess) {
		passwordDialog = new ConfirmWithPasswordDialog(
				parent, message, sess);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent shell
	 * @param style
	 */
	public ConfirmWithPasswordDialogAdapter(Shell parent, String message,
			String warning, Session sess) {
		passwordDialog = new ConfirmWithPasswordDialog(
				parent, message, warning, sess);
	}
	
	public void setText(String text){
		passwordDialog.setText(text);
	}
	
	public void setMessage(String message) {
		passwordDialog.setMessage(message);
	}

	public String open(){
		String messg = passwordDialog.open();
		if (messg.equals("verified")) {
			return messg;
		} else if (messg.equals("switchUser")) {
			Login login = new Login(LocalObjects.currentClinic);
			if (login.isSuccessfulLogin()) {
				return "verified";
			}
		}
		return "failed";
	}
}
