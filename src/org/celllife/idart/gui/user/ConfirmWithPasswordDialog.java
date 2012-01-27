/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.celllife.idart.gui.user;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

/**
 * Dialog to prompt current user for password
 * 
 * Created on 22/11/2006
 * 
 */
public class ConfirmWithPasswordDialog extends Dialog {

	private String userName;

	private String message;

	private String warning;

	private String result;

	private int noOfTimestoAllowUserToEnterPassword;

	private int passwordAttmpts;

	private Composite compEverything = null;

	private Session sess = null;

	/**
	 * Contructor
	 * 
	 * @param parent
	 *            the parent shell
	 * @param sess
	 *            Session
	 */
	public ConfirmWithPasswordDialog(Shell parent, Session sess) {
		this(
				parent,
				"Please enter your password",
				"WARNING: You should only perform this action if you are sure you want to remove information from the database PERMANENTLY. The user who performed this action, as well as the current time, will be recorded in the Transaction Log.",
				sess);
	}

	/**
	 * Constructor for ConfirmWithPasswordDialog.
	 * 
	 * @param parent
	 *            Shell
	 * @param message
	 *            String
	 * @param sess
	 *            Session
	 */
	public ConfirmWithPasswordDialog(Shell parent, String message, Session sess) {
		this(
				parent,
				message,
				"WARNING: You should only perform this action if you are sure you want to remove information from the database PERMANENTLY. The user who performed this action, as well as the current time, will be recorded in the Transaction Log.",
				sess);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent shell
	 * @param message
	 *            String
	 * @param warning
	 *            String
	 * @param sess
	 *            Session
	 */
	public ConfirmWithPasswordDialog(Shell parent, String message,
			String warning, Session sess) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("Password Required");
		setMessage(message);
		setWarning(warning);
		this.sess = sess;
		result = "Unverified";
		passwordAttmpts = 0;
		// Default Number of times to allow user to enter password
		noOfTimestoAllowUserToEnterPassword = 3;
	}

	/**
	 * 
	 * @return String
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Method getMessage.
	 * 
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Method setMessage.
	 * 
	 * @param message
	 *            String
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Method getNoOfTimestoAllowUserToEnterPassword.
	 * 
	 * @return int
	 */
	public int getNoOfTimestoAllowUserToEnterPassword() {
		return noOfTimestoAllowUserToEnterPassword;
	}

	/**
	 * Method setNoOfTimestoAllowUserToEnterPassword.
	 * 
	 * @param noOfTimestoAllowUserToEnterPassword
	 *            int
	 */
	public void setNoOfTimestoAllowUserToEnterPassword(
			int noOfTimestoAllowUserToEnterPassword) {
		this.noOfTimestoAllowUserToEnterPassword = noOfTimestoAllowUserToEnterPassword;
	}

	/**
	 * Method open.
	 * 
	 * @return String
	 */
	public String open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.setBounds(0, 0, 400, 240);
		LayoutUtils.centerGUI(shell);
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		// Return the entered password, or null
		return result;

	}

	/**
	 * Method createContents.
	 * 
	 * @param shell
	 *            Shell
	 */
	private void createContents(final Shell shell) {

		compEverything = new Composite(shell, SWT.NONE);
		compEverything.setBounds(new Rectangle(20, 20, 360, 200));

		final Label lblMessage = new Label(compEverything, SWT.WRAP
				| SWT.CENTER);
		lblMessage.setBounds(new Rectangle(5, 5, 350, 60));
		lblMessage.setText(warning);
		lblMessage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblMessage.setForeground(ResourceUtils.getColor(iDartColor.RED));

		final Label lblUserName = new Label(compEverything, SWT.NONE);
		lblUserName.setBounds(new Rectangle(5, 80, 80, 30));
		lblUserName.setText("Username:");
		lblUserName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		final Text txtUserName = new Text(compEverything, SWT.BORDER);

		txtUserName.setText(LocalObjects.getUser(sess).getUsername());
		txtUserName.setEnabled(false);
		txtUserName.setEditable(false);
		txtUserName.setBounds(new Rectangle(120, 80, 200, 20));
		txtUserName.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblPassword = new Label(compEverything, SWT.NONE);
		lblPassword.setText("Password:");
		lblPassword.setBounds(new Rectangle(5, 110, 80, 30));
		lblPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		final Text txtPassword = new Text(compEverything, SWT.PASSWORD
				| SWT.BORDER);
		txtPassword.setBounds(new Rectangle(120, 110, 200, 20));
		txtPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Create OK button
		Button btnOk = new Button(compEverything, SWT.PUSH);
		btnOk.setText("OK");
		btnOk.setBounds(new Rectangle(40, 140, 80, 30));
		btnOk.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (!verifyPassword(txtPassword.getText())) {
					if (passwordAttmpts == noOfTimestoAllowUserToEnterPassword) {
						shell.close();
					} else {

						txtPassword.setText("");
						txtPassword.setFocus();

						MessageBox incorrectPassword = new MessageBox(shell,
								SWT.OK | SWT.ICON_ERROR);
						incorrectPassword.setText("Incorrect password");
						incorrectPassword.setMessage(message);
						incorrectPassword.open();
					}
				}
				// If the password has been entered correctly or if the user
				// entered the incorrect password an x amount of times,
				// we return to the previous screen
				if (result.equalsIgnoreCase("verified")) {
					shell.close();
				}
			}
		});

		// Create cancel button
		Button btnCancel = new Button(compEverything, SWT.PUSH);
		btnCancel.setText("Cancel");
		btnCancel.setBounds(new Rectangle(140, 140, 80, 30));
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				result = "cancel";
				shell.close();
			}
		});

		Button btnChangeUser = new Button(compEverything, SWT.PUSH);
		btnChangeUser.setText("Change User");
		btnChangeUser.setBounds(new Rectangle(240, 140, 80, 30));
		btnChangeUser.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		btnChangeUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				result = "switchUser";
				shell.close();
			}
		});

		// Set the OK button to default so the user can press enter after typing
		shell.setDefaultButton(btnOk);

	}

	/**
	 * Method verifyPassword.
	 * 
	 * @param password
	 *            String
	 * @return boolean
	 */
	private boolean verifyPassword(String password) {

		if (noOfTimestoAllowUserToEnterPassword > 0) {
			passwordAttmpts++;
		}
		if (LocalObjects.getUser(sess).getPassword().equals(password)) {
			result = "verified";
			return true;
		} else {
			if ((noOfTimestoAllowUserToEnterPassword - passwordAttmpts) > 1) {
				setMessage("Incorrect Password! Please enter the \ncorrect password.");
			} else if ((noOfTimestoAllowUserToEnterPassword - passwordAttmpts) == 1) {
				setMessage("Incorrect Password! Please enter the \ncorrect password. This is your final attempt");
			}

			return false;
		}
	}

	/**
	 * Method getWarning.
	 * 
	 * @return String
	 */
	public String getWarning() {
		return warning;
	}

	/**
	 * Method setWarning.
	 * 
	 * @param warning
	 *            String
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

}
