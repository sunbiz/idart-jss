package org.celllife.idart.model.utils;

import java.util.Date;

import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.gui.utils.iDartImage;

/**
 */
public enum PackageLifeStage {
	PACKED("Packages Created", iDartImage.REPORT_PACKAGESARRIVE), 
	SCANNED_OUT("Packages Leaving Pharmacy",iDartImage.REPORT_PACKAGESSCANNEDOUT), 
	SCANNED_IN("Packages Received at Clinic", iDartImage.REPORT_PACKAGESSCANNEDIN), 
	PICKED_UP("Packages Collected by Patient",iDartImage.DISPENSEPACKAGE), 
	RETURNED("Packages Returned", iDartImage.PACKAGERETURN);

	private String action;
	private iDartImage iconImage;

	/**
	 * Constructor for PackageLifeStage.
	 * @param action String
	 * @param iconImage iDartImage
	 */
	PackageLifeStage(String action,iDartImage iconImage) {
		this.action = action;
		this.iconImage = iconImage;
	
	}

	/**
	 * Method getAction.
	 * @return String
	 */
	public String getAction() {
		return action;
	}

	

	/**
	 * Method getIconImage.
	 * @return iDartImage
	 */
	public iDartImage getIconImage() {
		return iconImage;
	}

	/**
	 * Return the date associated with this LifeStage for this package
	 * @param p
	 * @return Date
	 */
	public Date getPackageDate(Packages p) {
		switch (this) {
		case PACKED:
			return p.getPackDate();
		case SCANNED_OUT:
			return p.getDateLeft();
		case SCANNED_IN:
			return p.getDateReceived();
		case PICKED_UP:
			return p.getPickupDate();
		case RETURNED:
			return p.getDateReturned();
		default:
			return null;
		}
	}
		/**
		 * @return the string property name 
		 * for the package date property associated with this LifeStage
		 * for use in HQL queries
		 */
		public String getDatePropertyName() {
			switch (this) {
			case PACKED:
				return "packDate";
			case SCANNED_OUT:
				return "dateLeft";
			case SCANNED_IN:
				return "dateReceived";
			case PICKED_UP:
				return "pickupDate";
			case RETURNED:
				return "dateReturned";
			default:
				return null;
			}
	}
		
		/**
		 * @return the string name in plain english 
		 * for the package date property associated with this LifeStage
		 * for use in report column header
		 */
		public String getDateStringName() {
			switch (this) {
			case PACKED:
				return "Date Packed";
			case SCANNED_OUT:
				return "Date Left";
			case SCANNED_IN:
				return "Date Received";
			case PICKED_UP:
				return "Date Collected";
			case RETURNED:
				return "Date Returned";
			default:
				return null;
			}
	}
}
