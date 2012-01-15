package org.celllife.idart.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 */
public class InsertLicenseText {

    String record = null;

    DataInputStream dis = null;

    int recCount = 0;

    /**
     * Method main.
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) {

	InsertLicenseText it = new InsertLicenseText();
	String path = args[0];
	// System.out.println("path: " + path);
	// String path = "D://development//workspace//TestGPL//FilesToChange";

	File folder = new File(path);
	if (path.endsWith("java")) {
	    it.readMyFile(folder);
	}
	// File[] files = folder.listFiles();
	// for (int i = 0; i < files.length; i++) {
	// if (files[i].getName().endsWith("java")) {
	// it.readMyFile(files[i]);
	// }
	// if (files[i].isDirectory()) {
	// File[] children = files[i].listFiles();
	// for (int j = 0; j < children.length; j++) {
	// it.readMyFile(children[j]);
	// }
	// }
	// }
    }

    /**
     * Method readMyFile.
     * 
     * @param fileToRead
     *            File
     */
    void readMyFile(File fileToRead) {

	String fileContents = "";
	String GPL_License = "/*\n"
		+ " * iDART: The Intelligent Dispensing of Antiretroviral Treatment\n"
		+ " * Copyright (C) 2006 Cell-Life \n"
		+ " * \n"
		+ " * This program is free software; you can redistribute it and/or modify it \n"
		+ " * under the terms of the GNU General Public License version 2 as published by \n"
		+ " * the Free Software Foundation. \n"
		+ " * \n"
		+ " * This program is distributed in the hope that it will be useful, but WITHOUT  \n"
		+ " * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  \n"
		+ " * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version  \n"
		+ " * 2 for more details. \n"
		+ " * \n"
		+ " * You should have received a copy of the GNU General Public License version 2 \n"
		+ " * along with this program; if not, write to the Free Software Foundation, \n"
		+ " * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA \n"
		+ " * \n" + " */\n\n";

	try {
	    BufferedReader in = new BufferedReader(new FileReader("infilename"));
	    String str;
	    while ((str = in.readLine()) != null) {
		fileContents = fileContents.concat(str + "\n");
	    }
	    in.close();

	    FileOutputStream out = new FileOutputStream(fileToRead);
	    PrintStream p = new PrintStream(out);
	    p.println(GPL_License + fileContents);
	    p.close();
	} catch (IOException e) {
	}

    }
}
