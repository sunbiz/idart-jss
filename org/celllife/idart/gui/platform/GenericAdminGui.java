package org.celllife.idart.gui.platform;

import java.text.MessageFormat;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 */
public abstract class GenericAdminGui extends GenericGui {

	protected Composite compHeader;

	protected Composite compOptions;

	protected Composite compBackButton;

	protected Label lblPicLogo;

	private Label lblBackButton;

	private Button btnBackButton;

	private Label lblHeader;

	private Label lblIcon;

	// --- Mandatory Abstract methods
	/**
	 * This method is called during the class initialisation and should be used
	 * to call the {@link GenericAdminGui#buildCompHeader(String, String)}
	 * method.
	 * 
	 * @see GenericAdminGui#buildCompHeader(String, iDartImage)
	 */
	protected abstract void createCompHeader();

	/**
	 * This method is called during the class initialisation to allow the user
	 * to add groups and buttons to the options composite.
	 * 
	 * @see GenericAdminGui#buildCompOptions()
	 */
	protected abstract void createCompOptions();

	protected abstract void cmdCloseSelectedWidget();

	/**
	 * Constructor for GenericAdminGui.
	 * 
	 * @param parent
	 *            Shell
	 */
	public GenericAdminGui(Shell parent) {
		super(parent, null);
		activate();
	}

	/**
	 * This method sets the shell options, title and bounds, and calls the other
	 * initialisation methods.
	 * 
	 * @param shellTitle
	 * @see GenericAdminGui#createCompHeader()
	 * @see GenericAdminGui#createCompOptions()
	 */
	protected void buildShell(String shellTitle) {

		String title = Messages.getString("common.screen.title"); //$NON-NLS-1$
		
		// show class names in title to if env variable set
		if (System.getenv("idart.debug.ui") != null){
			shellTitle += " - " + this.getClass().getName();
		}
		
		getShell().setText(
				MessageFormat.format(title, shellTitle, LocalObjects.getUser(
						getHSession()).getUsername()));
		getShell().setBounds(
new Rectangle(0, 0, 900, 700));
		Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
		getShell().setImage(i);

		Image myIcon = ResourceUtils.getImage(iDartImage.LOGO_IDART);
		// lblPicLogo
		lblPicLogo = new Label(getShell(), SWT.NONE);
		lblPicLogo.setImage(myIcon);
		lblPicLogo.setBounds(new Rectangle(650, 565, 240, 100));

		// Building the shell for the GenericAdminGui =====
		createCompHeader();
		buildCompOptions();
		createCompOptions();
		buildCompBackButton();
	}

	/**
	 * Method buildCompHeader.
	 * 
	 * @param titleText
	 *            String
	 * @param icoImage
	 *            iDartImage
	 */
	protected void buildCompHeader(String titleText, iDartImage icoImage) {
		setCompHeader(new Composite(getShell(), SWT.NONE));
		getCompHeader().setBounds(new Rectangle(111, 15, 680, 52));

		// lblIcon
		lblIcon = new Label(getCompHeader(), SWT.NONE);
		lblIcon.setBounds(new Rectangle(0, 1, 50, 43));
		lblIcon.setImage(ResourceUtils.getImage(icoImage));

		// lblHeader
		lblHeader = new Label(getCompHeader(), SWT.CENTER | SWT.SHADOW_IN);
		lblHeader.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		// lblHeader.setForeground(ResourceUtils.getColor(iDartColor.WHITE));
		lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		lblHeader.setBounds(new Rectangle(60, 11, 600, 30));
		lblHeader.setText(titleText);

		// lblBackGround
		Label lblBg = new Label(getCompHeader(), SWT.CENTER | SWT.SHADOW_IN);
		lblBg.setBounds(new Rectangle(60, 6, 600, 30));
		lblBg.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));

	}

	protected void buildCompBackButton() {
		setCompBackButton(new Composite(getShell(), SWT.NONE));
		getCompBackButton().setBounds(new Rectangle(20, 565, 100, 90));
		lblBackButton = new Label(getCompBackButton(), SWT.CENTER);
		lblBackButton.setBounds(new Rectangle(0, 0, 100, 30));
		lblBackButton.setText(Messages.getString("genericadmingui.button.back")); //$NON-NLS-1$
		lblBackButton.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnBackButton = new Button(getCompBackButton(), SWT.NONE);
		btnBackButton.setBounds(new Rectangle(0, 30, 100, 60));
		btnBackButton
		.setToolTipText(Messages.getString("genericadmingui.button.back.tooltip")); //$NON-NLS-1$
		btnBackButton.setImage(ResourceUtils.getImage(iDartImage.BACKARROW));
		btnBackButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCloseSelectedWidget();
			}
		});
	}

	protected void cmdCloseSelected() {
		closeShell(true);
	}

	protected void buildCompOptions() {
		setCompOptions(new Composite(getShell(), SWT.NONE));
		getCompOptions().setBounds(new Rectangle(45, 80, 810, 485));
	}

	/**
	 * @return the compBackButton
	 */
	public Composite getCompBackButton() {
		return compBackButton;
	}

	/**
	 * @param compBackButton
	 *            the compBackButton to set
	 */
	public void setCompBackButton(Composite compBackButton) {
		this.compBackButton = compBackButton;
	}

	/**
	 * @return the compHeader
	 */
	public Composite getCompHeader() {
		return compHeader;
	}

	/**
	 * @param compHeader
	 *            the compHeader to set
	 */
	public void setCompHeader(Composite compHeader) {
		this.compHeader = compHeader;
	}

	/**
	 * @return the compOptions
	 */
	public Composite getCompOptions() {
		return compOptions;
	}

	/**
	 * @param compOptions
	 *            the compOptions to set
	 */
	public void setCompOptions(Composite compOptions) {
		this.compOptions = compOptions;
	}

}
