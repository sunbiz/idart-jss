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
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "patientstattypes")
public class PatientStatTypes {

	@Id
	@GeneratedValue
	private Integer id;
	private String statname;
	private String statformat;

	public PatientStatTypes() {
		super();

	}

	public PatientStatTypes(String statname, String statformat) {

		super();
		this.statname = statname;
		this.statformat = statformat;
	}

	public int getId() {
		return id;
	}

	public String getstatname() {
		return statname;
	}

	public String getstatformat() {
		return statformat;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setstatname(String statname) {
		this.statname = statname;
	}

	public void setDateofVisit(String statformat) {
		this.statformat = statformat;
	}

}
