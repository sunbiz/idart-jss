package org.celllife.idart.print.label;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class printtest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PrintService[] allServices = PrintServiceLookup.lookupPrintServices(
				null, null);
		for (PrintService s : allServices) {
			System.out.println(s.getName());
		}

	}

}
