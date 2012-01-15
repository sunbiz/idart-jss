/*
] * iDART: The Intelligent Dispensing of Antiretroviral Treatment
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.User;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.GUIException;
import org.celllife.idart.gui.platform.GenericGuiInterface;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 */
public class Login implements GenericGuiInterface {
	private Logger log;

	private Shell loginShell;

	public static Display display;

	private Composite compLoginInfo;

	private Composite compButtons;

	private Text txtPassword;

	private CCombo cmbUsers;

	private CCombo cmbClinics;

	private Button btnLogin;

	private Button btnCancel;

	private boolean successfulLogin;

	private Session hSession;

	private final boolean limitClinic;

	public Login() {
		super();
		limitClinic = false;
		preInitialize();

		populateUsername(null);
		populateClinics(null);

		postInitialize();
	}

	public Login(Clinic clinic) {
		super();
		limitClinic = true;
		preInitialize();
		
		// Reload Clinic into current session to avoid
		// LazyInitialisation error on users.
		clinic = AdministrationManager.getClinic(hSession, clinic.getClinicName());

		populateUsername(clinic);
		populateClinics(clinic);

		postInitialize();
	}

	private void postInitialize() {
		cmbUsers.setFocus();

		if (!loginShell.isDisposed()) {
			LayoutUtils.centerGUI(loginShell);
			loginShell.open();

		}

		while (!loginShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void preInitialize() {
		log = Logger.getLogger(Login.class);
		hSession = HibernateUtil.getNewSession();

		display = Display.getCurrent();
		if (display == null)
			throw new GUIException("Display is null."); //$NON-NLS-1$

		loginShell = new Shell();
		loginShell.setText(Messages.getString("login.screen.title")); //$NON-NLS-1$
		loginShell.setBounds(new Rectangle(130, 170, 420, 300));

		Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
		loginShell.setImage(i);

		loginShell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				closeScreen();
			}
		});

		Label lblPicLogo = new Label(loginShell, SWT.NONE);
		lblPicLogo.setBounds(new Rectangle(130, 180, 142, 67));
		lblPicLogo.setImage(ResourceUtils.getImage(iDartImage.FINAL_LOGO));

		Label lblVersionNumbers = new Label(loginShell, SWT.CENTER);
		lblVersionNumbers.setBounds(new Rectangle(0, 247, 420, 30));
		String message = Messages.getString("common.label.version");
		lblVersionNumbers.setText(MessageFormat.format(message,
				iDartProperties.idartVersionNumber)); //$NON-NLS-1$
		lblVersionNumbers.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		createCompLoginInfo();
		createCompButtons();

		log.debug("Login Form Created"); //$NON-NLS-1$
	}

	/**
	 * This method initializes compLoginInfo
	 * 
	 */
	private void createCompLoginInfo() {

		int kernel = 5;

		// compLoginInfo
		compLoginInfo = new Composite(loginShell, SWT.NONE);
		compLoginInfo.setBounds(new Rectangle(20, 10 + kernel, 395, 100));

		// lblUsername & cmbUsers
		Label lblUsername = new Label(compLoginInfo, SWT.NONE);
		lblUsername.setBounds(new Rectangle(10, 10 + kernel, 75, 20));
		lblUsername.setText(Messages.getString("login.label.username")); //$NON-NLS-1$
		lblUsername.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbUsers = new CCombo(compLoginInfo, SWT.BORDER);
		cmbUsers.setData(iDartProperties.SWTBOT_KEY, "login.username"); //$NON-NLS-1$
		cmbUsers.setBounds(new Rectangle(125, 10 + kernel, 240, 20));
		cmbUsers.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbUsers.setEditable(false);
		cmbUsers.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdLoginSelected();
				}
				
				String s = String.valueOf(e.character);

				String[] items = cmbUsers.getItems();

				for (int i = 0; i < items.length; i++) {
					if (items[i].substring(0, 1).equalsIgnoreCase(s)) {
						cmbUsers.setText(items[i]);
					}
				}
			}

		});
		cmbUsers.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		if (!limitClinic) {
			cmbUsers.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					populateClinics(null);
				}
			});
		}

		// lblPassword & txtPassword
		Label lblPassword = new Label(compLoginInfo, SWT.NONE);
		lblPassword.setBounds(new Rectangle(10, 40 + kernel, 75, 20));
		lblPassword.setText(Messages.getString("login.label.password")); //$NON-NLS-1$
		lblPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPassword = new Text(compLoginInfo, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setData(iDartProperties.SWTBOT_KEY, "login.password"); //$NON-NLS-1$
		txtPassword.setBounds(new Rectangle(125, 40 + kernel, 240, 20));
		txtPassword.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					cmdLoginSelected();
				}
			}
		});

		// lblClinic & cmbClinics
		Label lblClinic = new Label(compLoginInfo, SWT.NONE);
		lblClinic.setBounds(new Rectangle(10, 70 + kernel, 75, 20));
		lblClinic.setText(Messages.getString("login.label.clinic")); //$NON-NLS-1$
		lblClinic.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinics = new CCombo(compLoginInfo, SWT.BORDER);
		cmbClinics.setData(iDartProperties.SWTBOT_KEY, "login.clinic");
		cmbClinics.setBounds(new Rectangle(125, 70 + kernel, 240, 20));
		cmbClinics.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		cmbClinics.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbClinics.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String s = String.valueOf(e.character);

				if (e.character == SWT.CR) {
					cmdLoginSelected();
				} else {
					String[] items = cmbClinics.getItems();

					for (int i = 0; i < items.length; i++) {
						if (items[i].substring(0, 1).equalsIgnoreCase(s)) {
							cmbClinics.setText(items[i]);
						}
					}

				}
			}
		});
		cmbClinics.setEditable(false);
		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.OFFLINE_DOWNREFERRAL_MODE)) {
			cmbClinics.setEnabled(false);
		}
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	private void createCompButtons() {
		compButtons = new Composite(loginShell, SWT.NONE);
		compButtons.setBounds(new Rectangle(100, 140, 215, 35));

		btnLogin = new Button(compButtons, SWT.NONE);
		btnLogin.setBounds(new Rectangle(5, 5, 85, 27));
		btnLogin.setData(iDartProperties.SWTBOT_KEY, "btnLogin"); //$NON-NLS-1$
		btnLogin.setText(Messages.getString("login.button.login.text")); //$NON-NLS-1$
		btnLogin.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnLogin.setToolTipText(Messages
				.getString("login.button.login.tooltip")); //$NON-NLS-1$
		btnLogin
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdLoginSelected();
			}
		});

		btnCancel = new Button(compButtons, SWT.NONE);
		btnCancel.setText(Messages.getString("login.button.cancel.text")); //$NON-NLS-1$
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setToolTipText(Messages
				.getString("login.button.cancel.tooltip")); //$NON-NLS-1$
		btnCancel.setBounds(new Rectangle(115, 5, 85, 27));
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelSelected();
			}
		});
	}

	/**
	 * Populates the username combobox with the usernames from the users table
	 */
	private void populateUsername(Clinic clinic) {
		List<String> users = null;
		if (clinic != null) {
			users = new ArrayList<String>();
			for (User u : clinic.getUsers()) {
				if (LocalObjects.getUser(hSession).getId() != u.getId()) {
					users.add(u.getUsername());
				}
			}
		} else {
			try {
				users = AdministrationManager.getUserList(hSession);
			} catch (HibernateException e) {
				log.error("Unable to get user list.", e); //$NON-NLS-1$

				showErrorAndClose();
			}
		}
		if (users != null) {
			for (int i = 0; i < users.size(); i++) {
				String s = users.get(i);
				// load the usernames into the combo box
				cmbUsers.add(s);
			}

			if (cmbUsers.getItemCount() > 0) {
				// Set the username to the first username in the combo box
				cmbUsers.setText(cmbUsers.getItem(0));
			} else {
				cmbUsers.setFocus();
			}
		}
	}

	private void showErrorAndClose() {
		new LoginErr(Messages.getString("login.error.database.connection")); //$NON-NLS-1$

		successfulLogin = false;
		loginShell.dispose();
	}

	/**
	 * Populates the clinics combobox with the clinics from the Clinics table
	 */
	private void populateClinics(Clinic clinic) {
		List<Clinic> clinics = new ArrayList<Clinic>();
		cmbClinics.removeAll();

		if (clinic != null) {
			cmbClinics.add(clinic.getClinicName());
			cmbClinics.select(0);
			cmbClinics.setEditable(false);
			cmbClinics.setEnabled(false);
		} else {
			try {
				User user = AdministrationManager.getUserByName(hSession,
						cmbUsers.getText());

				if (user != null) {
					Set<Clinic> clinicsSet = user.getClinics();

					if (clinicsSet != null) {
						clinics.addAll(clinicsSet);
					}

				} else {
					clinics = AdministrationManager.getClinics(hSession);
				}

				Collections.sort(clinics, new Comparator<Clinic>() {
					@Override
					public int compare(Clinic clinic1, Clinic clinic2) {
						return clinic1.getClinicName().compareTo(
								clinic2.getClinicName());
					}
				});

				int key = 0;
				int index = 0;
				for (Clinic theSite : clinics) {
					String s = theSite.getClinicName();
					cmbClinics.add(s);
					if (theSite.isMainClinic()) {
						key = index;
					}
					index++;
				}

				cmbClinics.setText(cmbClinics.getItem(key));
			}

			catch (HibernateException e) {
				log.error("Unable to get clinic list.", e); //$NON-NLS-1$
				showErrorAndClose();
			} finally {
				clinics = null;
			}
		}
	}

	/**
	 * @return Returns the successfulLogin.
	 */
	public boolean isSuccessfulLogin() {
		return successfulLogin;
	}

	private void cmdCancelSelected() {
		// If the user presses the cancel button, then exit the program
		successfulLogin = false;
		closeScreen();

	}

	private void closeScreen() {
		if ((hSession != null) && (hSession.isOpen())) {
			hSession.close();
		}
		loginShell.dispose();
	}

	private void cmdLoginSelected() {
		// Check the password against the username
		checkLogin();
	}

	private void checkLogin() {

		try {

			Clinic theClinic = AdministrationManager.getClinicbyName(hSession,
					cmbClinics.getText());
			User theUser = AdministrationManager.getUserByName(hSession,
					cmbUsers.getText());

			if (theUser == null) {
				successfulLogin = false;
				MessageDialog.openError(loginShell, Messages
						.getString("login.dialog.error.title"), //$NON-NLS-1$
						Messages.getString("login.error.username") //$NON-NLS-1$
						+ ""); //$NON-NLS-1$
				txtPassword.setFocus();
				txtPassword.setText(""); //$NON-NLS-1$
			} else if (theClinic == null) {
				successfulLogin = false;
				MessageDialog.openError(loginShell, Messages
						.getString("login.dialog.error.title"), //$NON-NLS-1$
						Messages.getString("login.error.clinic") //$NON-NLS-1$
						+ ""); //$NON-NLS-1$
				txtPassword.setFocus();
				txtPassword.setText(""); //$NON-NLS-1$
			} else if (!theClinic.getUsers().contains(theUser)) {
				successfulLogin = false;
				MessageDialog.openError(loginShell, Messages
						.getString("login.dialog.error.title"), //$NON-NLS-1$
						Messages.getString("login.error.userpermission")); //$NON-NLS-1$
				txtPassword.setFocus();
				txtPassword.setText(""); //$NON-NLS-1$
			} else if (!txtPassword.getText().equals((theUser.getPassword()))) {
				// If the login was unsuccessful, then alert the user
				successfulLogin = false;
				MessageDialog.openError(loginShell, Messages
						.getString("login.dialog.error.title"), //$NON-NLS-1$
						Messages.getString("login.error.password")); //$NON-NLS-1$
				txtPassword.setFocus();
				txtPassword.setText(""); //$NON-NLS-1$
			} else {

				successfulLogin = true;

				LocalObjects.setUser(theUser);
				LocalObjects.currentClinic = theClinic.getClinicName()
				.equalsIgnoreCase(
						LocalObjects.mainClinic.getClinicName()) ? LocalObjects.mainClinic
								: theClinic;
				log.info("Login successful for user " + theUser.getUsername()); //$NON-NLS-1$
				closeScreen();

			}

		} catch (HibernateException e) {
			showErrorAndClose();
		}
	}
}
