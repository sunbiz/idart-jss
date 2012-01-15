package model.manager.exports.columns;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public enum DrugsDispensedEnum {
	
	dateDispensed("Date Dispensed", Date.class, 17 ),
	
	clinic("Clinic", String.class, 15),
	
	patientFirstName("Patient First Name", String.class, 15),
	
	patientLastName("Patient Last Name", String.class, 15),
	
	patientId("Patient Number", String.class, 15),
	
	sex("Sex", Character.class, 6),
	
	dateOfBirth("Date Of Birth", Date.class, 17),
	
	age("Age on {0,date,medium}", Integer.class, 8), 
	
	packageId("Package ID", String.class, 25),
	
	pregnant("Was pregnant on {0,date,medium}", Boolean.class, 15),
	
	drugGroupName("Drug Group Name", String.class, 15),
	
	regimen("Regimen", String.class, 10), 
	
	drugsCollected("Drugs Collected", String.class, 45),
	
	arvStartDate("ARV Start Date", Date.class, 17),
	
	expectedRunoutDate("Expected runout date", Date.class,17);
	
	
	private String title;

	private Class dataType;
	
	private int columnWidth;
	
	private static Date endDate = new Date();

	/**
	 * @param title
	 * @param displayName
	 * @param dataType
	 */
	private DrugsDispensedEnum(String title, Class dataType, int columnWidth) {
		this.title = title;
		this.dataType = dataType;
		this.columnWidth = columnWidth;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
	public static DrugsDispensedEnum[] getDefaults(){
		return new DrugsDispensedEnum[] { dateDispensed, clinic,
				patientFirstName, patientLastName, patientId, sex, packageId,
				drugGroupName };
	}
	
	public static List<DrugsDispensedEnum> getCompulsory() {
		return Arrays.asList(new DrugsDispensedEnum[] { dateDispensed, clinic, patientId });
	}

	public String getTitle() {
		return MessageFormat.format(title, endDate);
	}
	
	public int getColumnWidth() {
		return columnWidth;
	}
	
	public Class getDataType() {
		return dataType;
	}
	
	public static void setEndDate(Date endDate) {
		DrugsDispensedEnum.endDate = endDate;
	}
}
