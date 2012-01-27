package org.celllife.idart.print.label;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This is the default printer class to print label printers.
 * <p>
 * The printer is called with a list of labels (or one label). The driver then
 * spawns a new thread to print all the labels and leaves the main program to
 * continue.
 * <p>
 * It still needs a lot of work. Printer detection is not implemented.
 * 
 */
public class EkapaPrinter implements Runnable {

	private static Logger log = Logger.getLogger(EkapaPrinter.class.getName());

	private List<Printable> labels = null;

	/**
	 * Windows printing routine
	 * 
	 */
	private void printWindows() {
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = new PageFormat();

		// set the whole paper as imageable
		// and make the label define a border
		// NOTE we can't set the page format in the label
		Paper paper = new Paper();
		paper.setSize(285, 135); // 1000 x 480 mm
		paper.setImageableArea(0.0, 0.0, paper.getWidth(), paper.getHeight());
		pf.setPaper(paper);

		Book book = new Book();
		for (Printable p : labels)
			book.append(p, pf);
		job.setPageable(book);
		log.info("Printing " + book.getNumberOfPages() + " labels");

		boolean doPrint = job.printDialog();
		if (doPrint) {
			try {

				job.print();

			} catch (PrinterException e) {

				log.error("Printing error: " + e);

			}

		}
	}

	/**
	 * Print multiple labels.
	 * 
	 * @param labels List<Printable>
	 */
	public void print(List<Printable> labelsToPrint) {
		this.labels = labelsToPrint;
		Thread t = new Thread(this, "iDART Printing");
		t.start();
	}

	/**
	 * The new thread for the printer. It chooses the operating system and
	 * prints labels using the specified driver.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		printWindows();
	}

	/**
	 * Print a single label
	 * 
	 * @param label Printable
	 */
	public void print(Printable label) {
		List<Printable> labelsToPrint = new ArrayList<Printable>();
		labelsToPrint.add(label);
		print(labelsToPrint);
	}
}
