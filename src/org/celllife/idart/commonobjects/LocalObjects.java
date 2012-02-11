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

package org.celllife.idart.commonobjects;

import model.manager.AdministrationManager;
import model.nonPersistent.PharmacyDetails;

import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.User;
import org.hibernate.Session;

/**
 */
public class LocalObjects {

	public static IdentifierType nationalIdentifierType;

	public static Clinic mainClinic;

	public static int userId;

	public static Clinic currentClinic;

	public static PharmacyDetails pharmacy;

	/**
	 * Private constructor to prevent instantiation
	 */
	private LocalObjects() {
	}

	/**
	 * Method getUser.
	 * 
	 * @param sess
	 *            Session
	 * @return User
	 */
	public static User getUser(Session sess) {
		if (userId == -1)
			return null;
		return AdministrationManager.getUserById(sess, userId);
	}

	/**
	 * Method setUser.
	 * 
	 * @param u
	 *            User
	 */
	public static void setUser(User u) {
		if (u == null) {
			userId = -1;
		} else {
			userId = u.getId();
		}
	}

	public static boolean loggedInToMainClinic() {
		return currentClinic.equals(mainClinic);
	}
}
