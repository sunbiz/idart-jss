/**
 * 
 */
package org.celllife.idart.gui.reprintLabels.tableViewerUtils;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rashid
 * 
 */
public class LabelEditingSupport extends EditingSupport {

	private CellEditor editor;
	private final int column;

	public LabelEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		switch (column) {
		case 1:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			((Text) editor.getControl()).addVerifyListener(

					new VerifyListener() {
						@Override
						public void verifyText(VerifyEvent e) {
							e.doit = e.text.matches("[0-9]*");
						}
					});

		}
		this.column = column;
	}

	@Override
	protected boolean canEdit(Object element) {
		ReprintLabelsViewModel model = (ReprintLabelsViewModel) element;
		if (column == 1 && model.getNum() != null)
			return true;
		return false;
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		ReprintLabelsViewModel model = (ReprintLabelsViewModel) element;

		if (column == 1 && model.getNum() != null)
			return model.getNum().toString();

		return null;

	}

	@Override
	protected void setValue(Object element, Object value) {
		ReprintLabelsViewModel model = (ReprintLabelsViewModel) element;

		if (column == 1) {
			String stringVal = (String) value;
			Integer val = stringVal.isEmpty() ? 0 : Integer.valueOf(stringVal);
			model.setNum(val);
			getViewer().update(element, null);
		}
	}

}
