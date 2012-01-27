package org.celllife.idart.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

/**
 */
public class PatientBarcodeParser {

	private static Pattern barcodeRegexPattern;
	private static Matcher matcher;
	private static Logger log = null;

	/**
	 * Method initialisePatientBarcodeParser.
	 * @param regex String
	 */
	public static void initialisePatientBarcodeParser(String regex) {
		log = Logger.getLogger(PatientBarcodeParser.class);

		try {
			barcodeRegexPattern = Pattern.compile(regex);

		} catch (PatternSyntaxException p) {
			log.error("Barcode parsing regex is not valid. Will use \\w+");
			barcodeRegexPattern = Pattern.compile("\\w+");

		}

	}

	/**
	 * Method getPatientId.
	 * @param scannedBarcode String
	 * @return String
	 */
	public static String getPatientId(String scannedBarcode) {
		matcher = barcodeRegexPattern.matcher(scannedBarcode);
		if (matcher.find()) {
			return matcher.group().toUpperCase();
		} else {
			log.warn("Could not match patientId using regular expression.");
			return null;
		}

	}

}
