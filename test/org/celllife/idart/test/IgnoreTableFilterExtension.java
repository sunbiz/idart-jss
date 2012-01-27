package org.celllife.idart.test;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.filter.AbstractTableFilter;

class IgnoreTableFilterExtension extends
		AbstractTableFilter {
	
	private String[] tablesToIgnore;
	
	public IgnoreTableFilterExtension(String[] tablesToIgnore) {
		this.tablesToIgnore = tablesToIgnore;
	}

	@Override
	public boolean isValidName(String name) throws DataSetException {
		boolean valid = true;
		if (tablesToIgnore != null && tablesToIgnore.length > 0) {
			String lowerCase = name.toLowerCase();
			for (String ignore : tablesToIgnore) {
				if (lowerCase.startsWith(ignore)) {
					return false;
				}
			}
		}
		return valid;
	}
	
	public void setTablesToIgnore(String[] tablesToIgnore) {
		this.tablesToIgnore = tablesToIgnore;
	}
}