package org.celllife.idart.gui.dataExports;

import model.manager.exports.EpisodeColumnsGroup;
import model.manager.exports.ExportColumn;
import model.manager.exports.columns.ColumnModifier;
import model.manager.exports.columns.EpisodeDetailsEnum;
import model.manager.exports.columns.IColumnEnum;

import org.celllife.idart.gui.dataExports.listViewerUtils.ExportListViewerContentProvider;
import org.celllife.idart.gui.dataExports.listViewerUtils.ExportListViewerLabelProvider;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EpisodeGroupGUI implements iDataExport {

	private Composite compTblModifiers;
	private CCombo columnModifier;
	private Text txtModifierNum;
	private int ypos;
	private CheckboxTableViewer tblViewerEpisodeAttributes;
	private Label lblHead;
	private Button rdBtnRecent;
	private Button rdBtnOldest;
	private Group grpSelectOption;
	private Label lblNumRecentEpisodes;
	private Label lblNumOldestEpisodes;
	private Text txtNumRecent;
	private Text txtNumOldest;
	private final Object[] episodeFields;
	private EpisodeColumnsGroup episodeGroup;

	public EpisodeGroupGUI() {
		episodeFields = EpisodeDetailsEnum.values();
	}

	@Override
	public void createView(Composite compDetails) {

		ypos = 0;

		tblViewerEpisodeAttributes = CheckboxTableViewer.newCheckList(
				compDetails, SWT.BORDER);
		tblViewerEpisodeAttributes.getTable().setBounds(
				new org.eclipse.swt.graphics.Rectangle(0, ypos, 250, 240));
		tblViewerEpisodeAttributes.getTable().setFont(
				ResourceUtils.getFont(iDartFont.VERASANS_8));
		tblViewerEpisodeAttributes
		.setContentProvider(new ExportListViewerContentProvider());
		tblViewerEpisodeAttributes
		.setLabelProvider(new ExportListViewerLabelProvider());
		tblViewerEpisodeAttributes.setInput(episodeFields);

		ypos = 0;
		compTblModifiers = new Composite(compDetails, SWT.NONE);
		compTblModifiers.setBounds(new Rectangle(0, 250, 250, 80));

		lblHead = new Label(compTblModifiers, SWT.LEFT);
		lblHead.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		lblHead.setBounds(new Rectangle(4, 5, 150, 15));
		lblHead.setText("I want to see:");

		grpSelectOption = new Group(compTblModifiers, SWT.NONE);
		grpSelectOption.setBounds(new Rectangle(5, 20, 238, 60));
		grpSelectOption.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));

		int row1Y = 8;
		int row2Y = 28;
		lblNumRecentEpisodes = new Label(grpSelectOption, SWT.LEFT);
		lblNumRecentEpisodes.setText("episode(s)");
		lblNumRecentEpisodes.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblNumRecentEpisodes.setBounds(143, row1Y + 6, 70, 16);

		lblNumOldestEpisodes = new Label(grpSelectOption, SWT.LEFT);
		lblNumOldestEpisodes.setText("episode(s)");
		lblNumOldestEpisodes.setFont(ResourceUtils
				.getFont(iDartFont.VERASANS_8));
		lblNumOldestEpisodes.setBounds(143, row2Y + 6, 70, 16);

		rdBtnRecent = new Button(grpSelectOption, SWT.RADIO);
		rdBtnRecent.setBounds(new Rectangle(5, row1Y, 95, 25));
		rdBtnRecent.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnRecent.setText("most recent");
		rdBtnRecent.setSelection(true);

		rdBtnOldest = new Button(grpSelectOption, SWT.RADIO);
		rdBtnOldest.setBounds(new Rectangle(5, row2Y, 95, 25));
		rdBtnOldest.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		rdBtnOldest.setText("oldest");

		txtNumRecent = new Text(grpSelectOption, SWT.NONE | SWT.BORDER);
		txtNumRecent.setText("1");
		txtNumRecent.setBounds(108, row1Y + 3, 30, 18);
		txtNumRecent.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNumRecent
		.addFocusListener(new org.eclipse.swt.events.FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				rdBtnRecent.setSelection(true);
				rdBtnOldest.setSelection(false);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});

		txtNumOldest = new Text(grpSelectOption, SWT.NONE | SWT.BORDER);
		txtNumOldest.setText("1");
		txtNumOldest.setBounds(108, row2Y + 5, 30, 18);
		txtNumOldest.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		txtNumOldest
		.addFocusListener(new org.eclipse.swt.events.FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				rdBtnRecent.setSelection(false);
				rdBtnOldest.setSelection(true);
			}

			@Override
			public void focusLost(FocusEvent arg0) {
			}
		});

	}

	public void cmbcolumnModifierSelected() {
		String selection = columnModifier.getText();
		if (selection.endsWith("NUM")) {
			txtModifierNum.setEnabled(true);
		} else {
			txtModifierNum.setEnabled(false);
		}
	}

	@Override
	public void updateView(ExportColumn column) {

		EpisodeColumnsGroup eg = (EpisodeColumnsGroup) column;
		IColumnEnum[] values = eg.getColumns();
		tblViewerEpisodeAttributes.setCheckedElements(values);

		int EpisodesNum = eg.getModifierNum();

		if ((eg.getModifier() == ColumnModifier.MODIFIER_NEWEST)
				|| (eg.getModifier() == ColumnModifier.MODIFIER_NEWEST_NUM)) {
			txtNumRecent.setText("" + EpisodesNum);
			rdBtnRecent.setSelection(true);
			rdBtnOldest.setSelection(false);

		} else if ((eg.getModifier() == ColumnModifier.MODIFIER_OLDEST)
				|| (eg.getModifier() == ColumnModifier.MODIFIER_OLDEST_NUM)) {
			txtNumOldest.setText("" + EpisodesNum);
			rdBtnRecent.setSelection(false);
			rdBtnOldest.setSelection(true);

		}

	}

	@Override
	public EpisodeColumnsGroup getColumn() {

		Object[] episodeSelections = tblViewerEpisodeAttributes
		.getCheckedElements();

		// no episode fields were selected
		if (episodeSelections == null || episodeSelections.length == 0)
			return null;

		EpisodeDetailsEnum[] episodeSelection = new EpisodeDetailsEnum[episodeSelections.length];

		for (int i = 0; i < episodeSelections.length; i++) {
			episodeSelection[i] = (EpisodeDetailsEnum) episodeSelections[i];
		}

		int EpisodesNum = 0;
		ColumnModifier cm;
		if (rdBtnOldest.getSelection()) {
			EpisodesNum = Integer.parseInt(txtNumOldest.getText());
			cm = (EpisodesNum <= 1) ? ColumnModifier.MODIFIER_OLDEST
					: ColumnModifier.MODIFIER_OLDEST_NUM;
		} else {
			EpisodesNum = Integer.parseInt(txtNumRecent.getText());
			cm = (EpisodesNum <= 1) ? ColumnModifier.MODIFIER_NEWEST
					: ColumnModifier.MODIFIER_NEWEST_NUM;

		}

		episodeGroup = new EpisodeColumnsGroup(cm, new Integer(EpisodesNum),
				episodeSelection);

		return episodeGroup;

	}

	@Override
	public boolean fieldsOk() {

		try {

			@SuppressWarnings("unused")
			int numEpisodes = (rdBtnOldest.getSelection()) ? Integer
					.parseInt(txtNumOldest.getText()) : Integer
					.parseInt(txtNumRecent.getText());

		} catch (NumberFormatException e) {

			return false;

		}
		return true;
	}

	@Override
	public void clearForm() {
		tblViewerEpisodeAttributes.setAllChecked(false);
		txtNumOldest.setText("1");
		txtNumRecent.setText("1");
		rdBtnOldest.setSelection(false);
		rdBtnRecent.setSelection(true);
	}

}
