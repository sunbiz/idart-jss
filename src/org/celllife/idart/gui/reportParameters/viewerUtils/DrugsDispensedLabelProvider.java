/**
 * 
 */
package org.celllife.idart.gui.reportParameters.viewerUtils;

import org.celllife.idart.database.hibernate.Drug;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Rashid
 * 
 */
public class DrugsDispensedLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	@Override
	public String getColumnText(Object arg0, int arg1) {
		return ((Drug)arg0).getName();
	}

	@Override
	public void addListener(ILabelProviderListener arg0) {
		
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
	}
}