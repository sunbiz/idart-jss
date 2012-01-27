/*
 * {ARK CABA - System for managing the feeding of school kids }
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
 */

// Created 15/03/2006
package model.manager.excel.download;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.read.biff.PasswordException;
import model.manager.excel.conversion.exceptions.ReportException;

import org.apache.log4j.Logger;
import org.celllife.idart.misc.iDARTUtil;

/**
 */

public class XLReadManager {

	private Workbook read = null;

	private Logger log = null;

	private Sheet readableSheet = null;
	
	/**
	 * Default Constructor
	 */
	public XLReadManager() {
		super();
		log = Logger.getLogger(XLReadManager.class);
	}

	/**
	 * Constructor for XLReadManager.
	 * @param stream byte[]
	 * @param sheetName String
	 * @throws ReportException 
	 */
	public XLReadManager(File file, String sheetName) throws ReportException {
		super();
		// Create a workbook
		try {
			read = Workbook.getWorkbook(file);
			readableSheet = this.read.getSheet(sheetName);
		} catch (PasswordException pe) {
			log.error("Error opening Excel file: file is password protected", pe);
			throw new ReportException(pe);
		} catch (BiffException be) {
			log.error("Error opening Excel file: Not a valid excel File", be);
			throw new ReportException(be);
		} catch (IOException e) {
			log.error("Error opening Excel file.", e);
			throw new ReportException(e);
		}
	}

	/**
	 * Method setSheet.
	 * @param name String
	 */
	public void setSheet(String name) {
		readableSheet = this.read.getSheet(name);
	}

	/**
	 * Method setSheet.
	 * @param pageNumber int
	 */
	public void setSheet(int pageNumber) {
		readableSheet = this.read.getSheet(pageNumber);
	}

	/**
	 * reads a cell of data from the current sheet of the spreadsheet
	 * 
	 * @param columnNumber
	 *            column number of the cell to be read
	 * @param rowNumber
	 *            row number of the cell to be read
	 * 
	 * @return String
	 */
	public String readCell(int columnNumber, int rowNumber, boolean formatDates) {
		Cell currentCell = readableSheet.getCell(columnNumber, rowNumber);
		if (formatDates && currentCell.getType() == CellType.DATE) {
			DateCell dc = (DateCell) currentCell;
			Date date = dc.getDate();
			return iDARTUtil.format(date);
		}

		return currentCell.getContents().trim();
	}
	
	/**
	 * Method getReadableSheet.
	 * @return Sheet
	 */
	public Sheet getReadableSheet() {
		return readableSheet;
	}

}
