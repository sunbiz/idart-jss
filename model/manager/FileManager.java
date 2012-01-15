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

package model.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import model.manager.exports.ReportObject;
import model.manager.exports.xml.ReportObjectXMLDecoder;
import model.manager.exports.xml.ReportObjectXMLEncoder;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;

/**
 * 
 * @author Rashid
 * 
 */
public class FileManager {

	private static Logger log = Logger.getLogger(FileManager.class);

	public static boolean saveTemplate(ReportObject obj) {

		Date now = new Date();
		Calendar C = Calendar.getInstance();
		C.setTime(now);

		String fileName = C.getTimeInMillis() + ".xml";
		return saveTemplate(obj, fileName);
	}

	public static boolean saveTemplate(ReportObject obj, String fileName) {

		Date now = new Date();
		Calendar C = Calendar.getInstance();
		C.setTime(now);

		ReportObjectXMLEncoder enc = new ReportObjectXMLEncoder(obj);
		String xml = enc.toXmlString();
		
		File dir = new File(iDartProperties.exportDir);
		if(!dir.exists()){
			if (!dir.mkdirs()){
				log.error("Error creating directory: " + iDartProperties.exportDir);
				return false;
			}
		}
		String pathname = iDartProperties.exportDir + File.separator + fileName;
		try {
			FileUtils.writeStringToFile(new File(pathname), xml);
		} catch (IOException E) {
			log.error("Error saving file: " + fileName);
			return false;
		}

		return true;
	}

	/**
	 * This method opens an xml file and decodes it into a
	 * DataExportReportObject
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static ReportObject readXMLfile(String filename) throws IOException {

		String fileAndPath = "";
		fileAndPath = fileAndPath + iDartProperties.exportDir
				+ File.separator + filename;

		String filecontents_XML = FileUtils.readFileToString(new File(
				fileAndPath));

		ReportObjectXMLDecoder rod = new ReportObjectXMLDecoder(
				filecontents_XML);
		ReportObject bre = rod.toBaseReportObject();

		return bre;

	}

	/**
	 * Method deleteFile.
	 * 
	 * @param fileName
	 *            String
	 * @param path
	 *            String
	 * @return boolean
	 */
	public static boolean deleteFile(String fileName, String path) {

		try {
			// Construct a File object for the file to be deleted.
			File target = new File(path + fileName);
			if (!target.exists()) {
				log.error("File " + fileName + " not present to begin with!");
				return false;
			}
			target.delete();

		} catch (SecurityException e) {
			log.error("Unable to delete " + fileName, e);
			return false;
		}

		return true;
	}

	/**
	 * Method to find all xml files in a given directory.
	 * 
	 * @param rootPath
	 * @return List<String>
	 */
	public static String[] getDataExportObjects(String rootPath) {

		File dir = new File(rootPath);

		String[] children = dir.list();

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir1, String name) {
				return name.endsWith(".xml");
			}
		};
		children = dir.list(filter);
		return children;
	}

}
