package org.celllife.idart.database.hibernate.tmp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 */
@Entity
public class DeletedItem {

	@Id
	@GeneratedValue
	private int id;

	private int deletedItemId;

	private String itemType;

	private boolean invalid = false;

	public DeletedItem() {
		super();
	}

	/**
	 * Constructor for DeletedItem.
	 * @param deletedItemId int
	 * @param itemType String
	 * @param invalid boolean
	 */
	public DeletedItem(int deletedItemId, String itemType, boolean invalid) {
		super();
		this.deletedItemId = deletedItemId;
		this.itemType = itemType;
		this.invalid = invalid;
	}

	/**
	 * Method getDeletedItemId.
	 * @return int
	 */
	public int getDeletedItemId() {
		return deletedItemId;
	}

	/**
	 * Method getId.
	 * @return int
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method getItemType.
	 * @return String
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * Method isInvalid.
	 * @return boolean
	 */
	public boolean isInvalid() {
		return invalid;
	}

	/**
	 * Method setDeletedItemId.
	 * @param deletedItemId int
	 */
	public void setDeletedItemId(int deletedItemId) {
		this.deletedItemId = deletedItemId;
	}

	/**
	 * Method setId.
	 * @param id int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method setInvalid.
	 * @param invalid boolean
	 */
	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * Method setItemType.
	 * @param itemType String
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

}
