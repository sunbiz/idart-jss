package org.celllife.idart.misc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class iDARTErrorDialog extends org.eclipse.jface.dialogs.ErrorDialog {
	
	private List list;

	private Clipboard clipboard;

	private IStatus status;

	public iDARTErrorDialog(Shell parentShell, String dialogTitle, String message,
			IStatus status, int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
		this.status = status;
	}
	
	/**
	 * Create this dialog's drop-down list component.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the drop-down list component
	 */
	@Override
	protected List createDropDownList(Composite parent) {
		list = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
		data.heightHint = list.getItemHeight() * 10;
		data.horizontalSpan = 2;
		list.setLayoutData(data);
		list.setFont(parent.getFont());
		list.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (clipboard != null) {
					clipboard.dispose();
				}
			}
		});
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				list.selectAll();
				super.widgetSelected(e);
			}
		});
		list.removeAll();
		populateList(status.getException());
		createCopyAction(parent);
		return list;
	}
	
	private void createCopyAction(final Composite parent) {
		Menu menu = new Menu(parent.getShell(), SWT.POP_UP);
		MenuItem copyAction = new MenuItem(menu, SWT.PUSH);
		copyAction.setText("&Copy"); //$NON-NLS-1$
		copyAction.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clipboard = new Clipboard(parent.getDisplay());
				clipboard.setContents(new Object[] { prepareCopyString() },
						new Transfer[] { TextTransfer.getInstance() });
				super.widgetSelected(e);
			}
		});
		list.setMenu(menu);
	}

	private String prepareCopyString() {
		if (list == null || list.isDisposed()) {
			return ""; //$NON-NLS-1$
		}
		StringBuffer sb = new StringBuffer();
		String newLine = System.getProperty("line.separator"); //$NON-NLS-1$
		for (int i = 0; i < list.getItemCount(); i++) {
			sb.append(list.getItem(i));
			sb.append(newLine);
		}
		return sb.toString();
	}

	private void populateList(Throwable t) {
		if (t == null) {
			list.add("No stack trace");
			return;
		}
		list.add(t.toString());
		StackTraceElement[] ste = t.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			list.add(ste[i].toString());
		}
		if (t.getCause() != null) {
			list.add("Caused by:");
			populateList(t.getCause());
		}
	}

}
