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

package org.celllife.idart.gui.welcome;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.GUIException;
import org.celllife.idart.gui.platform.GenericGuiInterface;
import org.celllife.idart.gui.utils.LayoutUtils;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.LoginTimer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class GenericWelcome implements GenericGuiInterface {

	public static Shell shell;

	public static LoginTimer timer;

	public static Display display;

	private final Logger log;
	
	protected Composite grpTitle;

	JFrame frame = new JFrame();

	private boolean timedOut = false;
	Button btnLogOff;
	Label lblPicLogoff;

	protected Composite compHelpAndLogoff;

	public GenericWelcome() {
		super();
		log = Logger.getLogger(GenericWelcome.class);
		if (iDartProperties.logoutTime != -1) {
			timer = new LoginTimer(iDartProperties.logoutTime);
		} else {
			timer = new LoginTimer(600);
		}
		display = Display.getCurrent();
		if (display == null)
			throw new GUIException("Display is null."); //$NON-NLS-1$

		createnewWelcome();
		showGUI();

	}

	public void showGUI() {

		showWelcomeScreen();

		timer.start();

		display.addFilter(SWT.MouseMove, new SimpleListener("mouse moved")); //$NON-NLS-1$
		display.addFilter(SWT.KeyDown, new SimpleListener("key pressed")); //$NON-NLS-1$

		// Call login screen if user closes screen with x button
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event e) {
				logOff();
			}
		});

		while (!shell.isDisposed()) {

			if (!timer.getThread().isAlive()) {
				timedOut = true;
				break;
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}

		}
		shell.dispose();
		timer.stop();

	}

	private void createWelcomeLabel() {
		grpTitle = new Composite(shell, SWT.NONE);
		grpTitle.setBounds(new Rectangle(160, 40, 590, 40));
		grpTitle.setBackground(ResourceUtils.getColor(iDartColor.GRAY));

		// lblWelcomeBlurb
		Label lblWelcomeBlurb = new Label(grpTitle, SWT.CENTER | SWT.SHADOW_IN);
		lblWelcomeBlurb.setText(getWelcomeLabelText());
		lblWelcomeBlurb.setBackground(ResourceUtils.getColor(iDartColor.GRAY));
		lblWelcomeBlurb.setForeground(ResourceUtils.getColor(iDartColor.BLACK));
		lblWelcomeBlurb.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		lblWelcomeBlurb.setBounds(new Rectangle(3, 8, 580, 25));
	}

	protected abstract String getWelcomeLabelText();

	/**
	 * This method initializes welcome.shell
	 */
	private void createnewWelcome() {
		// only destroy on relogin
		if (shell != null) {
			shell.dispose();
		}
		log.debug("Creating newWelcome"); //$NON-NLS-1$
		shell = new Shell(GenericWelcome.display.getActiveShell());

		String title = Messages.getString("common.screen.title"); //$NON-NLS-1$
		String name = Messages.getString("welcome.screen.name"); //$NON-NLS-1$
		shell.setText(MessageFormat.format(title, name, LocalObjects.getUser(
				HibernateUtil.getNewSession()).getUsername()));
		shell.setBounds(new Rectangle(0, 0, 900, 700));
		Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
		shell.setImage(i);

		createWelcomeLabel();

		// lblInstructions
		Label lblInstructions = new Label(shell, SWT.CENTER);
		lblInstructions.setText(Messages.getString("welcome.screen.instruction")); //$NON-NLS-1$
		lblInstructions.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_BACKGROUND));
		lblInstructions.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));
		lblInstructions.setBounds(new Rectangle(340, 530, 220, 30));

		// lblPicLogo
		Label lblPicLogo = new Label(shell, SWT.NONE);
		lblPicLogo.setText(""); //$NON-NLS-1$
		lblPicLogo.setImage(ResourceUtils.getImage(iDartImage.LOGO_IDART));
		// the logo gif is 200 x 62
		lblPicLogo.setBounds(new Rectangle(340, 560, 220, 82));

		createHelp();
		createCompHelpAndLogoff();

		Composite compOptions = new Composite(shell, SWT.NONE);
		compOptions.setBounds(new Rectangle(145, 220, 610, 240));
		createCompOptions(compOptions);

		Label lblVersionNumbers = new Label(shell, SWT.CENTER);
		lblVersionNumbers.setBounds(new Rectangle(175, 650, 550, 30));
		String message = Messages.getString("common.label.version");
		lblVersionNumbers.setText(MessageFormat.format(message,
				iDartProperties.idartVersionNumber)); //$NON-NLS-1$
		lblVersionNumbers.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
	}

	protected abstract void createCompOptions(Composite compOptions);

	/**
	 * This method initializes compHelpAndLogoff
	 * 
	 */
	private void createCompHelpAndLogoff() {
		
		
		compHelpAndLogoff = new Composite(shell, SWT.NONE);
		compHelpAndLogoff.setBounds(new Rectangle(145, 350, 610, 115));

		Label lblHelp = new Label(compHelpAndLogoff, SWT.NONE);
		lblHelp.setBounds(new Rectangle(200, 0, 50, 43));
		lblHelp.setImage(ResourceUtils.getImage(iDartImage.HELP));
		lblHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				showHelp();
			}
		});
		lblHelp.setVisible(false);

		Button btnHelp = new Button(compHelpAndLogoff, SWT.NONE);
		btnHelp.setBounds(new Rectangle(160, 50, 130, 40));
		btnHelp.setText(Messages.getString("welcome.button.help.text")); //$NON-NLS-1$
		btnHelp
		.setToolTipText(Messages.getString("welcome.button.help.tooltip")); //$NON-NLS-1$
		btnHelp.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showHelp();
			}
		});
		btnHelp.setVisible(false);
		initLogoffBtnLabel(null, null, null);
	}

	private void initLogoffBtnLabel(Composite composite, Rectangle lblRectangle, Rectangle btnRectangle) {
	
		
		// log off
		if(composite == null){
			lblPicLogoff = new Label(compHelpAndLogoff, SWT.NONE);
			btnLogOff = new Button(compHelpAndLogoff, SWT.NONE);
		}else{
			lblPicLogoff = new Label(composite, SWT.NONE);
			btnLogOff = new Button(composite, SWT.NONE);
		}
		if(lblRectangle == null){
			lblPicLogoff.setBounds(new Rectangle(280, 0, 50, 43));
		}else{
			lblPicLogoff.setBounds(lblRectangle);
		}
		lblPicLogoff.setImage(ResourceUtils.getImage(iDartImage.PATIENTUPDATE));
		lblPicLogoff.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				logOff();
			}
		});

		btnLogOff.setData(iDartProperties.SWTBOT_KEY, "logoff"); //$NON-NLS-1$
		btnLogOff.setText(Messages.getString("welcome.button.logoff.text")); //$NON-NLS-1$
		
		if(btnRectangle == null){
			btnLogOff.setBounds(new Rectangle(240, 50, 130, 40));
		}else{
			btnLogOff.setBounds(btnRectangle);
		}
		btnLogOff
		.setToolTipText(Messages.getString("welcome.button.logoff.tooltip")); //$NON-NLS-1$
		btnLogOff.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnLogOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logOff();
			}
		});
	}

	/*
	 * This method creates the help frame
	 */

	private void createHelp() {
		try {
			ClassLoader cl = GenericWelcome.class.getClassLoader();
			URL url = new URL((new File(".")).toURI().toURL(), "doc" //$NON-NLS-1$ //$NON-NLS-2$
					+ File.separator + "jhelpset.hs"); //$NON-NLS-1$
			JHelp helpViewer = null;
			// Create a new JHelp object with a new HelpSet.
			helpViewer = new JHelp(new HelpSet(cl, url));
			// helpViewer.setCurrentID("top");

			frame.setSize(800, 600);
			frame.setTitle(Messages.getString("welcome.help.title")); //$NON-NLS-1$

			frame.getContentPane().add(helpViewer);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} catch (MalformedURLException e) {
			log.error("Unable to load help due to bad URL."); //$NON-NLS-1$
		} catch (HelpSetException e) {
			log.error("Unable to load help."); //$NON-NLS-1$
		}

	}

	/*
	 * This method displays the help
	 */
	private void showHelp() {
		frame.setVisible(true);
	}

	/**
	 * Log out of iDART, and display the login screen
	 * 
	 */
	private void logOff() {

		timedOut = true;
		LocalObjects.setUser(null);
		shell.dispose();

	}

	private void showWelcomeScreen() {
		LayoutUtils.centerGUI(shell);

		shell.open();
	}

	/**
	 */
	class SimpleListener implements Listener {
		String name;

		/**
		 * Constructor for SimpleListener.
		 * 
		 * @param name
		 *            String
		 */
		public SimpleListener(String name) {
			this.name = name;
		}

		/**
		 * Method handleEvent.
		 * 
		 * @param e
		 *            Event
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(Event)
		 */
		@Override
		public void handleEvent(Event e) {
			timer.restart();
		}
	}

	/**
	 * Method isTimedOut.
	 * 
	 * @return boolean
	 */
	public boolean isTimedOut() {
		return timedOut;
	}
	
	public void overrideBtnLogLocation(Composite composite, Rectangle lblRectangle, Rectangle btnRectangle){
		compHelpAndLogoff.dispose();
		initLogoffBtnLabel(composite, lblRectangle, btnRectangle);
	}
}
