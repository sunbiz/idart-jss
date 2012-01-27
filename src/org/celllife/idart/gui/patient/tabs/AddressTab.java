package org.celllife.idart.gui.patient.tabs;

import java.util.HashMap;
import java.util.Map;

import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.misc.GenericTab;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.Session;

/**
 */
public class AddressTab extends GenericTab implements IPatientTab {

	private CCombo cmbProvince;
	private Session hSession;
	private TabFolder parent;
	private int style;

	private Text txtAddress1;

	private Text txtAddress2;

	private Text txtAddress3;

	private Text txtPhoneHome;
	private Text txtPhoneWork;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#changesMade(org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public boolean changesMade(Patient patient) {
		boolean noChangesMade = true;
		noChangesMade &= patient.getAddress1().trim().equals(
				txtAddress1.getText().trim());
		noChangesMade &= patient.getAddress2().trim().equals(
				txtAddress2.getText().trim());
		noChangesMade &= patient.getAddress3().trim().equals(
				txtAddress3.getText().trim());
		noChangesMade &= patient.getProvince().trim().equals(
				cmbProvince.getText().trim());
		noChangesMade &= patient.getHomePhone().trim().equals(
				txtPhoneHome.getText().trim());
		noChangesMade &= patient.getWorkPhone().trim().equals(
				txtPhoneWork.getText().trim());
		return !noChangesMade;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#clear()
	 */
	@Override
	public void clear() {
		txtAddress1.setText(EMPTY);
		txtAddress2.setText(EMPTY);
		txtAddress3.setText(EMPTY);
		cmbProvince.select(cmbProvince.getItemCount());
		txtPhoneHome.setText(EMPTY);
		txtPhoneWork.setText(EMPTY);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#create()
	 */
	@Override
	public void create() {
		setTabItem(new TabItem(parent, style));
		getTabItem().setText(Messages.getString("AddressTab.tab.name")); //$NON-NLS-1$
		createAddressesGroup();
	}
	/**
	 * This method initializes grpAddresses
	 */
	private void createAddressesGroup() {

		Group grpAddresses = new Group(getTabItem().getParent(), SWT.NONE);
		// grpAddresses.setText("Patient Address");
		grpAddresses.setBounds(new Rectangle(3, 3, 750, 140));
		grpAddresses.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		// First tab in the tabbed group
		getTabItem().setControl(grpAddresses);

		// Location
		Label lblAddress1 = new Label(grpAddresses, SWT.NONE);
		lblAddress1.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 20,
				90, 20));
		lblAddress1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblAddress1.setText(Messages.getString("AddressTab.address1.label")); //$NON-NLS-1$
		txtAddress1 = new Text(grpAddresses, SWT.BORDER);
		txtAddress1.setBounds(new org.eclipse.swt.graphics.Rectangle(140, 20,
				220, 20));
		txtAddress1.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Street
		Label lblAddress2 = new Label(grpAddresses, SWT.NONE);
		lblAddress2.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 50,
				90, 20));
		lblAddress2.setText(Messages.getString("AddressTab.address2.label")); //$NON-NLS-1$
		lblAddress2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtAddress2 = new Text(grpAddresses, SWT.BORDER);
		txtAddress2.setBounds(new org.eclipse.swt.graphics.Rectangle(140, 50,
				220, 20));
		txtAddress2.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Suburb
		Label lblAddress3 = new Label(grpAddresses, SWT.NONE);
		lblAddress3.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 80,
				90, 20));
		lblAddress3.setText(Messages.getString("AddressTab.address3.label")); //$NON-NLS-1$
		lblAddress3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtAddress3 = new Text(grpAddresses, SWT.BORDER);
		txtAddress3.setBounds(new org.eclipse.swt.graphics.Rectangle(140, 80,
				220, 20));
		txtAddress3.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Province
		Label lblProvince = new Label(grpAddresses, SWT.NONE);
		lblProvince.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 110,
				90, 20));
		lblProvince.setText(Messages.getString("AddressTab.province.label")); //$NON-NLS-1$
		lblProvince.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		cmbProvince = new CCombo(grpAddresses, SWT.BORDER);
		cmbProvince.setBounds(new org.eclipse.swt.graphics.Rectangle(140, 110,
				150, 20));
		cmbProvince.setBackground(ResourceUtils.getColor(iDartColor.WHITE));
		cmbProvince.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		CommonObjects.populateProvinces(hSession, cmbProvince);
		cmbProvince.setVisibleItemCount(cmbProvince.getItemCount());
		cmbProvince.setEditable(false);
		cmbProvince.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String theText = cmbProvince.getText();
				if (theText.length() > 2) {
					String s = theText.substring(0, 1);
					String t = theText.substring(1, theText.length());
					theText = s.toUpperCase() + t;
					String[] items = cmbProvince.getItems();
					for (int i = 0; i < items.length; i++) {
						if (items[i].length() > 3
								&& items[i].substring(0, 3).equalsIgnoreCase(
										theText)) {
							cmbProvince.setText(items[i]);
							cmbProvince.setFocus();
						}
					}
				}
			}
		});


		// Phone Home
		Label lblPhoneHome = new Label(grpAddresses, SWT.NONE);
		lblPhoneHome.setBounds(new org.eclipse.swt.graphics.Rectangle(400, 20,
				105, 20));
		lblPhoneHome.setText(Messages.getString("AddressTab.phone.home.label")); //$NON-NLS-1$
		lblPhoneHome.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPhoneHome = new Text(grpAddresses, SWT.BORDER);
		txtPhoneHome.setBounds(new org.eclipse.swt.graphics.Rectangle(505, 20,
				220, 20));
		txtPhoneHome.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		// Phone Work
		Label lblPhoneWork = new Label(grpAddresses, SWT.NONE);
		lblPhoneWork.setBounds(new org.eclipse.swt.graphics.Rectangle(400, 50,
				105, 20));
		lblPhoneWork.setText(Messages.getString("AddressTab.phone.work.label")); //$NON-NLS-1$
		lblPhoneWork.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtPhoneWork = new Text(grpAddresses, SWT.BORDER);
		txtPhoneWork.setBounds(new org.eclipse.swt.graphics.Rectangle(505, 50,
				220, 20));
		txtPhoneWork.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#enable(boolean,
	 *      org.eclipse.swt.graphics.Color)
	 */
	@Override
	public void enable(boolean enable, Color color) {
		txtAddress1.setEnabled(enable);
		txtAddress2.setEnabled(enable);
		txtAddress3.setEnabled(enable);
		cmbProvince.setEnabled(enable);
		txtPhoneHome.setEnabled(enable);
		txtPhoneWork.setEnabled(enable);

		cmbProvince.setBackground(color);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#populate(org.hibernate.Session,
	 *      org.celllife.idart.database.hibernate.Patient)
	 */
	/**
	 * Method loadPatientDetails.
	 * @param sess Session
	 * @param patient Patient
	 * @param isPatientActive boolean
	 * @see org.celllife.idart.gui.patient.tabs.IPatientTab#loadPatientDetails(Session, Patient, boolean)
	 */
	@Override
	public void loadPatientDetails(Patient patient, boolean isPatientActive) {
		CommonObjects.populateProvinces(hSession, cmbProvince);

		txtPhoneHome.setText(patient.getHomePhone());
		txtAddress1.setText(patient.getAddress1());
		txtAddress2.setText(patient.getAddress2());
		txtAddress3.setText(patient.getAddress3());
		txtPhoneWork.setText(patient.getWorkPhone());
		cmbProvince.setText(patient.getProvince());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#setParent(org.eclipse.swt.widgets.TabFolder)
	 */
	@Override
	public void setParent(TabFolder parent) {
		this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#setPatientDetails(org.celllife.idart.database.hibernate.Patient)
	 */
	@Override
	public void setPatientDetails(Patient patient) {
		patient.setAddress1(txtAddress1.getText());
		patient.setAddress2(txtAddress2.getText());
		patient.setAddress3(txtAddress3.getText());
		patient.setHomePhone(txtPhoneHome.getText());
		patient.setProvince(cmbProvince.getText());
		patient.setWorkPhone(txtPhoneWork.getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#setSession(org.hibernate.Session)
	 */
	@Override
	public void setSession(Session session) {
		this.hSession = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.misc.IGenericTab#setStyle(int)
	 */
	@Override
	public void setStyle(int SWTStyle) {
		this.style = SWTStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#submit(org.hibernate.Session,
	 *      org.celllife.idart.database.hibernate.Patient)
	 */
	/**
	 * Method submit.
	 * @param patient Patient
	 * @see org.celllife.idart.gui.patient.tabs.IPatientTab#submit(Patient)
	 */
	@Override
	public void submit(Patient patient) {
		// nothing to submit
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.celllife.idart.gui.patient.util.IPatientTab#validateFields()
	 */
	/**
	 * Method validateFields.
	 * @param patient Patient
	 * @return Map<String,String>
	 * @see org.celllife.idart.gui.patient.tabs.IPatientTab#validateFields(Patient)
	 */
	@Override
	public Map<String, String> validateFields(Patient patient) {
		String title = EMPTY;
		String message = EMPTY;
		boolean result = true;
		
		// Check if user selected a province
		if(!"select a province".equalsIgnoreCase(cmbProvince.getText())) { //$NON-NLS-1$
			title = Messages.getString("AddressTab.error.invalid-province.title"); //$NON-NLS-1$
			message = Messages.getString("AddressTab.error.invalid-province.msg"); //$NON-NLS-1$
			result = false;
		}
		// Check if user entered a valid province
		for (int i = 0; i < cmbProvince.getItemCount(); i++) {
			if (cmbProvince.getItem(i).equalsIgnoreCase(cmbProvince.getText())) {
				title = EMPTY;
				message = EMPTY;
				result = true;
			}
		}


		Map<String, String> map = new HashMap<String, String>();
		map.put("result", String.valueOf(result)); //$NON-NLS-1$
		map.put("title", title); //$NON-NLS-1$
		map.put("message", message); //$NON-NLS-1$
		return map;
	}
}
