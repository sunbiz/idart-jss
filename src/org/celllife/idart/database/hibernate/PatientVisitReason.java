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
@Table(name = "patientvisitreason")
public class PatientVisitReason {

	@Id
	@GeneratedValue
	private Integer id;
	private String reasonforvisit;

	public PatientVisitReason() {
		super();
	}

	public PatientVisitReason(String reasonforvisit) {
		super();
		this.reasonforvisit = reasonforvisit;
	}

	public int getId() {
		return id;
	}

	public String getvisitreason() {
		return reasonforvisit;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setreasonforvisit(String reasonforvisit) {
		this.reasonforvisit = reasonforvisit;
	}

}
