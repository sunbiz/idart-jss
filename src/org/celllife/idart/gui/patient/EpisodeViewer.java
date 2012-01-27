package org.celllife.idart.gui.patient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.manager.AdministrationManager;
import model.manager.PatientManager;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.CommonObjects;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.gui.misc.iDARTChangeListener;
import org.celllife.idart.gui.platform.GenericOthersGui;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartColor;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.vafada.swtcalendar.SWTCalendarDialog;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 */
public class EpisodeViewer extends GenericOthersGui {

	private final Patient patient;

	private Composite compEpisodeTable;

	private Table tblEpisodes;

	private Label lblInstructions;

	private TableEditor editorTblEpisodes;

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");

	public boolean changesMade = false;

	private iDARTChangeListener changeListener;

	private final boolean isEditable;

	private int COL_START_DATE;

	private int COL_START_REASON;

	private int COL_START_NOTES;

	private int COL_CLINIC;

	private int COL_STOP_DATE;

	private int COL_STOP_REASON;

	private int COL_STOP_NOTES;

	/**
	 * Constructor for EpisodeViewer.
	 * 
	 * @param hSession
	 *            Session
	 * @param parent
	 *            Shell
	 * @param patient
	 *            Patient
	 * @param isEditable
	 */
	public EpisodeViewer(Session hSession, Shell parent, Patient patient,
			boolean isEditable) {
		super(parent, hSession);
		this.patient = patient;
		this.isEditable = isEditable;
	}

	public void openViewer() {
		activate();
		populateCompEpisodeList();
		if (isEditable) {
			attachTableEditor();
		}
	}

	@Override
	protected void createShell() {
		String shellTxt = "View / edit previous episodes";
		buildShell(shellTxt, new Rectangle(25, 0, 900, 500));

		createCompEpisodeTable();
	}

	@Override
	protected void createCompHeader() {
		String headerTxt = "View / Edit Previous Episodes";
		iDartImage icoImage = iDartImage.PATIENTARRIVES;
		buildCompHeader(headerTxt, icoImage);
	}

	private void createCompEpisodeTable() {
		compEpisodeTable = new Composite(getShell(), SWT.NONE);
		compEpisodeTable.setBounds(new Rectangle(5, 90, 885, 280));

		lblInstructions = new Label(compEpisodeTable, SWT.CENTER);
		lblInstructions.setBounds(new Rectangle(5, 5, 880, 40));
		lblInstructions.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8_ITALIC));
		lblInstructions
		.setText("Click in the table to edit information for previous episodes");

		tblEpisodes = new Table(compEpisodeTable, SWT.FULL_SELECTION
				| SWT.BORDER);
		tblEpisodes.setHeaderVisible(true);
		tblEpisodes.setLinesVisible(true);
		tblEpisodes.setBounds(new Rectangle(0, 45, 880, 220));
		tblEpisodes.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		TableColumn clmStartDate = new TableColumn(tblEpisodes, SWT.CENTER);
		clmStartDate.setText("Start Date");
		clmStartDate.setWidth(75);
		clmStartDate.setResizable(true);
		COL_START_DATE = 0;

		TableColumn clmStartReason = new TableColumn(tblEpisodes, SWT.CENTER);
		clmStartReason.setText("Start Reason");
		clmStartReason.setWidth(130);
		clmStartReason.setResizable(true);
		COL_START_REASON = 1;

		TableColumn clmStartNotes = new TableColumn(tblEpisodes, SWT.CENTER);
		clmStartNotes.setText("Start Notes");
		clmStartNotes.setWidth(175);
		clmStartNotes.setResizable(true);
		COL_START_NOTES = 2;

		TableColumn clmClinic = new TableColumn(tblEpisodes, SWT.CENTER);
		clmClinic.setText("Clinic");
		clmClinic.setWidth(100);
		clmClinic.setResizable(true);
		COL_CLINIC = 3;

		TableColumn clmStopDate = new TableColumn(tblEpisodes, SWT.CENTER);
		clmStopDate.setText("Stop Date");
		clmStopDate.setWidth(75);
		clmStopDate.setResizable(true);
		COL_STOP_DATE = 4;

		TableColumn clmStopReason = new TableColumn(tblEpisodes, SWT.CENTER);
		clmStopReason.setText("Stop Reason");
		clmStopReason.setWidth(130);
		clmStopReason.setResizable(true);
		COL_STOP_REASON = 5;

		TableColumn clmStopNotes = new TableColumn(tblEpisodes, SWT.CENTER);
		clmStopNotes.setText("Stop Notes");
		clmStopNotes.setWidth(175);
		clmStopNotes.setResizable(true);
		COL_STOP_NOTES = 6;
	}

	/**
	 * This method initializes compButtons
	 * 
	 */
	@Override
	protected void createCompButtons() {

		Button btnSave = new Button(getCompButtons(), SWT.NONE);
		btnSave.setText("Save");
		btnSave.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		if (isEditable) {
			btnSave
			.setToolTipText("Press this button to save the information \nyou have entered on this screen.");
			btnSave
			.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
				@Override
				public void widgetSelected(
						org.eclipse.swt.events.SelectionEvent e) {
					cmdSaveWidgetSelected();
				}
			});
		} else {
			btnSave.setEnabled(false);
		}

		Button btnCancel = new Button(getCompButtons(), SWT.NONE);
		btnCancel.setText("Cancel");
		btnCancel.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		btnCancel
		.setToolTipText("Press this button to close this screen.\nThe information you've entered here will be lost.");
		btnCancel
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				cmdCancelWidgetSelected();
			}
		});
	}

	private void populateCompEpisodeList() {
		tblEpisodes.clearAll();
		tblEpisodes.removeAll();

		Episode latestEp = PatientManager.getMostRecentEpisode(patient);
		List<Episode> epList = patient.getEpisodes();
		Collections.reverse(epList);
		for (Episode ep : epList) {

			if (((ep != null) && (!ep.isOpen()) && ((!latestEp.isOpen()) || (ep != latestEp)))) {
				TableItem ti = new TableItem(tblEpisodes, SWT.NONE);
				if (ep.getStartDate() != null) {
					ti.setText(COL_START_DATE, sdf.format(ep.getStartDate()));
				}
				if (ep.getStartReason() != null) {
					ti.setText(COL_START_REASON, ep.getStartReason());
				}
				if (ep.getStartNotes() != null) {
					ti.setText(COL_START_NOTES, ep.getStartNotes());
				}
				if (ep.getClinic() != null) {
					ti.setText(COL_CLINIC, ep.getClinic().getClinicName());
				}
				if (ep.getStopDate() != null) {
					ti.setText(COL_STOP_DATE, sdf.format(ep.getStopDate()));
				}
				if (ep.getStartReason() != null) {
					ti.setText(COL_STOP_REASON, ep.getStopReason());
				}
				if (ep.getStartNotes() != null) {
					ti.setText(COL_STOP_NOTES, ep.getStopNotes());
				}
				ti.setData(ep);
			}
		}
		Collections.reverse(epList);
	}

	public void attachTableEditor() {

		// add a editor for the accum column
		editorTblEpisodes = new TableEditor(tblEpisodes);
		editorTblEpisodes.horizontalAlignment = SWT.LEFT;
		editorTblEpisodes.grabHorizontal = true;

		tblEpisodes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent event) {
				// Dispose any existing editor
				Control old = editorTblEpisodes.getEditor();
				if (old != null) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tblEpisodes.getItem(pt);
				if (item != null) {
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = tblEpisodes.getColumnCount(); i < n; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							// This is the selected column
							column = i;
							break;
						}
					}

					// text boxes
					if (((column == COL_START_NOTES) || (column == COL_STOP_NOTES))
							&& (item.getData() != null)) {
						// Create the Text object for our editor

						final Text text = new Text(tblEpisodes, SWT.LEFT);
						text.setForeground(item.getForeground());
						text.setBackground(ResourceUtils
								.getColor(iDartColor.GRAY));
						text.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));

						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						editorTblEpisodes.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editorTblEpisodes.setEditor(text, item, column);

						final int col = column;
						text.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								item.setText(col, text.getText());

							}
						});

					}

					else if (((column == COL_START_DATE) || (column == COL_STOP_DATE))
							&& (item.getData() != null)) {
						getDateFromCalendar(item, column);

					}

					else if (((column == COL_START_REASON) || (column == COL_STOP_REASON))
							&& (item.getData() != null)) {
						final CCombo combo = new CCombo(tblEpisodes, SWT.LEFT);
						combo.setForeground(item.getForeground());
						combo.setBackground(item.getBackground());
						combo.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));

						combo.setText(item.getText(column));
						combo.setForeground(item.getForeground());
						combo.setFocus();

						editorTblEpisodes.minimumWidth = combo.getBounds().width;

						// Set the control into the editor
						editorTblEpisodes.setEditor(combo, item, column);

						final int col = column;

						if (column == COL_START_REASON) {
							CommonObjects.populateActivationReasons(
									getHSession(), combo);
						} else {
							CommonObjects.populateDeactivationReasons(
									getHSession(), combo);
						}

						combo.setText(item.getText(column));

						combo.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {

								item.setText(col, combo.getText());
							}
						});

					} else if (((column == COL_CLINIC))
							&& (item.getData() != null)) {
						final CCombo combo = new CCombo(tblEpisodes, SWT.LEFT);
						combo.setForeground(item.getForeground());
						combo.setBackground(item.getBackground());
						combo.setFont(ResourceUtils
								.getFont(iDartFont.VERASANS_8));

						combo.setText(item.getText(column));
						combo.setForeground(item.getForeground());
						combo.setFocus();

						editorTblEpisodes.minimumWidth = combo.getBounds().width;

						// Set the control into the editor
						editorTblEpisodes.setEditor(combo, item, column);

						final int col = column;

						CommonObjects.populateClinics(getHSession(), combo);

						combo.setText(item.getText(column));

						combo.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent event1) {
								item.setText(col, combo.getText());
							}
						});

					}
				}
			}
		});

	}

	/**
	 * Opens the SWTCalendar dialog for the user to choose a date
	 * 
	 * @param ti
	 *            TableItem
	 * @param col
	 *            int
	 */
	private void getDateFromCalendar(final TableItem ti, final int col) {
		final SWTCalendarDialog cal = new SWTCalendarDialog(getShell());

		try {
			Date parse = sdf.parse(ti.getText(col));
			cal.setDate(parse);
		} catch (ParseException e1) {
			e1.printStackTrace();
			cal.setDate(new Date());
		}


		cal.addDateChangedListener(new SWTCalendarListener() {
			@Override
			public void dateChanged(SWTCalendarEvent calendarEvent) {
				if (calendarEvent.type == SWTCalendarEvent.DAY) {
					cal.close();
					try {
						Date selectedDate = calendarEvent.getCalendar()
						.getTime();
						ti.setText(col, sdf.format(selectedDate));
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		});
		cal.open();
	}

	/**
	 * Method getNewEpisodeFromTableItem.
	 * 
	 * @param ti
	 *            TableItem
	 * @return Episode
	 */
	private Episode getNewEpisodeFromTableItem(TableItem ti) {

		Date startDate = null;
		Date stopDate = null;
		String startReason = ti.getText(COL_START_REASON);
		String stopReason = ti.getText(COL_STOP_REASON);
		String clinic = ti.getText(COL_CLINIC);
		String startNotes = ti.getText(COL_START_NOTES);
		String stopNotes = ti.getText(COL_STOP_NOTES);

		try {
			startDate = sdf.parse(ti.getText(COL_START_DATE));
		} catch (ParseException p) {
			getLog().error(
					"Parse error while setting episode date in episode editor. String was: "
					+ ti.getText(COL_START_DATE));
		}

		try {
			stopDate = sdf.parse(ti.getText(COL_STOP_DATE));
		} catch (ParseException p) {
			getLog().error(
					"Parse error while setting episode date in episode editor. String was: "
					+ ti.getText(COL_STOP_DATE));
		}

		return new Episode(patient, startDate, stopDate, startReason,
				stopReason, startNotes, stopNotes, AdministrationManager
				.getClinic(getHSession(), clinic));
	}

	private void cmdSaveWidgetSelected() {

		boolean validated = true;

		// Validate if episode have more then one
		// 'New Patient' or 'Deceased' Episodes.
		validated = processEpisodeConsistency();
		if (validated) {
			for (TableItem ti : tblEpisodes.getItems()) {
				Episode newEp = getNewEpisodeFromTableItem(ti);
				if (!newEp.equals(ti.getData())) {
					changesMade = true;

					Map<String, String> data = PatientManager.validateEpisode(
							newEp.getStartDate(), newEp.getStartReason(), newEp
							.getStopDate(), newEp.getStopReason());
					validated = data.get("result").equalsIgnoreCase("true");
					String title = data.get("title");
					String message = data.get("message");

					// if validation fails show error message
					if (!validated) {
						MessageBox validationError = new MessageBox(getShell(),
								SWT.ICON_ERROR | SWT.OK);
						validationError.setText(title);
						validationError.setMessage(message);
						validationError.open();
						break;
					} else {
						Transaction tx = getHSession().beginTransaction();
						try {
							Episode ep = (Episode) ti.getData();
							ep.setStartDate(newEp.getStartDate());
							ep.setStartReason(newEp.getStartReason());
							ep.setStartNotes(newEp.getStartNotes());
							ep.setStopDate(newEp.getStopDate());
							ep.setStopReason(newEp.getStopReason());
							ep.setStopNotes(newEp.getStopNotes());
							ep.setClinic(newEp.getClinic());

							getHSession().update(ep);
							patient.updateClinic();

							getHSession().flush();
							tx.commit();

							MessageBox m = new MessageBox(getShell(), SWT.OK
									| SWT.ICON_INFORMATION);
							m.setText("Epsiode Updated");
							m.setMessage("Patient '".concat(
									patient.getPatientId()).concat(
									"''s episode has been updated"));
							m.open();
						} catch (HibernateException e) {
							getLog().error("Episode Viewer not able to save.",
									e);
							if (tx != null) {
								tx.rollback();
							}
						}
					}
				}
			}

			if (validated) {
				cmdCancelWidgetSelected();
				// must check there is a listener becuase not all GUI that load
				// the
				// EpisodeViewer want to listen for changes to the episodes e.g.
				// PackageReturn
				iDARTChangeListener changeListenerObject = this.changeListener;
				if (changeListenerObject != null) {
					changeListenerObject.changed(new Episode());
				}
			}
		}
	}

	/**
	 * 
	 * Method which looks at the current content in the episodes table, in order
	 * to determine if the episode start reason and stop reason are consistent,
	 * that is that none have any duplicates.
	 * 
	 * @return boolean
	 */
	private boolean processEpisodeConsistency() {
		MessageBox msgBox = new MessageBox(getShell(), SWT.ICON_ERROR
				| SWT.PRIMARY_MODAL);
		boolean consistent = true;
		// Verify if more then one of the target episodes have been assigned in
		// the
		// Table editor.
		int newPatientCnt = 0;
		int deceasedCnt = 0;
		TableItem[] tblItems = tblEpisodes.getItems();
		for (TableItem tblItem : tblItems) {
			String startReason = tblItem.getText(1); // Start reason
			String stopReason = tblItem.getText(5); // Stop reason
			if (startReason.equalsIgnoreCase(Episode.REASON_NEW_PATIENT)) {
				newPatientCnt++;
				if (newPatientCnt > 1) {
					// Warn of duplicate
					// fail the check.
					String txt = "This patient already has an episode with its 'Start Reason' set "
						+ "to 'New Patient'. \n\nNote that 'New Patient' implies that the patient is "
						+ "initiated on ART at your facility, so you cannot have more than 1 "
						+ "episode with this 'Start Reason'. \n\nPlease change the 'Start Reason' "
						+ "for the previous episode.";
					msgBox
					.setText("Only 1 'New Patient' Episode Allowed Per Patient");
					msgBox.setMessage(txt);
					msgBox.open();
					consistent = false;
					break;
				}
			} 
			if (stopReason.equalsIgnoreCase(Episode.REASON_DECEASED)) {
				deceasedCnt++;
				if (deceasedCnt > 1) {
					// Warn of duplicate
					// fail the check.
					String txt = "This patient has already been marked as"
						+ " 'Deceased' in a previous episode.\n\nPlease change"
						+ " the 'Stop Reason' to make sure that only 1 episode "
						+ "has this set to 'Deceased'.";
					msgBox
					.setText("Only 1 'Deceased' Episode Allowed Per Patient");
					msgBox.setMessage(txt);
					msgBox.open();
					consistent = false;
					break;
				}
			}

		}

		return consistent;
	}

	public void setSession(Session session) {
		setHSession(session);
	}

	private void cmdCancelWidgetSelected() {
		closeShell(false);
	}

	@Override
	protected void createCompOptions() {
	}

	@Override
	protected void setLogger() {
		setLog(Logger.getLogger(this.getClass()));
	}

	public void addChangeListener(iDARTChangeListener listener) {
		this.changeListener = listener;
	}
}
