package org.celllife.idart.gui.misc;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.hibernate.Session;

public interface IGenericTab {
	
	public static final String EMPTY = ""; //$NON-NLS-1$

	/**
	 * Set the Hibernate session for the tab to use.
	 * 
	 * @param hSession
	 */
	public void setSession(Session hSession);

	/**
	 * Set the tab's parent container.
	 * 
	 * @param parent
	 */
	public void setParent(TabFolder parent);

	/**
	 * Set the style to use for the TabItem.
	 * 
	 * @see TabItem
	 * @param SWTStyle
	 */
	public void setStyle(int SWTStyle);

	/**
	 * Creates the TabItem and its contents.
	 */
	public void create();

	/**
	 * Gets the tab item for the tab or null.
	 * 
	 * @return TabItem
	 */
	public TabItem getTabItem();

	/**
	 * Sets the tabs TabItem.
	 * @param item
	 */
	public void setTabItem(TabItem item);
}
