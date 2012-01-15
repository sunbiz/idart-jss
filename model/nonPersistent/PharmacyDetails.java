/**
 * 
 */
package model.nonPersistent;

/**
 * @author Rashid
 * 
 * This class represents the single details of the pharmacy. The fields are
 * stroed in the simple Domain table and are editable through the update
 * pharmacy details screen
 * 
 */
public class PharmacyDetails {

	private String pharmacist;

	private String assistantPharmacist;

	private String street;

	private String city;

	private String contactNo;

	private String pharmacyName;
	
	private boolean modified;
	

	/**
	 * @return the modified
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @param modified the modified to set
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	/**
	 * @param pharmacist
	 * @param assistantPharmacist
	 * @param street
	 * @param city
	 * @param contactNo
	 * @param pharmacyName
	 */
	public PharmacyDetails(String pharmacist, String assistantPharmacist,
			String street, String city, String contactNo, String pharmacyName) {
		super();
		this.pharmacist = pharmacist;
		this.assistantPharmacist = assistantPharmacist;
		this.street = street;
		this.city = city;
		this.contactNo = contactNo;
		this.pharmacyName = pharmacyName;
	}

	/**
	 * Default constructor
	 * 
	 */
	public PharmacyDetails() {
		super();
	}

	/**
	 * @return the assistantPharmacist
	 */
	public String getAssistantPharmacist() {
		return assistantPharmacist;
	}

	/**
	 * @param assistantPharmacist
	 *            the assistantPharmacist to set
	 */
	public void setAssistantPharmacist(String assistantPharmacist) {
		this.assistantPharmacist = assistantPharmacist;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the contactNo
	 */
	public String getContactNo() {
		return contactNo;
	}

	/**
	 * @param contactNo
	 *            the contactNo to set
	 */
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	/**
	 * @return the pharmacist
	 */
	public String getPharmacist() {
		return pharmacist;
	}

	/**
	 * @param pharmacist
	 *            the pharmacist to set
	 */
	public void setPharmacist(String pharmacist) {
		this.pharmacist = pharmacist;
	}

	/**
	 * @return the pharmacyName
	 */
	public String getPharmacyName() {
		return pharmacyName;
	}

	/**
	 * @param pharmacyName
	 *            the pharmacyName to set
	 */
	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street
	 *            the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	@Override
	public String toString() {
		return super.toString() + pharmacist + " " + assistantPharmacist + " "
				+ street + " " + city + " " + contactNo + " " + pharmacyName;

	}

}
