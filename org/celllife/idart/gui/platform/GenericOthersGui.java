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
import org.eclipse.swt.graphics.Point;
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
public abstract class GenericOthersGui extends GenericGui {

	private Composite compHeader;

	private Composite compButtons;

	protected Label lblIcon;

	protected Label lblHeader;

	protected Label lblBg;

	/**
	 * Constructor for GenericOthersGui.
	 * @param parent Shell
	 * @param hSession Session
	 */
	public GenericOthersGui(Shell parent, Session hSession) {
		super(parent, hSession);
	}

	protected abstract void createCompHeader();

	protected abstract void createCompButtons();

	protected abstract void createCompOptions();

	/**
	 * Method buildShell.
	 * @param shellTxt String
	 * @param bounds Rectangle
	 */
	protected void buildShell(String shellTitle, Rectangle bounds) {
		getShell().setBounds(bounds);
		String title = Messages.getString("common.screen.title"); //$NON-NLS-1$
		
		// show class names in title to if env variable set
		if (System.getenv("idart.debug.ui") != null){
			shellTitle += " - " + this.getClass().getName();
		}
		
		getShell().setText(
				MessageFormat.format(title, shellTitle, LocalObjects.getUser(
						getHSession()).getUsername()));
		Image i = ResourceUtils.getImage(iDartImage.LOGO_GRAPHIC);
		getShell().setImage(i);
		createCompOptions();
		buildStdButtons();
		createCompHeader();
	}

	/**
	 * Method buildCompHeader.
	 * @param txt String
	 * @param icoImage iDartImage
	 */
	protected void buildCompHeader(String txt, iDartImage icoImage) {

		// compHeader
		setCompHeader(new Composite(getShell(), SWT.NONE));
		getCompHeader().setLayout(new FormLayout());

		FormData fd = new FormData();
		fd.left = new FormAttachment(25, 0);
		fd.right = new FormAttachment(75, 0);
		fd.top = new FormAttachment(0, 15);

		// lblHeader
		lblHeader = new Label(compHeader, SWT.CENTER | SWT.SHADOW_IN);
		lblHeader.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		lblHeader.setFont(ResourceUtils.getFont(iDartFont.VERASANS_14));
		lblHeader.setText(txt);
		lblHeader.setLayoutData(fd);

		fd = new FormData();
		fd.right = new FormAttachment(lblHeader, -15, SWT.LEFT);
		fd.top = new FormAttachment(lblHeader, 0, SWT.CENTER);

		// lblIcon
		lblIcon = new Label(compHeader, SWT.NONE);
		try {
			lblIcon.setImage(ResourceUtils.getImage(icoImage));
		} catch (Exception e) {
			e.printStackTrace();
		}
		lblIcon.setLayoutData(fd);

		fd = new FormData();
		fd.left = new FormAttachment(lblHeader, -5, SWT.LEFT);
		fd.top = new FormAttachment(lblHeader, -5, SWT.TOP);
		fd.bottom = new FormAttachment(lblHeader, 5, SWT.BOTTOM);
		fd.right = new FormAttachment(lblHeader, 5, SWT.RIGHT);

		// lblBackGround
		lblBg = new Label(getCompHeader(), SWT.NONE | SWT.SHADOW_IN);
		lblBg.setBackground(ResourceUtils
				.getColor(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND));
		lblBg.setLayoutData(fd);

		getCompHeader().pack();
		// Set bounds after pack, otherwise it resizes the composite
		Rectangle b = getShell().getBounds();
		getCompHeader().setBounds(0, 5, b.width, 70);

	}

	protected void buildStdButtons() {

		Composite myCmp = new Composite(getShell(), SWT.NONE);

		RowLayout rowlyt = new RowLayout();
		rowlyt.justify = true;
		rowlyt.pack = false;
		rowlyt.spacing = 10;
		myCmp.setLayout(rowlyt);

		setCompButtons(new Composite(myCmp, SWT.NONE));
		getCompButtons().setLayout(rowlyt);

		createCompButtons();

		Control[] buttons = getCompButtons().getChildren();
		for (int i = 0; i < buttons.length; i++) {
			RowData rdata = new RowData(
					(buttons.length < 4 ? new Point(150, 30) : new Point(100,
							30)));
			buttons[i].setLayoutData(rdata);
		}

		myCmp.pack();
		Rectangle bnd = getShell().getBounds();
		myCmp.setBounds(0, bnd.height - 75, bnd.width, 35);

	}

	/**
	 * Method newFormButton.
	 * @param type int
	 * @return Button
	 */
	protected Button newFormButton(int type) {
		return new Button(getCompButtons(), type);
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

}
