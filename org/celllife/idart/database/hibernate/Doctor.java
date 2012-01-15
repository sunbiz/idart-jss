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

package org.celllife.idart.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 */
@Entity
public class Doctor {

	private String emailAddress;

	private String firstname;

	@Id
	@GeneratedValue
	private Integer id;

	private String lastname;

	private String mobileno;

	private char modified;

	private String telephoneno;

	private boolean active;

	public Doctor() {
		super();

	}

	/**
	 * @param surname
	 * @param firstname
	 * @param telephoneno
	 * @param mobileno
	 * @param emailAddress
	 * @param modified
	 * @param active
	 */
	public Doctor(String surname, String firstname, String telephoneno,
			String mobileno, String emailAddress, char modified, boolean active) {
		super();
		this.lastname = surname;
		this.firstname = firstname;

		this.telephoneno = telephoneno;
		this.mobileno = mobileno;
		this.emailAddress = emailAddress;
		this.modified = modified;
		this.active = active;
	}

	/**
	 * Method getEmailAddress.
	 * @return String
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Method getFirstname.
	 * @return String
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Method getFullname.
	 * @return String
	 */
	public String getFullname() {
		return lastname + ", " + firstname;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getLastname.
	 * @return String
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Method getMobileno.
	 * @return String
	 */
	public String getMobileno() {
		return mobileno;
	}

	/**
	 * Method getModified.
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method getTelephoneno.
	 * @return String
	 */
	public String getTelephoneno() {
		return telephoneno;
	}

	/**
	 * Method isActive.
	 * @return boolean
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Method setEmailAddress.
	 * @param emailAddress String
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Method setFirstname.
	 * @param firstname String
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Method setLastname.
	 * @param lastname String
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * Method setMobileno.
	 * @param mobileno String
	 */
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}

	/**
	 * Method setModified.
	 * @param modified char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setTelephoneno.
	 * @param telephoneno String
	 */
	public void setTelephoneno(String telephoneno) {
		this.telephoneno = telephoneno;
	}

	/**
	 * Method setActive.
	 * @param active boolean
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
