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

/*
 * Created on 2005/03/24
 *
 */
package org.celllife.idart.database.hibernate;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue
	private Integer id;

	private char modified;

	@Column(name = "cl_password")
	private String password;

	@Column(name = "role", nullable = true)
	private String role;

	@Column(name = "cl_username")
	private String username;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "ClinicUser", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "clinicId") })
	private Set<Clinic> clinics; 
	public User() {
		super();
	}

	/**
	 * @param username
	 * @param password
	 * @param role
	 * @param modified
	 * @param clinics Set<Clinic>
	 */
	public User(String username, String password, String role, char modified, Set<Clinic> clinics) {
		super();
		this.username = username;
		this.password = password;
		this.role = role;
		this.modified = modified;
		this.clinics=clinics;

	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getModified.
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method getPassword.
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Method getRole.
	 * @return String
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Method getUsername.
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method setModified.
	 * @param modified char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method setPassword.
	 * @param password String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Method setRole.
	 * @param role String
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Method setUsername.
	 * @param username String
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Method getClinics.
	 * @return Set<Clinic>
	 */
	public Set<Clinic> getClinics() {
		return clinics;
	}

	/**
	 * Method setClinics.
	 * @param clinics Set<Clinic>
	 */
	public void setClinics(Set<Clinic> clinics) {
		this.clinics = clinics;
	}

}
