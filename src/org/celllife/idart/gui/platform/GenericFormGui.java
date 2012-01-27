package org.celllife.idart.gui.platform;

import java.text.MessageFormat;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

/**
 */
public abstract class GenericFormGui extends GenericGui implements
GenericFormGuiInterface {

	private Composite compHeader;

	private Composite compButtons;

	protected Label lblIcon;

	protected Label lblHeader;

	protected Button btnSave;
	
	private String btnSaveText = Messages.getString("genericformgui.button.save.text"); ////$NON-NLS-1$

	protected Button btnClear;

	protected Button btnCancel;

	/**
	 * Constructor for GenericFormGui.
	 * @param parent Shell
	 * @param hSession Session
	 */
	public GenericFormGui(Shell parent, Session hSession) {
		super(parent, hSession);
		activate();
	}

	protected abstract void clearForm();

	/**
	 * Submits the data to the database.
	 * 
	 * @return boolean true if data was successfully submitted.
	 */
	protected abstract boolean submitForm();

	/**
	 * Method fieldsOk.
	 * @return boolean
	 */
	protected abstract boolean fieldsOk();

	/**
	 * This method is called during the class initialisation and should be used
	 * to call the {@link GenericFormGui#buildCompHeader(String, String)}
	 * method.
	 *
	 * @see GenericFormGui#buildCompHeader(String, iDartImage)
	 */
	protected abstract void createCompHeader();

	/**
	 * This method is called during the class initialisation to create the form
	 * buttons.
	 *
	 * @see GenericFormGui#buildCompButtons()
	 */
	protected abstract void createCompButtons();

	protected abstract void cmdSaveWidgetSelected();

	protected abstract void cmdClearWidgetSelected();

	protected abstract void cmdCancelWidgetSelected();

	/**
	 * Method enableFields.
	 * @param enable boolean
	 */
	protected abstract void enableFields(boolean enable);

	/**
	 * This method is called during the class initialisation to allow the user
	 * to add their contents to the shell.
	 */
	protected abstract void createContents();

	/**
	 * Method buildShell.
	 * @param shellTxt String
	 * @param bounds Rectangle
	 */
	public void buildShell(String shellTitle, Rectangle bounds) {

		String title = Messages.getString("common.screen.title"); //$NON-NLS-1$
		
		// show class names in title to if env variable set
		if (System.getenv("idart.debug.ui") != null){
			shellTitle += " - " + this.getClass().getName();
		}
		
		getShell().setText(
				MessageFormat.format(title, shellTitle, LocalObjects.getUser(
						getHSession()).getUsername()));
		getShell().setBounds(bounds);
		try {
			Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
			getShell().setImage(i);
		} catch (RuntimeException e) {
			getLog().error("Bad image URL", e); //$NON-NLS-1$
		}
		createCompHeader();
		createCompButtons();
		createContents();
	}

	/**
	 * Method buildCompHeader.
	 * @param headerTxt String
	 * @param icoImage iDartImage
	 */
	protected void buildCompHeader(String headerTxt, iDartImage icoImage) {

		// compHeader
		setCompHeader(new Composite(getShell(), SWT.NONE));
		getCompHeader().setLayout(new FormLayout());

		// lblHeader
		// Since this screen is not rebuilt, we dont want to recreate
		// this component since the screen rebuilds this component all the time.
		if (lblHeader == null) {
			lblHeader = new Label(getCompHeader(), SWT.CENTER | SWT.SHADOW_IN);
			lblHeader.setBackground(ResourceUtils
					.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
			lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		}
		lblHeader.setText(headerTxt);
		lblHeader.redraw();
		lblHeader.update();

		FormData fdata = new FormData();
		fdata.left = new FormAttachment(25, 0);
		fdata.right = new FormAttachment(75, 0);
		fdata.top = new FormAttachment(0, 15);
		lblHeader.setLayoutData(fdata);

		// lblIcon
		lblIcon = new Label(getCompHeader(), SWT.NONE);
		if (icoImage != null) {
			lblIcon.setImage(ResourceUtils.getImage(icoImage));
		}

		fdata = new FormData();
		fdata.right = new FormAttachment(lblHeader, -15, SWT.LEFT);
		fdata.top = new FormAttachment(lblHeader, 0, SWT.CENTER);
		lblIcon.setLayoutData(fdata);

		// lblBackGround
		Label lblBg = new Label(getCompHeader(), SWT.NONE | SWT.SHADOW_IN);
		lblBg.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		fdata = new FormData();
		fdata.left = new FormAttachment(lblHeader, -6, SWT.LEFT);
		fdata.top = new FormAttachment(lblHeader, -6, SWT.TOP);
		fdata.bottom = new FormAttachment(lblHeader, 6, SWT.BOTTOM);
		fdata.right = new FormAttachment(lblHeader, 6, SWT.RIGHT);
		lblBg.setLayoutData(fdata);

		getCompHeader().pack();
		getCompHeader().redraw();
		// set bound after packing otherwise it resizes the composite
		Rectangle b = getShell().getBounds();
		getCompHeader().setBounds(0, 0, b.width, 55);

	}

	protected void buildCompButtons() {

		Composite myCmp = new Composite(getShell(), SWT.NONE);

		RowLayout rowlyt = new RowLayout();
		rowlyt.justify = true;
		rowlyt.pack = false;
		rowlyt.spacing = 10;
		myCmp.setLayout(rowlyt);

		RowData rowD = new RowData(170, 30);

		setCompButtons(new Composite(myCmp, SWT.NONE));
		getCompButtons().setLayout(rowlyt);


		// btnSave
		btnSave = new Button(getCompButtons(), SWT.NONE);
		btnSave.setText(btnSaveText); //$NON-NLS-1$
		btnSave.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnSave
		.setToolTipText(Messages.getString("genericformgui.button.save.tooltip")); //$NON-NLS-1$


		// btnClear
		btnClear = new Button(getCompButtons(), SWT.NONE);
		btnClear.setText(Messages.getString("genericformgui.button.clear.text")); //$NON-NLS-1$
		btnClear.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnClear.setToolTipText(Messages
				.getString("genericformgui.button.clear.tooltip")); //$NON-NLS-1$


		// btnCancel
		btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setText(Messages.getString("genericformgui.button.cancel.text")); //$NON-NLS-1$
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel.setToolTipText(Messages
				.getString("genericformgui.button.cancel.tooltip")); //$NON-NLS-1$


		// Adding button listener
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdSaveWidgetSelected();
			}
		});

		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdClearWidgetSelected();
			}
		});

		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});

		Control[] buttons = getCompButtons().getChildren();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setLayoutData(rowD);
		}

		getCompButtons().pack();
		Rectangle b = getShell().getBounds();
		myCmp.setBounds(0, b.height - 79, b.width, 40);
	}

	/**
	 * @return the compHeader
	 */
	protected Composite getCompHeader() {
		return compHeader;
	}

	/**
	 * @param compHeader
	 *            the compHeader to set
	 */
	protected void setCompHeader(Composite compHeader) {
		this.compHeader = compHeader;
	}

	/**
	 * @return the compButtons
	 */
	protected Composite getCompButtons() {
		return compButtons;
	}

	/**
	 * @param compButtons
	 *            the compButtons to set
	 */
	protected void setCompButtons(Composite compButtons) {
		this.compButtons = compButtons;
	}

	protected void cmdCloseSelected() {
		closeShell(true);
	}

	protected void cmdCancelSelected() {
		cmdCloseSelected();
	}

	/**
	 * Set the text to display on the save button if you do not wish to use the default value.
	 * Default value = Messages.getString("genericformgui.button.save.text")
	 * @param btnSaveText
	 */
	public void setBtnSaveText(String btnSaveText) {
		this.btnSaveText = btnSaveText;
	}
}
