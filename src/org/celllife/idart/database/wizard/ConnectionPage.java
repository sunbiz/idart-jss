package org.celllife.idart.database.wizard;

import java.util.Map;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.DatabaseTools;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ConnectionPage extends WizardPage implements PropertiesPage {
	private static Logger log = Logger.getLogger(ConnectionPage.class);

	private Text txtHost;
	private Composite container;
	private Text txtUser;
	private Text txtPasswd;
	private final Map<String, String> connSettings;
	private Text txtName;

	public ConnectionPage() {
		super("Connection Settings");
		setTitle("Database Connection Settings");
		setDescription("iDART can not connect to the database. "
				+ "Please check the connection settings below.");
		setPageComplete(false);
		connSettings = DatabaseTools._().decomposeConnectionURL();
	}

	@Override
	public void createControl(Composite parent) {
		final Display display = Display.getCurrent();
		final Runnable runner = new Runnable() {
			@Override
			public void run() {
				setErrorMessage(null);
				BusyIndicator.showWhile(display, new Runnable() {
					@Override
					public void run() {
						if (updateProperties()) {
							setPageComplete(true);
						}
					}
				});
			}
		};

		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		Label lblHost = new Label(container, SWT.NULL);
		lblHost.setText("Database server address:");
		txtHost = new Text(container, SWT.BORDER | SWT.SINGLE);
		txtHost.setText(connSettings.get(DatabaseTools.DBHOST));
		txtHost.setLayoutData(gd);
		txtHost.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!txtPasswd.getText().isEmpty()) {
					display.timerExec(1000, runner);
				}
			}
		});

		Label lblName = new Label(container, SWT.NULL);
		lblName.setText("Database name:");
		txtName = new Text(container, SWT.BORDER | SWT.SINGLE);
		txtName.setText(connSettings.get(DatabaseTools.DBNAME));
		txtName.setLayoutData(gd);
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!txtPasswd.getText().isEmpty()) {
					display.timerExec(1000, runner);
				}
			}
		});

		Label lblUser = new Label(container, SWT.NULL);
		lblUser.setText("Database username:");
		txtUser = new Text(container, SWT.BORDER | SWT.SINGLE);
		txtUser.setText(iDartProperties.hibernateUsername);
		txtUser.setLayoutData(gd);
		txtUser.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!txtPasswd.getText().isEmpty()) {
					display.timerExec(1000, runner);
				}
			}
		});

		Label lblPasswd = new Label(container, SWT.NULL);
		lblPasswd.setText("Database password:");
		txtPasswd = new Text(container, SWT.BORDER | SWT.SINGLE);
		txtPasswd.setText("");
		txtPasswd.setLayoutData(gd);
		txtPasswd.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!txtPasswd.getText().isEmpty()) {
					display.timerExec(1000, runner);
				}
			}
		});

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
		setErrorMessage("Password must not be empty.");
	}

	@Override
	public boolean updateProperties() {
		log.debug("Updating properties");
		iDartProperties.hibernatePassword = txtPasswd.getText();
		iDartProperties.hibernateUsername = txtUser.getText();
		connSettings.put(DatabaseTools.DBHOST, txtHost.getText());
		connSettings.put(DatabaseTools.DBNAME, txtName.getText());
		iDartProperties.hibernateConnectionUrl = DatabaseTools._().composeUrl(
				connSettings);

		try {
			DatabaseTools._().refresh();
			JDBCUtil.rebuild();
			JDBCUtil.currentSession();
			JDBCUtil.closeJDBCConnection();
			return true;
		} catch (Throwable e) {
			setPageComplete(false);
			setErrorMessage(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean isRequired() {
		HibernateUtil.rebuildUtil();
		return !DatabaseTools._().checkConnection();
	}
}
