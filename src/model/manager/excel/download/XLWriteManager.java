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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

/**
 */

public class XLWriteManager {

	protected WritableWorkbook write = null;

	protected ByteArrayOutputStream bos = new ByteArrayOutputStream();

	private Logger log = null;

	protected WritableSheet writableSheet = null;

	protected Label label = null;

	protected Number number = null;

	protected DateTime dateTime = null;

	protected int sheetNumber = 0;

	protected WritableCellFormat headingFormatTextCentered = null;

	protected WritableCellFormat headingFormatTextLeft = null;

	protected WritableCellFormat headingFormatTextCenteredThickBorder = null;

	protected WritableCellFormat titleFormat = null;

	protected WritableCellFormat cellFormat = null;

	protected WritableCellFormat stringFormat = null;

	protected WritableCellFormat dateFormat = null;

	protected WritableCellFormat dateFormat2 = null;

	private int rowCount = 0;

	public static final int ALIGN_RIGHT = 0;

	private File errorFile;

	/**
	 * Default Constructor
	 */
	protected XLWriteManager() {
		super();
		initialise();
	}

	/**
	 * Constructor for XLWriteManager.
	 * 
	 * @param sheetName
	 *            String
	 */
	public XLWriteManager(String sheetName) {
		super();
		try {
			errorFile = new File("idart-" + System.currentTimeMillis() + "-exportErrors.xls");
			write = Workbook.createWorkbook(errorFile);
			writableSheet = this.write.createSheet(sheetName, sheetNumber);
			sheetNumber++;
		} catch (IOException e) {
			log.error("Could not open Excel file", e);
		}
		initialise();
	}

	/**
	 * Constructor for XLWriteManager.
	 * 
	 * @param sheetName
	 *            String
	 * @param path
	 *            String
	 */
	public XLWriteManager(String sheetName, String path) {
		super();
		try {
			write = Workbook.createWorkbook(new File(path));

			writableSheet = this.write.createSheet(sheetName, sheetNumber);
			sheetNumber++;
		} catch (IOException e) {
			log.error("Could not open Excel file", e);
		}
		initialise();
	}

	private void initialise() {
		log = Logger.getLogger(XLWriteManager.class);
		/*
		 * Initialise Heading Format and title fonts
		 */
		WritableFont arial10BoldCenter = new WritableFont(WritableFont.ARIAL,
				10, WritableFont.BOLD, false);

		WritableFont arial16BoldCenter = new WritableFont(WritableFont.ARIAL,
				16, WritableFont.BOLD, false);

		headingFormatTextCentered = new WritableCellFormat(arial10BoldCenter);
		headingFormatTextLeft = new WritableCellFormat(arial10BoldCenter);
		headingFormatTextCenteredThickBorder = new WritableCellFormat(
				arial10BoldCenter);
		titleFormat = new WritableCellFormat(arial16BoldCenter);
		try {
			headingFormatTextCentered.setAlignment(Alignment.CENTRE);
			headingFormatTextCentered.setWrap(true);
			headingFormatTextCentered
					.setVerticalAlignment(VerticalAlignment.CENTRE);
			headingFormatTextCentered.setBorder(Border.ALL,
					BorderLineStyle.THIN);

			headingFormatTextLeft.setAlignment(Alignment.LEFT);
			headingFormatTextLeft.setWrap(true);
			headingFormatTextLeft
					.setVerticalAlignment(VerticalAlignment.CENTRE);
			headingFormatTextLeft.setBorder(Border.ALL, BorderLineStyle.THIN);

			headingFormatTextCenteredThickBorder.setAlignment(Alignment.CENTRE);
			headingFormatTextCenteredThickBorder.setWrap(true);
			headingFormatTextCenteredThickBorder
					.setVerticalAlignment(VerticalAlignment.CENTRE);
			headingFormatTextCenteredThickBorder.setBorder(Border.ALL,
					BorderLineStyle.THICK);

			titleFormat.setAlignment(Alignment.LEFT);
			titleFormat.setWrap(true);
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			titleFormat.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
		} catch (WriteException e) {
			log.info("Can not write Bold Format!!!");

		}

		/*
		 * Initialise Date Format
		 */
		DateFormat customDateFormat = new DateFormat("dd MMMM yyyy");
		dateFormat = new WritableCellFormat(customDateFormat);
		try {
			dateFormat.setAlignment(Alignment.GENERAL);
			dateFormat.setWrap(false);
			dateFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			dateFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		} catch (WriteException e) {
			log.error(e);
		}

		/*
		 * @author Rashid Initialise Date Format2 (includes time)
		 */
		DateFormat customDateFormat2 = new DateFormat("dd MMMM yyyy HH:mm");
		dateFormat2 = new WritableCellFormat(customDateFormat2);
		try {
			dateFormat2.setAlignment(Alignment.GENERAL);
			dateFormat2.setWrap(false);
			dateFormat2.setVerticalAlignment(VerticalAlignment.CENTRE);
			dateFormat2.setBorder(Border.ALL, BorderLineStyle.THIN);
		} catch (WriteException e) {
			log.error(e);
		}

		/*
		 * Initialise Cell Format
		 */
		WritableFont arial10NormalLeft = new WritableFont(WritableFont.ARIAL,
				10, WritableFont.NO_BOLD, false);
		cellFormat = new WritableCellFormat(arial10NormalLeft);
		try {
			cellFormat.setAlignment(Alignment.GENERAL);
			cellFormat.setWrap(false);
			cellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		} catch (WriteException e) {
			log.error(e);
		}

		/*
		 * Initialise String Format Used specifically to set the String in the
		 * right part of the cell EG. putting Strings like R100 in the right
		 * part of the cell to align them with numbers
		 */
		WritableFont arial10NormalRight = new WritableFont(WritableFont.ARIAL,
				10, WritableFont.NO_BOLD, false);
		stringFormat = new WritableCellFormat(arial10NormalRight);
		try {
			stringFormat.setAlignment(Alignment.RIGHT);
			stringFormat.setWrap(false);
			stringFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			stringFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
		} catch (WriteException e) {
			log.error(e);
		}
	}

	/**
	 * Method newSheet.
	 * 
	 * @param newSheetName
	 *            String
	 */
	public void newSheet(String newSheetName) {
		writableSheet = this.write.createSheet(newSheetName, sheetNumber);
		sheetNumber++;
	}

	/**
	 * closes an excel file
	 * 
	 * @return byte[]
	 */
	public byte[] closeFile() {
		try {

			this.write.write();
			this.write.close();
			return bos.toByteArray();
		} catch (IOException e) {
			log.error("IO Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.error("Could not write File: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Sets the background for a cell
	 * 
	 * @param column
	 * @param row
	 */
	public void highlightCell(int column, int row) {

		try {
			
			WritableCell c = writableSheet.getWritableCell(column, row);
			CellFormat oldFormat = c.getCellFormat();
			
			WritableCellFormat newFormat;
			if(oldFormat != null) {
				newFormat = new WritableCellFormat(oldFormat);
			}
			else {
				newFormat = new WritableCellFormat();
			}
			newFormat.setBackground(Colour.YELLOW);
			
			c.setCellFormat(newFormat);

		} catch (WriteException we) {
			we.printStackTrace();
		}
	}
	
	/**
	 * Sets the background for a cell
	 * 
	 * @param column
	 * @param row
	 */
	public void boldCell(int column, int row) {

		try {
			
			WritableCell c = writableSheet.getWritableCell(column, row);
			CellFormat oldFormat = c.getCellFormat();
			WritableCellFormat newFormat = new WritableCellFormat(oldFormat);
			
			WritableFont font = (WritableFont) oldFormat.getFont();
			font.setBoldStyle(WritableFont.BOLD);
			newFormat.setFont(font);
			
			c.setCellFormat(newFormat);
		} catch (WriteException we) {
			we.printStackTrace();
		}
	}

	/**
	 * Method writeCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            String
	 */
	public void writeCell(int x, int y, String value) {
		try {
			label = new Label(x, y, value, cellFormat);
			writableSheet.addCell(label);
		} catch (RowsExceededException e) {
			log.info("Rows Exceeded Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            String
	 * @param justify
	 *            int
	 */
	public void writeCell(int x, int y, String value, int justify) {
		try {
			label = new Label(x, y, value, stringFormat);
			writableSheet.addCell(label);
		} catch (RowsExceededException e) {
			log.info("Rows Exceeded Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            int
	 */
	public void writeCell(int x, int y, int value) {
		Double d = new Double(value);
		try {
			number = new Number(x, y, d.doubleValue(), cellFormat);
			writableSheet.addCell(number);
		} catch (RowsExceededException e) {
			log.info("Rows Exceeded Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            double
	 */
	public void writeCell(int x, int y, double value) {
		try {
			number = new Number(x, y, value, cellFormat);
			writableSheet.addCell(number);
		} catch (RowsExceededException e) {
			log.info("Rows Exceeded Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            Date
	 */
	public void writeCell(int x, int y, Date value) {
		try {
			dateTime = new DateTime(x, y, value, dateFormat);
			writableSheet.addCell(dateTime);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * This method writes a date including time to the spreadsheet
	 * 
	 * @param x
	 * @param y
	 * @param value
	 * 
	 * @param i
	 *            int
	 */
	public void writeCell(int x, int y, Date value, int i) {
		try {
			dateTime = new DateTime(x, y, value, dateFormat2);
			writableSheet.addCell(dateTime);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Default Write Cell Value
	 * 
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @param object
	 *            Object to insert: Either String, Integer, Double, Date,
	 *            Timestamp, Boolean or null
	 */
	public void writeCell(int x, int y, Object object) {

		if (object == null) {
			this.writeCell(x, y, "");
		} else if (object.getClass() == String.class) {
			String s = (String) object;
			this.writeCell(x, y, s);
		} else if (object.getClass() == Integer.class) {
			Integer integer = (Integer) object;
			this.writeCell(x, y, integer.intValue());
		} else if (object.getClass() == Double.class) {
			Double doubleObj = (Double) object;
			this.writeCell(x, y, doubleObj.intValue());
		} else if (object.getClass() == Date.class) {
			Date date = (Date) object;
			this.writeCell(x, y, date);
		} else if (object.getClass() == Timestamp.class) {
			Timestamp ts = (Timestamp) object;
			Date d = (Date) ts.clone();
			this.writeCell(x, y, d);
		} else if (object.getClass() == Boolean.class) {
			Boolean bool = (Boolean) object;
			String toWrite = null;
			if (bool.booleanValue()) {
				toWrite = "y";
			} else {
				toWrite = "n";
			}
			this.writeCell(x, y, toWrite);
		} else if (object.getClass() == Character.class) {
			Character character = (Character) object;
			this.writeCell(x, y, "" + character);
		} else if (object.getClass() == Formula.class) {
			Formula formula = (Formula)object;
			formula.setCellFormat(cellFormat);
			this.writeCell(formula);
		} 

	}
	
	public void writeCell(WritableCell cell) {
		try {
			writableSheet.addCell(cell);
		} catch (RowsExceededException e) {
			log.info("Write Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}	
	}

	public void writeCell(Formula formula) {
		try {
			writableSheet.addCell(formula);
		} catch (RowsExceededException e) {
			log.info("Write Exception: " + e.getMessage());
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}		
	}

	/**
	 * Method writeHeadingCell.
	 * 
	 * @param x
	 *            int
	 * @param value
	 *            String
	 */
	public void writeHeadingCell(int x, String value) {
		try {
			label = new Label(x, 0, value, headingFormatTextCentered);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeHeadingCell.
	 * 
	 * @param x
	 *            int
	 * @param value
	 *            String
	 * @param columnWidth
	 *            int
	 */
	public void writeHeadingCell(int x, String value, int columnWidth) {
		try {
			writableSheet.setColumnView(x, columnWidth);

			label = new Label(x, 0, value, headingFormatTextCentered);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param value
	 */
	public void writeHeadingCell(int x, int y, String value) {
		try {

			label = new Label(x, y, value, headingFormatTextCentered);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	public void writeTitleCell(int x, int y, String value) {
		try {

			label = new Label(x, y, value, titleFormat);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeHeadingCell.
	 * 
	 * @param x
	 *            int
	 * @param value
	 *            String
	 * @param columnWidth
	 *            int
	 */
	public void writeHeadingCell(int x, int y, String value, int columnWidth) {
		try {
			writableSheet.setColumnView(x, columnWidth);

			label = new Label(x, y, value, headingFormatTextCentered);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeSubHeadingCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            String
	 * @param columnWidth
	 *            int
	 */
	public void writeSubHeadingCell(int x, int y, String value, int columnWidth) {
		try {
			writableSheet.setColumnView(x, columnWidth);

			label = new Label(x, y, value, headingFormatTextCentered);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method writeSubHeadingCell.
	 * 
	 * @param x
	 *            int
	 * @param y
	 *            int
	 * @param value
	 *            String
	 */
	public void writeSubHeadingCellTextCentererd(int x, int y, String value) {
		try {

			label = new Label(x, y, value, headingFormatTextCentered);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	public void writeSubHeadingCellTextLeft(int x, int y, String value) {
		try {

			label = new Label(x, y, value, headingFormatTextLeft);
			writableSheet.addCell(label);
		} catch (WriteException e) {
			log.info("Write Exception: " + e.getMessage());
		}
	}

	/**
	 * Method setHeadingFormat.
	 * 
	 * @param headingFormat
	 *            WritableCellFormat
	 */
	public void setHeadingFormat(WritableCellFormat headingFormat) {
		this.headingFormatTextCentered = headingFormat;
	}

	/**
	 * Method getRowCount.
	 * 
	 * @return int
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Method setRowCount.
	 * 
	 * @param rowCount
	 *            int
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public void incrRowCount() {
		rowCount++;
	}

	/**
	 * Method mergeCells.
	 * 
	 * @param col_start
	 *            int
	 * @param row_start
	 *            int
	 * @param col_end
	 *            int
	 * @param row_end
	 *            int
	 */
	public void mergeCells(int col_start, int row_start, int col_end,
			int row_end) {
		try {
			writableSheet.mergeCells(col_start, row_start, col_end, row_end);
		} catch (WriteException we) {
			log.info("Write Exception: " + we.getMessage());
		}
	}

	/**
	 * Resize entire column
	 * 
	 * @param column
	 * @param width
	 */
	public void resizeColumn(int column, int width) {
		writableSheet.setColumnView(column, width);
	}

	/**
	 * Resize entire row
	 * 
	 * @param row
	 * @param height
	 */
	public void resizeRow(int row, int height) {
		try {
			writableSheet.setRowView(row, height);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		}
	}

	public void writeCellCentered(int columnIndex, int rowIndex, Object data) {
		try {
			WritableCellFormat newFormat = new WritableCellFormat(cellFormat);
			newFormat.setAlignment(Alignment.CENTRE);
			cellFormat = newFormat;
		} catch (WriteException e) {
			e.printStackTrace();
		}

		writeCell(columnIndex, rowIndex, data);

	}
	
	public File getErrorFile() {
		return errorFile;
	}
}
