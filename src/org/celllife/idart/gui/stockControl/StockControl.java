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

package org.celllife.idart.gui.stockControl;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.gui.deletions.DeleteStockPrescriptionsPackages;
import org.celllife.idart.gui.deletions.DestroyStock;
import org.celllife.idart.gui.packaging.NewPatientPackaging;
import org.celllife.idart.gui.packaging.PackageReturn;
import org.celllife.idart.gui.packaging.PackagesToOrFromClinic;
import org.celllife.idart.gui.packaging.PackagesToPatients;
import org.celllife.idart.gui.platform.GenericAdminGui;
import org.celllife.idart.gui.prescription.AddPrescription;
import org.celllife.idart.gui.stockArrives.StockArrives;
import org.celllife.idart.gui.stockTake.StockTakeGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.gui.welcome.GenericWelcome;
import org.celllife.idart.messages.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

/**
 */
public class StockControl extends GenericAdminGui {

	private Button btnStockArrives;

	private Button btnPatientPackaging;

	private Button btnDistributePackagesToClinic;

	private Label lblStockTake;

	private Button btnStockTake;

	private Label lblStockArrives;

	private Label lblPatientPackaging;

	private Label lblDistributePackages;

	private Button btnPrescription;

	private Label lblPrescription;

	private Label lblDispenseToPatients;

	private Button btnDispenseToPatients;

	private Label lblReturnStock;

	private Button btnReturnStock;

	private Label lblDestroyStock;

	private Button btnDestroyStock;

	/**
	 * Default Constructor
	 */
	public StockControl() {
		super(GenericWelcome.shell);
	}

	/**
	 * This method initializes the shell newPatientAdmin
	 */
	@Override
	protected void createShell() {
		buildShell(Messages.getString("StockControl.shell.title")); //$NON-NLS-1$
	}

	/**
	 * This method initializes compHeader
	 *
	 */
	@Override
	protected void createCompHeader() {
		String text = Messages.getString("StockControl.header"); //$NON-NLS-1$
		iDartImage icoImage = iDartImage.STOCKCONTROL;
		buildCompHeader(text, icoImage);
	}

	/**
	 * This method initializes compOptions
	 *
	 */
	@Override
	protected void createCompOptions() {
		// lblPrescription
		lblPrescription = new Label(compOptions, SWT.NONE);
		lblPrescription.setBounds(new Rectangle(50, 20, 50, 43));
		lblPrescription.setImage(ResourceUtils
				.getImage(iDartImage.PRESCRIPTIONNEW));
		lblPrescription.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdUpdatePrescriptionWidgetSelected();
			}
		});

		// btnPrescriptionz
		btnPrescription = new Button(compOptions, SWT.NONE);
		btnPrescription.setBounds(new Rectangle(105, 22, 260, 40));
		btnPrescription
		.setToolTipText(Messages.getString("StockControl.button.updatePrescription.tooltip")); //$NON-NLS-1$
		btnPrescription.setText(Messages.getString("StockControl.button.updatePrescription")); //$NON-NLS-1$
		btnPrescription.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPrescription
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdUpdatePrescriptionWidgetSelected();
			}
		});

		// lblPatientPackaging
		lblPatientPackaging = new Label(compOptions, SWT.NONE);
		lblPatientPackaging.setBounds(new Rectangle(50, 100, 50, 43));
		lblPatientPackaging.setImage(ResourceUtils
				.getImage(iDartImage.DISPENSEPACKAGES));
		lblPatientPackaging.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdPatientPackagingSelected();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		// btnPatientPackaging
		btnPatientPackaging = new Button(compOptions, SWT.NONE);
		btnPatientPackaging.setText(Messages.getString("StockControl.button.packaging")); //$NON-NLS-1$
		btnPatientPackaging
		.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnPatientPackaging
		.setToolTipText(Messages.getString("StockControl.button.packaging.tooltip")); //$NON-NLS-1$
		btnPatientPackaging.setBounds(new Rectangle(105, 102, 260, 40));
		btnPatientPackaging
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdPatientPackagingSelected();
			}
		});

		lblDispenseToPatients = new Label(compOptions, SWT.NONE);
		lblDispenseToPatients.setBounds(new Rectangle(50, 180, 50, 43));
		lblDispenseToPatients.setImage(ResourceUtils
				.getImage(iDartImage.PATIENTARRIVES));

		lblDispenseToPatients.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdDispenseToPatientsSelected();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		btnDispenseToPatients = new Button(compOptions, SWT.NONE);
		btnDispenseToPatients.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		btnDispenseToPatients.setBounds(new Rectangle(105, 182, 260, 40));
		btnDispenseToPatients.setText(Messages.getString("StockControl.button.scanOut")); //$NON-NLS-1$
		btnDispenseToPatients
		.setToolTipText(Messages.getString("StockControl.button.scanOut.tooltip")); //$NON-NLS-1$
		btnDispenseToPatients
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdDispenseToPatientsSelected();
			}
		});



		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.ONLINE_DOWNREFERRAL_MODE)) {

			// lblDistributePackages
			lblDistributePackages = new Label(compOptions, SWT.NONE);
			lblDistributePackages.setBounds(new Rectangle(50, 260, 50, 43));
			lblDistributePackages.setImage(ResourceUtils
					.getImage(iDartImage.OUTGOINGPACKAGES));

			lblDistributePackages.addMouseListener(new MouseListener() {

				@Override
				public void mouseUp(MouseEvent mu) {
					cmdDistributePackagesToClinicSelected();
				}

				@Override
				public void mouseDown(MouseEvent md) {
				}

				@Override
				public void mouseDoubleClick(MouseEvent dc) {
				}
			});

			// btnDistributePackages
			btnDistributePackagesToClinic = new Button(compOptions, SWT.NONE);
			btnDistributePackagesToClinic
			.setBounds(new Rectangle(105, 262, 260, 40));
			String label = Messages.getString("StockControl.button.scanDownRefer"); //$NON-NLS-1$

			btnDistributePackagesToClinic
			.setText(label);
			btnDistributePackagesToClinic.setFont(ResourceUtils
					.getFont(iDartFont.VERASANS_8));
			btnDistributePackagesToClinic
			.setToolTipText(Messages.getString("StockControl.button.scanDownRefer.tooltip")); //$NON-NLS-1$

			btnDistributePackagesToClinic
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmdDistributePackagesToClinicSelected();
				}
			});
		}

		// Return Uncollected Packages
		lblReturnStock = new Label(compOptions, SWT.NONE);
		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.ONLINE_DOWNREFERRAL_MODE)) {
			lblReturnStock.setBounds(new Rectangle(50, 340, 50, 43));
		} else {
			lblReturnStock.setBounds(new Rectangle(50, 260, 50, 43));
		}
		lblReturnStock.setImage(ResourceUtils
				.getImage(iDartImage.PACKAGERETURN));
		lblReturnStock.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdReturnPackage();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		btnReturnStock = new Button(compOptions, SWT.NONE);
		btnReturnStock.setText(Messages.getString("StockControl.button.returnPackages")); //$NON-NLS-1$
		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.ONLINE_DOWNREFERRAL_MODE)) {
			btnReturnStock.setBounds(new Rectangle(105, 342, 260, 40));
		} else {
			btnReturnStock.setBounds(new Rectangle(105, 262, 260, 40));
		}
		btnReturnStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnReturnStock
		.setToolTipText(Messages.getString("StockControl.button.returnPackages.tooltip")); //$NON-NLS-1$
		btnReturnStock.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent mu) {
				cmdReturnPackage();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		// lblStockArrives
		lblStockArrives = new Label(compOptions, SWT.NONE);
		lblStockArrives.setBounds(new Rectangle(415, 22, 50, 43));
		lblStockArrives.setImage(ResourceUtils
				.getImage(iDartImage.PACKAGESARRIVE));
		lblStockArrives.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdStockArrivesSelected();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		// btnStockArrives
		btnStockArrives = new Button(compOptions, SWT.NONE);
		btnStockArrives.setText(Messages.getString("StockControl.button.stockArrives")); //$NON-NLS-1$
		btnStockArrives.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnStockArrives
		.setToolTipText(Messages.getString("StockControl.button.stockArrives.tooltip")); //$NON-NLS-1$
		btnStockArrives.setBounds(new Rectangle(470, 22, 260, 40));
		btnStockArrives
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdStockArrivesSelected();
			}
		});

		// lblDestroyStock
		lblDestroyStock = new Label(compOptions, SWT.NONE);
		lblDestroyStock.setBounds(new Rectangle(415, 100, 50, 43));
		lblDestroyStock
		.setImage(ResourceUtils.getImage(iDartImage.DRUGALLERGY));
		lblDestroyStock.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdDestroyStockSelected();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		// btnDestroyStock
		btnDestroyStock = new Button(compOptions, SWT.NONE);
		btnDestroyStock.setBounds(new Rectangle(470, 102, 260, 40));
		btnDestroyStock.setText(Messages.getString("StockControl.button.destroyStock")); //$NON-NLS-1$
		btnDestroyStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnDestroyStock
		.setToolTipText(Messages.getString("StockControl.button.destroyStock.tooltip")); //$NON-NLS-1$
		btnDestroyStock
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdDestroyStockSelected();
			}
		});

		// lblReturnStock
		lblReturnStock = new Label(compOptions, SWT.NONE);
		lblReturnStock.setBounds(new Rectangle(415, 180, 50, 43));
		lblReturnStock.setImage(ResourceUtils.getImage(iDartImage.REDOPACKAGE));
		lblReturnStock.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdReturnStockSelected();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		// btnReturnStock
		btnReturnStock = new Button(compOptions, SWT.NONE);
		btnReturnStock.setBounds(new Rectangle(470, 182, 260, 40));
		btnReturnStock.setText(Messages.getString("StockControl.button.deletions")); //$NON-NLS-1$
		btnReturnStock.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnReturnStock
		.setToolTipText(Messages.getString("StockControl.button.deletions.tooltip")); //$NON-NLS-1$
		btnReturnStock
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdReturnStockSelected();
			}
		});
		// lblStockTake
		lblStockTake = new Label(compOptions, SWT.NONE);
		lblStockTake.setBounds(new Rectangle(415, 260, 50, 43));
		lblStockTake.setImage(ResourceUtils
				.getImage(iDartImage.PRESCRIPTIONNEW));
		lblStockTake.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent mu) {
				cmdReturnStockSelected();
			}

			@Override
			public void mouseDown(MouseEvent md) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent dc) {
			}
		});

		// btnStockTake
		btnStockTake = new Button(compOptions, SWT.NONE);
		btnStockTake.setBounds(new Rectangle(470, 262, 260, 40));
		btnStockTake.setText(Messages.getString("StockControl.button.stocktake")); //$NON-NLS-1$
		btnStockTake.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnStockTake
		.setToolTipText(Messages.getString("StockControl.button.stocktake.tooltip")); //$NON-NLS-1$

		btnStockTake
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdStockTakeSelected();
			}
		});

	}

	private void cmdReturnPackage() {
		new PackageReturn(getShell());
	}

	private void cmdStockArrivesSelected() {
		new StockArrives(getShell());
	}

	private void cmdPatientPackagingSelected() {
		new NewPatientPackaging(getShell());
	}

	private void cmdDispenseToPatientsSelected() {
		PackagesToPatients.addInitialisationOption("isAtRemoteClinic", false); //$NON-NLS-1$
		new PackagesToPatients(getShell());
	}


	private void cmdDistributePackagesToClinicSelected() {
		if (iDartProperties.downReferralMode
				.equalsIgnoreCase(iDartProperties.ONLINE_DOWNREFERRAL_MODE)) {
			PackagesToOrFromClinic.addInitialisationOption("isScanOut", true); //$NON-NLS-1$
			new PackagesToOrFromClinic(getShell());
		}
	}

	private void cmdUpdatePrescriptionWidgetSelected() {
		new AddPrescription(null, getShell(), false);
	}

	private void cmdReturnStockSelected() {
		new DeleteStockPrescriptionsPackages(getShell());
	}

	private void cmdDestroyStockSelected() {
		new DestroyStock(getShell());
	}

	private void cmdStockTakeSelected() {
		new StockTakeGui(getShell());
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	@Override
	protected void cmdCloseSelectedWidget() {
		cmdCloseSelected();
	}

}
