package model.manager.exports.columns;

import model.manager.exports.SimpleColumn;

import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.IdentifierType;

/**
 * This enum stores the available {@link SimpleColumn}'s along with their
 * header and template string.
 */

public class SimpleColumnsEnum implements IColumnEnum {

	public static final SimpleColumnsEnum patientId = new SimpleColumnsEnum(
			"Patient Number",
	"$!{fn.replaceSeperator($fn.getPatientField(\"Patient\", \"patientId\"))}");

	public static final SimpleColumnsEnum firstNames = new SimpleColumnsEnum(
			"First Name",
	"$!{fn.replaceSeperator($fn.getPatientField(\"Patient\", \"firstNames\"))}");

	public static final SimpleColumnsEnum lastName = new SimpleColumnsEnum("Surname",
	"$!{fn.replaceSeperator($fn.getPatientField(\"Patient\", \"lastname\"))}");

	public static final SimpleColumnsEnum accountStatus = new SimpleColumnsEnum(
			"Account Status",
	"$!{fn.getPatientField(\"Patient\", \"accountStatus\")}");

	public static final SimpleColumnsEnum dateOfBirth = new SimpleColumnsEnum(
			"Date of Birth",
	"$!{fn.formatDate('ymd', $fn.getPatientField(\"Patient\", \"dateOfBirth\"))}");

	public static final SimpleColumnsEnum sex = new SimpleColumnsEnum("Sex",
	"$!{fn.getPatientField(\"Patient\", \"sex\")}");

	public static final SimpleColumnsEnum address = new SimpleColumnsEnum(
			"Patient Address",
			"$!{fn.replaceSeperator($fn.getPatientField(\"Patient\", \"address1\"))} "
			+ "$!{fn.replaceSeperator($fn.getPatientField(\"Patient\", \"address2\"))} "
			+ "$!{fn.replaceSeperator($fn.getPatientField(\"Patient\", \"address3\"))}");

	// not available on iDART interface
	// nextOfKinName("Treatment Supporter",
	// "$!{fn.getPatientField(\"Patient\", \"nextOfKinName\")}"),

	// not available on iDART interface
	// nextOfKinPhone("Treatment Supporter Phone Number",
	// "$!{fn.getPatientField(\"Patient\", \"nextOfKinPhone\")}"),

	public static final SimpleColumnsEnum clinic = new SimpleColumnsEnum("Clinic",
	"$!{fn.getPatientField(\"Patient\", \"clinic.clinicName\")}");

	public static final SimpleColumnsEnum homePhone = new SimpleColumnsEnum(
			"Home Telephone Number",
	"$!{fn.getPatientField(\"Patient\", \"homePhone\")}");

	public static final SimpleColumnsEnum cellphone = new SimpleColumnsEnum(
			"Cellphone Number",
	"$!{fn.getPatientField(\"Patient\", \"cellphone\")}");

	public static final SimpleColumnsEnum province = new SimpleColumnsEnum(
			"Province", "$!{fn.getPatientField(\"Patient\", \"province\")}");
	
	public static SimpleColumnsEnum lastCollectedDate = new SimpleColumnsEnum(
			"Last Package Collection Date",
			"$!{fn.formatDate('ymd', $fn.getMostRecentPackageDetail(\""
			+ PackageDetailsEnum.COLLECTION_DATE + "\"))}");

	public static final SimpleColumnsEnum expectedRunoutDate = new SimpleColumnsEnum(
			"Expected Runout Date",
	"$!{fn.formatDate('ymd', $fn.getExpectedRunoutDate())}");

	public static final SimpleColumnsEnum lastCollectedDrugs = new SimpleColumnsEnum(
			"Last Package Collection Drugs",
			"$!{fn.getMostRecentPackageDetail(\""
			+ PackageDetailsEnum.COLLECTED_DRUGS + "\")}");

	public static final SimpleColumnsEnum[] all = new SimpleColumnsEnum[] {
		patientId, firstNames, lastName, accountStatus, dateOfBirth, sex,
		address, clinic, homePhone, cellphone, province,
		lastCollectedDate, expectedRunoutDate, lastCollectedDrugs };

	private String columnName;
	private String template;

	public SimpleColumnsEnum() {
		super();
	}

	public SimpleColumnsEnum(String columnName, String template) {
		this.template = template;
		this.columnName = columnName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String name) {
		this.columnName = name;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result
		+ ((template == null) ? 0 : template.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SimpleColumnsEnum other = (SimpleColumnsEnum) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SimpleColumnsEnum [columnName=" + columnName + ", template="
		+ template + "]";
	}
	
	@Override
	public String template() {
		return getColumnName();
	}

	public static SimpleColumnsEnum createFromAttributeType(AttributeType type) {
		String templateString = "$!{fn.replaceSeperator($fn.getPatientAttribute(\""
			+ type.getName() + "\"))}";
		return new SimpleColumnsEnum(type.getName(), templateString);
	}
	
	public static SimpleColumnsEnum createFromIdentifierType(IdentifierType type) {
		String templateString = "$!{fn.replaceSeperator($fn.getPatientIdentifier(\""
			+ type.getName() + "\"))}";
		return new SimpleColumnsEnum("ID: " + type.getName(), templateString);
	}
}
