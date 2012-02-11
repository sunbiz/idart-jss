package org.celllife.idart.facade;

import java.util.Date;
import java.util.List;

import model.manager.PackageManager;
import model.manager.PatientManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.events.PackageEvent;
import org.celllife.idart.events.PackageEvent.Type;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.adamtaft.eb.EventBusService;

/**
 */
public class PackageReturnFacade {

	private Logger log = Logger.getLogger(this.getClass());

	Session hSession;

	/**
	 * Constructor for PackageReturnFacade.
	 * @param hSession Session
	 */
	public PackageReturnFacade(Session hSession) {
		this.hSession = hSession;
	}

	/**
	 * Sets the return fields for the package, saves it and updates the stock
	 * levels for the drugs in the Package.
	 * 
	 * Should only be called from within a transaction.
	 * 
	 * @param pack
	 * @param destroyStock
	 * @param returnedDate
	 * @throws HibernateException
	 */
	public void returnPackage(Packages pack, boolean destroyStock,
			Date returnedDate, String reasonForReturn) throws HibernateException {
		pack.setPackageReturned(true);
		pack.setStockReturned(!destroyStock);
		pack.setDateReturned(returnedDate);
		pack.setReasonForStockReturn(reasonForReturn);
		hSession.saveOrUpdate(pack);
		hSession.flush();
		
		for (int i = 0; i < pack.getPackagedDrugs().size(); i++) {
			StockManager.updateStockLevel(hSession, (pack.getPackagedDrugs()
					.get(i)).getStock());
			hSession.flush();
		}
		log.info("Package Returned - " + new Date().toString());
		
		EventBusService.publish(new PackageEvent(Type.RETURN, pack));
	}
	
	/**
	 * Method getAllPackagesForPatient.
	 * @param p Patient
	 * @return List<Packages>
	 */
	public List<Packages> getAllPackagesForPatient(Patient p) {
		try {
			List<Packages> myPack = null;
			myPack = PackageManager.getAllPackagesForPatient(hSession, p);
			return myPack;
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method getPackageDrugsStringContent.
	 * @param pcks Packages
	 * @return String
	 */
	public String getPackageDrugsStringContent(Packages pcks) {
		try {
			return PackageManager.getShortPackageContentsString(hSession, pcks);
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Closes an Episode and sets the Patient's active status to false.
	 * 
	 * Should only be called from within a transaction.
	 * 
	 * @param epi
	 * @param stopReason
	 * @param dteEpisodeStopDate
	 * @param stopNotes
	 */
	public void closeEpisode(Episode epi, String stopReason,
			Date dteEpisodeStopDate, String stopNotes) {
		// Patient with episode is saved, given that
		// The episode start and stop dated are not null
		// by verifying if this episode is still open.
		epi.setStopReason(stopReason);
		epi.setStopDate(dteEpisodeStopDate);
		epi.setStopNotes(stopNotes);
		epi.getPatient().setAccountStatus(false);
		PatientManager.savePatient(hSession, epi.getPatient());
	}

}
