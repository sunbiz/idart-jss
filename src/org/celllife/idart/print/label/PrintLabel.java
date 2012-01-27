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

package org.celllife.idart.print.label;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;

import org.celllife.idart.commonobjects.iDartProperties;

/**
 */
public class PrintLabel {

	/**
	 * Label width for label 750mm x 500mm in 1/72 of an inch (Adjusted for
	 * printing optimisation)
	 */
	public static int NEW_LABEL_WIDTH = 216;

	public static int EPL2_LABEL_WIDTH = 600;

	/**
	 * This method iterates the list of labels that need to be printed, and
	 * systematically calls the print() method of each of the labels.
	 * 
	 * @param labelsToPrint
	 * @param service
	 *            PrintService
	 */

	public static void printLinuxZebraLabels(List<DefaultLabel> labelsToPrint,
			PrintService service) {

		try {
			for (int i = 0; i < labelsToPrint.size(); i++) {
				File tmpFile = new File("tmp.raw");
				FileWriter fWriter = new FileWriter(tmpFile);

				Vector<String> commands = labelsToPrint.get(i)
				.getEPL2Commands();
				System.out.println(commands);
				for (int a = 0; a < commands.size(); a++) {
					fWriter.write(commands.elementAt(a));
				}
				fWriter.close();

				FileInputStream fs = new FileInputStream("tmp.raw");

				DocAttributeSet das = new HashDocAttributeSet();
				DocPrintJob job = service.createPrintJob();
				Doc ptDoc = new SimpleDoc(fs, DocFlavor.INPUT_STREAM.AUTOSENSE,
						das);

				try {
					job.print(ptDoc, null);
				} catch (PrintException e) {
					e.printStackTrace();
				}
				// tmpFile.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method printWindowsLabels.
	 * 
	 * @param labelsToPrint
	 *            List<Printable>
	 */
	public static void printWindowsLabels(List<Printable> labelsToPrint) {
		PageFormat pf = new PageFormat();
		Paper paper = new Paper();

		if (iDartProperties.labelType.equals(iDartProperties.LabelType.EKAPA)) {
			paper.setSize(285, 135); // eKapa labels
		} else {
			paper.setSize(216, 135); // normal iDART labels
		}
		paper.setImageableArea(0.0, 0.0, paper.getWidth(), paper.getHeight());
		pf.setPaper(paper);
		pf.setOrientation(PageFormat.PORTRAIT);

		Book book = new Book();

		for (int i = 0; i < labelsToPrint.size(); i++) {

			Printable p = labelsToPrint.get(i);
			book.append(p, pf);

		}

		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPageable(book);

		boolean doPrint = job.printDialog();
		if (doPrint) {
			try {

				job.print();

			} catch (PrinterException e) {
				e.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	/**
	 * Method printiDARTLabels.
	 * 
	 * @param labelsToPrint
	 *            List<Object>
	 */
	public static void printiDARTLabels(List<Object> labelsToPrint)
	throws Exception {

		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")) {

			List<Printable> printableLabelList = new ArrayList<Printable>();
			for (Object o : labelsToPrint) {
				printableLabelList.add((Printable) o);
			}

			printWindowsLabels(printableLabelList);

		}

		else if (!System.getProperty("os.name").toUpperCase().startsWith(
		"LINUX")) {
			PrintService[] allServices = PrintServiceLookup
			.lookupPrintServices(null, null);

			/* rashid temp code */
			PrintService serv = null;
			for (PrintService s : allServices) {
				if (s.getName().contains("Zebra")) {
					serv = s;
				}
			}
			if (serv != null) {
				List<DefaultLabel> defaultLabelList = new ArrayList<DefaultLabel>();
				for (Object o : labelsToPrint) {
					defaultLabelList.add((DefaultLabel) o);
				}
				printLinuxZebraLabels(defaultLabelList, serv);
				return;
			}
			/* rashid temp code */

			boolean foundZebra = false;

			for (int j = 0; j < allServices.length; j++) {
				PrintService service = allServices[j];

				if (service != null) { // Is this printer a Zebra?
					try {
						Runtime rt = Runtime.getRuntime(); // search for this
						// printer in list
						// from lpstat

						Process p = rt.exec(new String[] {
								"/bin/sh",
								"-c",
								"lpstat -v| grep '" + service.getName()
								+ "' | grep Zebra" });
						InputStream inputstream = p.getInputStream();
						InputStreamReader inputstreamreader = new InputStreamReader(
								inputstream);
						BufferedReader bufferedReader = new BufferedReader(
								inputstreamreader);
						// read the output
						String line = bufferedReader.readLine();
						bufferedReader.close();
						if (line != null) {
							foundZebra = true;
							List<DefaultLabel> defaultLabelList = new ArrayList<DefaultLabel>();
							for (Object o : labelsToPrint) {
								defaultLabelList.add((DefaultLabel) o);
							}

							printLinuxZebraLabels(defaultLabelList, service);
							return;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (!foundZebra) {
				List<Printable> printableLabelList = new ArrayList<Printable>();
				for (Object o : labelsToPrint) {
					printableLabelList.add((Printable) o);
				}

				printWindowsLabels(printableLabelList);
			}

		}
	}

}
