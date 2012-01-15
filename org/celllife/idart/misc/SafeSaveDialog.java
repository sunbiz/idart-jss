/**
 * This class was written by Rob Warner and Robert Harris for the book
 * The Definitive guide to SWT and JFace
 */

package org.celllife.idart.misc;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * This class provides a facade for the "save" FileDialog class. If the selected
 * file already exists, the user is asked to confirm before overwriting.
 */
public class SafeSaveDialog {
	
	public static enum FileType {
		CSV(new String[] {"Comma Separated Values Files (*.csv)", "All Files (*.*)" },
				new String[] { "*.csv", "*.*" }),
		EXCEL(new String[] {"Microsoft Excel Spreadsheet Files (*.xls)", "All Files (*.*)" },
				new String[] { "*.xls", "*.*" });
		
		private String[] filterNames;
		private String[] filterExtensions;
		
		private FileType(String[] filterNames, String[] filterExtensions) {
			this.filterNames = filterNames;
			this.filterExtensions = filterExtensions;
		}
		
		public String[] getFilterNames() {
			return filterNames;
		}
		
		public String[] getFilterExtensions() {
			return filterExtensions;
		}
	}
	
	// The wrapped FileDialog
	private FileDialog dlg;

	/**
	 * SafeSaveDialog constructor
	 * 
	 * @param shell
	 *            the parent shell
	 */
	public SafeSaveDialog(Shell shell, FileType type) {
		dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setFilterExtensions(type.getFilterExtensions());
		dlg.setFilterNames(type.getFilterNames());
	}

	/**
	 * Method open.
	 * @return String
	 */
	public String open() {
		// We store the selected file name in fileName
		String fileName = null;

		// The user has finished when one of the
		// following happens:
		// 1) The user dismisses the dialog by pressing Cancel
		// 2) The selected file name does not exist
		// 3) The user agrees to overwrite existing file
		boolean done = false;

		while (!done) {
			// Open the File Dialog
			fileName = dlg.open();
			if (fileName == null) {
				// User has cancelled, so quit and return
				done = true;
			} else {
				// User has selected a file; see if it already exists
				File file = new File(fileName);
				if (file.exists()) {
					// The file already exists; asks for confirmation
					MessageBox mb = new MessageBox(dlg.getParent(),
							SWT.ICON_WARNING | SWT.YES | SWT.NO);

					// We really should read this string from a
					// resource bundle
					mb.setText(fileName
							+ " already exists");
					mb.setMessage(fileName
							+ " already exists. Do you want to replace it?");

					// If they click Yes, we're done and we drop out. If
					// they click No, we
					// ResourceUtils.getColor(iDartColor.RED)isplay the
					// File Dialog
					done = mb.open() == SWT.YES;
				} else {
					// File does not exist, so drop out
					done = true;
				}
				
			}
		}
		return fileName;
	}

	/**
	 * Method getFileName.
	 * @return String
	 */
	public String getFileName() {
		return dlg.getFileName();
	}

	/**
	 * Method getFileNames.
	 * @return String[]
	 */
	public String[] getFileNames() {
		return dlg.getFileNames();
	}

	/**
	 * Method getFilterExtensions.
	 * @return String[]
	 */
	public String[] getFilterExtensions() {
		return dlg.getFilterExtensions();
	}

	/**
	 * Method getFilterNames.
	 * @return String[]
	 */
	public String[] getFilterNames() {
		return dlg.getFilterNames();
	}

	/**
	 * Method getFilterPath.
	 * @return String
	 */
	public String getFilterPath() {
		return dlg.getFilterPath();
	}

	/**
	 * Method setFileName.
	 * @param string String
	 */
	public void setFileName(String string) {
		dlg.setFileName(string);
	}

	/**
	 * Method setFilterExtensions.
	 * @param extensions String[]
	 */
	public void setFilterExtensions(String[] extensions) {
		dlg.setFilterExtensions(extensions);
	}

	/**
	 * Method setFilterNames.
	 * @param names String[]
	 */
	public void setFilterNames(String[] names) {
		dlg.setFilterNames(names);
	}

	/**
	 * Method setFilterPath.
	 * @param string String
	 */
	public void setFilterPath(String string) {
		dlg.setFilterPath(string);
	}

	/**
	 * Method getParent.
	 * @return Shell
	 */
	public Shell getParent() {
		return dlg.getParent();
	}

	/**
	 * Method getStyle.
	 * @return int
	 */
	public int getStyle() {
		return dlg.getStyle();
	}

	/**
	 * Method getText.
	 * @return String
	 */
	public String getText() {
		return dlg.getText();
	}

	/**
	 * Method setText.
	 * @param string String
	 */
	public void setText(String string) {
		dlg.setText(string);
	}

}
