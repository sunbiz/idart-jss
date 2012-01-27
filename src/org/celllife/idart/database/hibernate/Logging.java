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

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 */
@Entity
public class Logging {

	@Id
	@GeneratedValue
	private Integer id;

	private Date transactionDate;

	@ManyToOne
	@JoinColumn(name = "iDart_User")
	private User iDart_User;

	private String transactionType;

	private String itemId;

	private char modified;

	private String message;

	public Logging() {
		super();
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method getItemId.
	 * @return String
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * Method setItemId.
	 * @param itemId String
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * Method getMessage.
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Method setMessage.
	 * @param message String
	 */
	public void setMessage(String message) {

		this.message = message;
	
	}

	/**
	 * Method getModified.
	 * @return char
	 */
	public char getModified() {
		return modified;
	}

	/**
	 * Method setModified.
	 * @param modified char
	 */
	public void setModified(char modified) {
		this.modified = modified;
	}

	/**
	 * Method getTransactionDate.
	 * @return Date
	 */
	public Date getTransactionDate() {
		return transactionDate;
	}

	/**
	 * Method setTransactionDate.
	 * @param transactionDate Date
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	/**
	 * Method getTransactionType.
	 * @return String
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * Method setTransactionType.
	 * @param transactionType String
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * Method getIDart_User.
	 * @return User
	 */
	public User getIDart_User() {
		return iDart_User;
	}

	/**
	 * Method setIDart_User.
	 * @param iDart_User User
	 */
	public void setIDart_User(User iDart_User) {
		this.iDart_User = iDart_User;
	}

}
