package model.manager;

import java.util.Date;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Alerts;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class AlertManager {
	
	private static final Logger log = Logger.getLogger(AlertManager.class);
	/**
	 * Session expected to be open and have a transaction started.
	 * 
	 * @param type
	 * @param message
	 * @param hSession
	 */
	public static void createAlert(String type, String message, Session hSession){
		try {
			Alerts alerts = new Alerts();
			alerts.setAlertDate(new Date());
			alerts.setAlertType(type);
			alerts.setAlertMessage(message);
			hSession.save(alerts);
		} catch (HibernateException e){
			log.error("Error creating alert", e); //$NON-NLS-1$
		}
	}

}
