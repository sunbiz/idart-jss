package org.celllife.idart.gui.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class used to store an instance of a FontRegisty, a ColorRegistry and
 * an ImageRegistry.
 * 
 * @see ImageRegistry
 * @see FontRegistry
 * @see ColorRegistry
 */
public class ResourceUtils {

	/**
	 * private constructor to prevent instantiation
	 */
	private ResourceUtils() {
	}

	private static final FontRegistry fontRegistry = new FontRegistry();

	private static final ColorRegistry colorRegistry = new ColorRegistry();

	private static final ImageRegistry imageRegistry = new ImageRegistry();

	private static Logger log = Logger.getLogger(ResourceUtils.class);

	static {

		createFontRegisty();
		createColorRegistry();
		createImageRegistry();

	}

	private static void createFontRegisty() {

		fontRegistry.put(iDartFont.VERASANS_8.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 8,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_8_ITALIC.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 8,
						SWT.ITALIC) });
		fontRegistry.put(iDartFont.VERASANS_8_BOLD.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 8,
						SWT.BOLD) });
		fontRegistry.put(iDartFont.VERASANS_10.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 10,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_10_ITALIC.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 10,
						SWT.ITALIC) });
		fontRegistry.put(iDartFont.VERASANS_10_BOLD.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 10,
						SWT.BOLD) });
		fontRegistry.put(iDartFont.VERASANS_11.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 11,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_12.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 12,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_12_ITALIC.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 12,
						SWT.ITALIC) });
		fontRegistry.put(iDartFont.VERASANS_12_BOLDITALIC.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 12,
						SWT.ITALIC | SWT.BOLD) });
		fontRegistry.put(iDartFont.VERASANS_14.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 14,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_16.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 16,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_20.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 20,
						SWT.NORMAL) });
		fontRegistry.put(iDartFont.VERASANS_24.name(),
				new FontData[] { new FontData("Bitstream Vera Sans", 24,
						SWT.NORMAL) });
	}

	private static void createColorRegistry() {

		colorRegistry.put(iDartColor.RED.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_RED).getRGB());
		colorRegistry.put(iDartColor.GREEN.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_GREEN).getRGB());
		colorRegistry.put(iDartColor.YELLOW.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_YELLOW).getRGB());
		colorRegistry.put(iDartColor.BLACK.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_BLACK).getRGB());
		colorRegistry.put(iDartColor.GRAY.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_GRAY).getRGB());
		colorRegistry.put(iDartColor.WHITE.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_WHITE).getRGB());
		colorRegistry.put(iDartColor.BLUE.name(), Display.getDefault()
				.getSystemColor(SWT.COLOR_BLUE).getRGB());
		colorRegistry.put(iDartColor.LIST_BACKGROUND.name(), Display
				.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND)
				.getRGB());
		colorRegistry.put(iDartColor.WIDGET_BACKGROUND.name(), Display
				.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)
				.getRGB());
		colorRegistry.put(iDartColor.WIDGET_NORMAL_SHADOW_BACKGROUND.name(),
				Display.getDefault().getSystemColor(
						SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB());
		colorRegistry.put(iDartColor.WIDGET_LIGHT_SHADOW_BACKGROUND.name(),
				Display.getDefault().getSystemColor(
						SWT.COLOR_WIDGET_LIGHT_SHADOW).getRGB());
		colorRegistry.put(iDartColor.WIDGET_TITLE_INACTIVE_FOREGROUND.name(),
				Display.getDefault().getSystemColor(
						SWT.COLOR_TITLE_INACTIVE_FOREGROUND).getRGB());
		colorRegistry.put(iDartColor.PALE_PEACH.name(), new Color(Display
				.getDefault(), 239, 217, 172).getRGB());

	}

	private static void createImageRegistry() {

		try {
			URL baseDir = (new File("img" + File.separator)).toURI().toURL();

			imageRegistry.put(iDartImage.ARROWLEFT_40X34.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"ArrowLeft_40x34.jpg")));
			imageRegistry.put(iDartImage.ARROWLEFT.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "ArrowLeft.jpg")));
			imageRegistry.put(iDartImage.ARROWRIGHT_40X34.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"ArrowRight_40x34.jpg")));
			imageRegistry.put(iDartImage.ARROWRIGHT.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "ArrowRight.jpg")));
			imageRegistry.put(iDartImage.ARVSTARTDATE_50X43.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"ARVStartDate_50x43.jpg")));
			imageRegistry.put(iDartImage.BACKARROW.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "BackArrow.jpg")));
			imageRegistry.put(iDartImage.CHILD_30X26.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Child_30x26.jpg")));
			imageRegistry.put(iDartImage.CHILD_50X43.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Child_50x43.jpg")));
			imageRegistry.put(iDartImage.CLINIC.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Clinic.jpg")));
			imageRegistry.put(iDartImage.DISPENSEPACKAGE.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"DispensePackage.jpg")));
			imageRegistry.put(iDartImage.DISPENSEPACKAGENOW_40X34.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"DispensePackageNow_40x34.jpg")));
			imageRegistry.put(iDartImage.DISPENSEPACKAGES.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"DispensePackages.jpg")));
			imageRegistry.put(iDartImage.DOCTOR_30X26.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Doctor_30x26.jpg")));
			imageRegistry.put(iDartImage.DOCTOR.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Doctor.jpg")));
			imageRegistry.put(iDartImage.DOWNARROW_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"DownArrow_30x26.jpg")));
			imageRegistry.put(iDartImage.DRUG_30X26.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Drug_30x26.jpg")));
			imageRegistry.put(iDartImage.DRUG_40X34.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Drug_40x34.jpg")));
			imageRegistry.put(iDartImage.DRUGALLERGY.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "DrugAllergy.jpg")));
			imageRegistry.put(iDartImage.DRUGGROUP.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "DrugGroup.jpg")));
			imageRegistry.put(iDartImage.DRUG.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Drug.jpg")));
			imageRegistry.put(iDartImage.FINAL_LOGO.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "final_logo.png")));
			imageRegistry.put(iDartImage.GENERALADMIN.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "GeneralAdmin.jpg")));
			imageRegistry.put(iDartImage.HELP.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Help.jpg")));
			imageRegistry.put(iDartImage.HOURGLASS.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Hourglass.jpg")));
			imageRegistry.put(iDartImage.LEFTARROW_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"LeftArrow_30x26.jpg")));
			imageRegistry.put(iDartImage.LOGO_GRAPHIC.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "logo_graphic.png")));
			imageRegistry.put(iDartImage.LOGO_IDART.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Logo_iDART.png")));
			imageRegistry.put(iDartImage.OUTGOINGPACKAGES.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"OutgoingPackages.jpg")));
			imageRegistry.put(iDartImage.PACKAGEDELETE.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PackageDelete.jpg")));
			imageRegistry.put(iDartImage.PACKAGERETURN.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PackageReturn.jpg")));
			imageRegistry.put(iDartImage.PACKAGESARRIVE.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PackagesArrive.jpg")));
			imageRegistry.put(iDartImage.PACKAGESAWAITINGPICKUP_40X34.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PackagesAwaitingPickup_40x34.jpg")));
			imageRegistry.put(iDartImage.PACKAGESAWAITINGPICKUP.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PackagesAwaitingPickup.jpg")));
			imageRegistry.put(iDartImage.PATIENTADMIN.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PatientAdmin.jpg")));
			imageRegistry.put(iDartImage.PATIENTARRIVES.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PatientArrives.jpg")));
			imageRegistry.put(iDartImage.PATIENTDUPLICATES_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PatientDuplicates_30x26.jpg")));
			imageRegistry.put(iDartImage.PATIENTDUPLICATES.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PatientDuplicates.jpg")));
			imageRegistry.put(iDartImage.PATIENTINFOLABEL_40X34.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PatientInfoLabel_40x34.jpg")));
			imageRegistry.put(iDartImage.PATIENTINFOLABEL.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PatientInfoLabel.jpg")));
			imageRegistry.put(iDartImage.PATIENTNEW.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PatientNew.jpg")));
			imageRegistry.put(iDartImage.PATIENTUPDATE.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PatientUpdate.jpg")));
			imageRegistry.put(iDartImage.PATIENTUPDATE.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PatientUpdate.PNG")));
			imageRegistry.put(iDartImage.PAVAS.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PAVAS.jpg")));
			imageRegistry.put(iDartImage.PAVASVISITS.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PAVASVisits.jpg")));
			imageRegistry.put(iDartImage.PAVASSTATS.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PAVASStats.jpg")));
			imageRegistry.put(iDartImage.PHARMACYUSER.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "PharmacyUser.jpg")));
			imageRegistry.put(iDartImage.PRESCRIPTIONADDDRUG_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PrescriptionAddDrug_30x26.jpg")));
			imageRegistry.put(iDartImage.PRESCRIPTIONADDDRUG.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PrescriptionAddDrug.jpg")));
			imageRegistry.put(iDartImage.PRESCRIPTIONDELETE.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PrescriptionDelete.jpg")));
			imageRegistry.put(iDartImage.PRESCRIPTIONNEW_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PrescriptionNew_30x26.jpg")));
			imageRegistry.put(iDartImage.PRESCRIPTIONNEW.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"PrescriptionNew.jpg")));
			imageRegistry.put(iDartImage.REDOPACKAGE_40X34.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"RedoPackage_40x34.jpg")));
			imageRegistry.put(iDartImage.REDOPACKAGE.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "RedoPackage.jpg")));
			imageRegistry.put(iDartImage.REPORT_ACTIVEPATIENTS.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_ActivePatients.jpg")));
			imageRegistry.put(iDartImage.REPORT_OUTGOINGPACKAGES.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_OutgoingPackages.jpg")));
			imageRegistry.put(iDartImage.REPORT_PACKAGESARRIVE.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PackagesArrive.jpg")));
			imageRegistry.put(iDartImage.REPORT_PACKAGESSCANNEDIN.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PackagesScannedIn.jpg")));
			imageRegistry.put(iDartImage.REPORT_PACKAGESSCANNEDOUT.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PackagesScannedOut.jpg")));
			imageRegistry.put(iDartImage.REPORT_PACKAGETRACKING.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PackageTracking.jpg")));
			imageRegistry.put(iDartImage.REPORT_PATIENTDEFAULTERS.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PatientDefaulters.jpg")));
			imageRegistry.put(iDartImage.REPORT_PATIENTHISTORY_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PatientHistory_30x26.jpg")));
			imageRegistry.put(iDartImage.REPORT_PATIENTHISTORY.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_PatientHistory.jpg")));
			imageRegistry.put(iDartImage.REPORTS.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Reports.jpg")));
			imageRegistry.put(iDartImage.REPORT_STOCKCONTROLPERCLINIC.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_StockControlPerClinic.jpg")));
			imageRegistry.put(iDartImage.REPORT_STOCKCONTROLPERDRUG.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"Report_StockControlPerDrug.jpg")));
			imageRegistry.put(iDartImage.RIGHTARROW_30X26.name(),
					ImageDescriptor.createFromURL(new URL(baseDir,
					"RightArrow_30x26.jpg")));
			imageRegistry.put(iDartImage.SPLASH.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "splash.png")));
			imageRegistry.put(iDartImage.STOCKCONTROL.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "StockControl.jpg")));
			imageRegistry.put(iDartImage.STOCKDELETE.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "StockDelete.jpg")));
			imageRegistry.put(iDartImage.UNUSED.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "Unused.jpg")));
			imageRegistry.put(iDartImage.UPARROW_30X26.name(), ImageDescriptor
					.createFromURL(new URL(baseDir, "UpArrow_30x26.jpg")));

		} catch (MalformedURLException m) {
			log.error(m);
		}

	}

	/**
	 * Returns a Font object that can be used by an interface element. Fonts are
	 * stored in a FontRegistry and therefore do not have to be disposed outside
	 * of this class.
	 * 
	 * @param theFont
	 *            iDartFont
	 * @return the Font specified by the iDartFont enum.
	 */
	public static Font getFont(iDartFont theFont) {
		if (!fontRegistry.hasValueFor(theFont.name())) {
			log.warn("No font defined for fontKey: " + theFont.name());
		}
		return fontRegistry.get(theFont.name());

	}

	/**
	 * Returns a Color object that can be used by an interface element. Colors
	 * are stored in a ColorRegistry and therefore do not have to be disposed
	 * outside of this class.
	 * 
	 * @param theColor
	 *            iDartColor
	 * @return the Color specified by the iDartColor enum.
	 */
	public static Color getColor(iDartColor theColor) {
		if (!colorRegistry.hasValueFor(theColor.name())) {
			log.warn("No color defined for colorKey: " + theColor.name());
		}
		return colorRegistry.get(theColor.name());

	}

	/**
	 * Returns a Image object that can be used by an interface element. Images
	 * are stored in a ImageRegistry and therefore do not have to be disposed
	 * outside of this class.
	 * 
	 * @param theiDartImage
	 *            iDartImage
	 * @return the Image specified by iDartImage enum.
	 */
	public static Image getImage(iDartImage theiDartImage) {
		Image theImage = imageRegistry.get(theiDartImage.name());
		if (theImage == null) {
			log.warn("No image defined for imageKey: " + theiDartImage.name());
		}
		return theImage;

	}
}
