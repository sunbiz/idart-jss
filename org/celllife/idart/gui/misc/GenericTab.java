/**
 * 
 */
package org.celllife.idart.gui.misc;

import org.eclipse.swt.widgets.TabItem;

/**
 */
public class GenericTab {
	
	protected TabItem tabItem;

	/* (non-Javadoc)
	 * @see org.celllife.idart.gui.misc.IGenericTab#setTabItem(org.eclipse.swt.widgets.TabItem)
	 */
	public void setTabItem(TabItem tabItem) {
		this.tabItem = tabItem;
	}

	/* (non-Javadoc)
	 * @see org.celllife.idart.gui.misc.IGenericTab#getTabItem()
	 */
	public TabItem getTabItem() {
		return tabItem;
	}
}
