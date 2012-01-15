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

package org.celllife.idart.misc.task;

import java.io.File;

import model.manager.excel.conversion.exceptions.ReportException;
import model.manager.importData.ImportPatients;

import org.eclipse.core.runtime.IProgressMonitor;

public class Import implements IdartTask {

	private String sheet;
	private String fileName;
	private ImportPatients importer;

	@Override
	public String getHelpText() {
		String help = "iDART Import help\n";
		help += "=================\n";
		help += "To use the iDART import you must "
			+ "pass in two arguments as follows:\n";
		help += "	argument1: fileName		"
			+ "This is the name of the Excel file you wish to import.\n";
		help += "	argument2: sheetName	"
			+ "This is the name of the Excel sheet within your file"
			+ " which you wish to import.\n\n";
		help += "An example would be:\n";
		help += "	go.bat idart.xls import (Windows)\n";
		help += "	./go.sh idart.xls import (Linux)\n";
		return help;

	}

	@Override
	public boolean init(String[] args) {
		if (args.length == 2) {
			fileName = args[0];
			sheet = args[1];
			if (("".equals(fileName)) || ("".equals(sheet)))
				return false;

			return true;
		} else
			return false;

	}

	@Override
	public void run(IProgressMonitor monitor) throws TaskException {
		importer = new ImportPatients();
		try {
			importer.importData(fileName, sheet, monitor);
		} catch (ReportException e) {
			throw new TaskException(e);
		}
		
	}

	@Override
	public String getDescription() {
		return "Import demographics into iDART.";
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}
	
	public int getErrorCount(){
		return importer.getImportSheet().getErrorCount();
	}
	
	public File getErrorFile(){
		return importer.getImportSheet().getErrorFile();
	}
}