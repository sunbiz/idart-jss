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
public class SimpleDomain {

	@Id
	@GeneratedValue
	private Integer id;

	private String name;

	private String value;

	private String description;

	public SimpleDomain() {
		super();
	}

	/**
	 * Constructor for SimpleDomain.
	 * @param name String
	 * @param value String
	 * @param description String
	 */
	public SimpleDomain(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	/**
	 * Method getDescription.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getName.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method getValue.
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Method setDescription.
	 * @param description String
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method setName.
	 * @param name String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method setValue.
	 * @param value String
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleDomain [name=").append(name).append(", value=")
				.append(value).append("]");
		return builder.toString();
	}
}
