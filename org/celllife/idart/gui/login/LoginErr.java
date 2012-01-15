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

// PHARMACY //
package org.celllife.idart.gui.login;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.platform.GenericGuiInterface;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.SessionFactory;

/**
 */
public class LoginErr implements GenericGuiInterface {
	private final Logger log;

	private final Shell loginShell;

	private Composite compLoginInfo;

	private String errorMessageText;

	private Button btnOk;

	SessionFactory sessionFactory;

	private Display display;

	/**
	 * Constructor for LoginErr.
	 * @param errorMessageText String
	 */
	public LoginErr(String errorMessageText) {

		super();
		log = Logger.getLogger(this.getClass());

		display = Display.getCurrent();
		if (display == null) {
			display = new Display();
		}

		this.errorMessageText = errorMessageText;
		loginShell = new Shell();
		loginShell.setText(Messages.getString("loginerr.screen.title")); //$NON-NLS-1$
		loginShell.setBounds(new Rectangle(200, 200, 400, 300));
		loginShell.setImage(ResourceUtils
				.getImage(iDartImage.LOGO_GRAPHIC));

		Label lblPicLogo = new Label(loginShell, SWT.NONE);
		lblPicLogo.setBounds(new Rectangle(129, 190, 142, 67));
		lblPicLogo.setImage(ResourceUtils.getImage(iDartImage.FINAL_LOGO));

		/* Create The Composite Login Information */
		createCompLoginInfo();
		/* Create the Composite Button Information */
		createCompButtons();

		log.debug("Login Form Created"); //$NON-NLS-1$

		/* Add the Usernames from the Database */

		LayoutUtils.centerGUI(loginShell);
		this.loginShell.open();

		while (!loginShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * This method initializes compLoginInfo
	 *
	 */
	private void createCompLoginInfo() {

		// compLoginInfo
		compLoginInfo = new Composite(loginShell, SWT.NONE);
		compLoginInfo.setBounds(new Rectangle(20, 10, 360, 140));

		Label lblMessage = new Label(compLoginInfo, SWT.WRAP);
		lblMessage.setBounds(new Rectangle(0, 5, 360, 35));
		lblMessage.setText(errorMessageText);
		lblMessage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8_BOLD));

		Label lblStdMessage = new Label(compLoginInfo, SWT.WRAP);
		lblStdMessage.setBounds(new Rectangle(0, 60, 360, 35));
		lblStdMessage
		.setText(Messages.getString("loginerr.label.error")); //$NON-NLS-1$
		lblStdMessage.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		Label lblVersionNumbers = new Label(compLoginInfo, SWT.CENTER);
		lblVersionNumbers.setBounds(new Rectangle(0, 105, 360, 15));
		String message = Messages.getString("loginerr.label.version"); //$NON-NLS-1$
		lblVersionNumbers.setText(MessageFormat.format(message,
				iDartProperties.idartVersionNumber));
		lblVersionNumbers.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/**
	 * This method initializes compButtons
	 *
	 */
	private void createCompButtons() {

		btnOk = new Button(loginShell, SWT.NONE);
		btnOk.setBounds(new Rectangle(165, 150, 70, 30));
		btnOk.setText(Messages.getString("common.ok")); //$NON-NLS-1$
		btnOk.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				loginShell.dispose();
				display.dispose();

			}
		});

	}

	/**
	 * Method getErrorMessageText.
	 * @return String
	 */
	public String getErrorMessageText() {
		return errorMessageText;
	}

	/**
	 * Method setErrorMessageText.
	 * @param errorMessageText String
	 */
	public void setErrorMessageText(String errorMessageText) {
		this.errorMessageText = errorMessageText;
	}

}
