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

package model.nonPersistent;

/**
 */
public class PatientIdAndName {

	private final String patientId;
	private final String names;
	private final int id;

	/**
	 * Constructor for PatientIdAndName.
	 * @param id 
	 * @param patientId String
	 * @param names String
	 */
	public PatientIdAndName(int id, String patientId, String names) {
		super();
		this.id = id;
		this.patientId = patientId;
		this.names = names;
	}

	/**
	 * Method getNames.
	 * @return String
	 */
	public String getNames() {
		return names;
	}

	/**
	 * Method getPatientId.
	 * @return String
	 */
	public String getPatientId() {
		return patientId;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((names == null) ? 0 : names.hashCode());
		result = prime * result
				+ ((patientId == null) ? 0 : patientId.hashCode());
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
		PatientIdAndName other = (PatientIdAndName) obj;
		if (id != other.id)
			return false;
		if (names == null) {
			if (other.names != null)
				return false;
		} else if (!names.equals(other.names))
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return patientId + " (" + names + ")";
	}

}
