package org.celllife.idart.gui.dataQuality;

import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;

public abstract class AbstractDataQualityReport implements DataQualityReport {

	private Session Session;
	
	public Session getSession(){
		 
		if(Session == null){
			Session = HibernateUtil.getNewSession();
		}
		return Session;
	}

}
