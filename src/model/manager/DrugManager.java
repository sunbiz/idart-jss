/**
 *
 */
package model.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 */
public class DrugManager {

	private static Logger log = Logger.getLogger(DrugManager.class);

	// ---------- METHODS FOR DRUG MANAGER ---------------

	/**
	 * Method to check if a drug already exists
	 * 
	 * @param session
	 *            the current hibernate session
	 * @param name
	 *            the name of the drug
	 * @param form
	 *            the form of the drug
	 * @param packsize
	 *            the packsize of the drug
	 * @return true if drug exists else false
	 */
	@SuppressWarnings("unchecked")
	public static boolean drugExists(Session session, String name, String form,
			int packsize) {
		Query query = session.createQuery("select d from Drug d where "
				+ "upper(d.name) = :name and upper(d.form.form) = :form "
				+ "and d.packSize = :packsize ");
		query.setString("name", name.toUpperCase());
		query.setString("form", form.toUpperCase());
		query.setInteger("packsize", packsize);

		List<Drug> result = query.list();
		if (result.size() > 0)
			return true;
		return false;
	}

	/**
	 * Check if the drugName exists in the database
	 * 
	 * @param session
	 *            Session
	 * @param drugName
	 * @return true if exists, else false
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static boolean drugNameExists(Session session, String drugName)
			throws HibernateException {
		boolean result = false;
		List<Drug> results;
		results = session.createQuery(
				"from Drug as d where upper(d.name) =:drugName").setString(
				"drugName", drugName.toUpperCase()).list();
		if (!results.isEmpty()) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * find the latest manufacturer of a specified drug
	 * 
	 * @param session
	 *            Session
	 * @param drugName
	 * @return String with name of latest drug manufacturer, or "" if none
	 * @throws HibernateException
	 */
	public static String getLatestDrugManufacturer(Session session,
			String drugName, String stockCenterName) throws HibernateException {
		String results;
		results = (String) session
				.createQuery(
						"select s.manufacturer from Stock s "
								+ "where s.drug.name=:drugName "
								+ "and s.stockCenter.stockCenterName = :stockCenterName "
								+ "order by s.id DESC").setString("drugName",
						drugName).setString("stockCenterName", stockCenterName)
				.setMaxResults(1).uniqueResult();
		return results;
	}

	/**
	 * Method getQuantityForDrugInPackage.
	 * 
	 * @param pack
	 *            Packages
	 * @param drug
	 *            Drug
	 * @return int
	 */
	public static int getQuantityForDrugInPackage(Packages pack, Drug drug) {

		if (pack.getPackagedDrugs() == null)
			return 0;
		int qty = 0;

		for (PackagedDrugs pd : pack.getPackagedDrugs()) {
			if (pd.getStock().getDrug().getId() == drug.getId()) {
				qty += pd.getAmount();
			}
		}

		return qty;
	}

	/**
	 * Returns this drug list as a string of chemical components and strengths
	 * 
	 * @param theDrugList
	 * @param separator
	 *            String
	 * @param includeStrength 
	 * @return String
	 */
	public static String getDrugListString(Collection<Drug> theDrugList,
			String separator, boolean includeStrength) {
		StringBuffer drugListString = new StringBuffer();
		Iterator<Drug> drugIt = theDrugList.iterator();
		while (drugIt.hasNext()) {
			Drug theDrug = drugIt.next();
			drugListString.append(getShortGenericDrugName(theDrug, includeStrength));
			if (drugIt.hasNext()) {
				drugListString.append(separator);
			}
		}

		return drugListString.toString();
	}

	/**
	 * Get the short generic name (using chem compounds if any) for this drug
	 * 
	 * @param d
	 * @param includeStrength
	 * @return String
	 */
	public static String getShortGenericDrugName(Drug d, boolean includeStrength) {
		StringBuffer shortDrugString = new StringBuffer();

		java.util.Set<ChemicalDrugStrength> csSet = d
				.getChemicalDrugStrengths();
		if (csSet.size() > 0) {
			Iterator<ChemicalDrugStrength> csIt = csSet.iterator();

			while (csIt.hasNext()) {
				ChemicalDrugStrength cs = csIt.next();
				shortDrugString.append(cs.getChemicalCompound().getAcronym());
				if (includeStrength)
					shortDrugString.append(" ").append(cs.getStrength());
				if (csIt.hasNext()) {
					shortDrugString.append("/");
				}
			}
		} else {
			shortDrugString.append(d.getName());
		}
		return shortDrugString.toString();
	}

	/**
	 * Returns a particular drug
	 * 
	 * @param sess
	 *            Session
	 * @param drugName
	 *            String
	 * @return Drug
	 * @throws HibernateException
	 */
	public static Drug getDrug(Session sess, String drugName)
			throws HibernateException {
		Drug theDrug = null;
		theDrug = (Drug) sess.createQuery(
				"from Drug as d where d.name = :drugName").setString(
				"drugName", drugName).setMaxResults(1).uniqueResult();
		return theDrug;
	}

	public static String getDrugNameForPackagedDrug(Session session,
			int packageDrugId) {
		return (String) session.createQuery(
				"select pd.stock.drug.name from PackagedDrugs pd "
						+ "where pd.id = :pid ").setInteger("pid",
				packageDrugId).uniqueResult();

	}

	/**
	 * Returns a list of Drug Objects for Stock take Difference here is that the
	 * query does not return drugs with no batches.
	 * 
	 * @param sess
	 * 
	 * @param includeZeroBatches
	 * @return List<Drug>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Drug> getDrugsListForStockTake(Session sess,
			boolean includeZeroBatches) throws HibernateException {
		List<Drug> result;
		if (includeZeroBatches) {
			result = sess.createQuery(
					"select distinct d" + " from Drug as d, Stock s"
							+ " where d.id = s.drug" +

							" order by d.name asc").list();
		} else {
			result = sess.createQuery(
					"select distinct d" + " from Drug as d, Stock s"
							+ " where s.drug = d.id and "
							+ " s.hasUnitsRemaining = 'T'"
							+ " order by d.sideTreatment, d.name asc").list();

		}
		return result;

	}

	/**
	 * Saves the newly created drug
	 * 
	 * @param s
	 *            Session
	 * @param theDrug
	 *            Drug
	 * @throws HibernateException
	 */
	public static void saveDrug(Session s, Drug theDrug)
			throws HibernateException {
		s.save(theDrug);

	}

	// ---------- METHODS FOR CHEMICAL DRUG STRENGTH MANAGER ---------------

	/**
	 * Returns all chemical compounds
	 * 
	 * @param sess
	 * @return List<ChemicalCompound>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<ChemicalCompound> getAllChemicalCompounds(Session sess)
			throws HibernateException {
		List<ChemicalCompound> result = sess.createQuery(
				"select c from ChemicalCompound as c order by c.name").list();

		return result;
	}

	/**
	 * Returns all drugs
	 * 
	 * @param sess
	 * @return List<Drug>
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static List<Drug> getAllDrugs(Session sess)
			throws HibernateException {
		List<Drug> result = sess.createQuery(
				"select d from Drug as d order by d.name").list();

		return result;
	}

	/**
	 * Method getChemicalCompoundByAcronym.
	 * 
	 * @param s
	 *            Session
	 * @param acronym
	 *            String
	 * @return ChemicalCompound
	 * @throws HibernateException
	 */
	public static ChemicalCompound getChemicalCompoundByAcronym(Session s,
			String acronym) throws HibernateException {
		ChemicalCompound theChem = null;
		theChem = (ChemicalCompound) s
				.createQuery(
						"select chem from ChemicalCompound as chem where chem.acronym = :acr")
				.setString("acr", acronym).setMaxResults(1).uniqueResult();
		return theChem;
	}

	/**
	 * Method getChemicalCompoundByName.
	 * 
	 * @param s
	 *            Session
	 * @param name
	 *            String
	 * @return ChemicalCompound
	 * @throws HibernateException
	 */
	public static ChemicalCompound getChemicalCompoundByName(Session s,
			String name) throws HibernateException {
		ChemicalCompound theChem = null;
		theChem = (ChemicalCompound) s
				.createQuery(
						"select chem from ChemicalCompound as chem where chem.name = :theName")
				.setString("theName", name).setMaxResults(1).uniqueResult();
		return theChem;
	}

	
	
	/**
	 * Method saveChemicalCompound.
	 * 
	 * @param s
	 *            Session
	 * @param theChemicalCompound
	 *            ChemicalCompound
	 * @throws HibernateException
	 */
	public static void saveChemicalCompound(Session s,
			ChemicalCompound theChemicalCompound) throws HibernateException {
		s.save(theChemicalCompound);

	}

	/**
	 * Method existsChemicalComposition.
	 * 
	 * @param s
	 *            Session
	 * @param toCompare
	 *            List<ChemicalCompound>
	 * @return boolean
	 * @throws HibernateException
	 */
	public static String existsChemicalComposition(Session s,
			Set<ChemicalDrugStrength> toCompare, String compareDrugName)
			throws HibernateException {

		// get a list of all the drugs in the database
		List<Drug> existingDrugs = getAllDrugs(s);

		for (Drug drug : existingDrugs) {
			if (drug.getChemicalDrugStrengths().size() == toCompare.size()) {
				if (drug.getChemicalDrugStrengths().containsAll(toCompare)
						&& (!drug.getName().equals(compareDrugName)))
					return drug.getName();
			}
		}
		return null;
	}

	public static boolean formChemicalComposition(Session s,
			Set<ChemicalDrugStrength> toCompare, String compareDrugName,String form)
			throws HibernateException {

		// get a list of all the drugs in the database
		List<Drug> existingDrugs = getAllDrugs(s);
		for (Drug drug : existingDrugs) {
		
			if (drug.getChemicalDrugStrengths().size() == toCompare.size()) {
				if (drug.getChemicalDrugStrengths().containsAll(toCompare)&& (!drug.getName().equals(compareDrugName))){
					if(drug.getForm().getForm().equals(form)){
					return true;
					}
				}
			}
		}
		return false;
	}
	
	
	// ------- METHODS FOR REGIMEN MANAGER --------------------------

	/**
	 * Method getRegimen.
	 * 
	 * @param session
	 *            Session
	 * @param name
	 *            String
	 * @return Regimen
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static Regimen getRegimen(Session session, String name)
			throws HibernateException {
		Regimen result = null;
		List<Regimen> regList = session.createQuery(
				"from Regimen r where r.regimenName = :name").setString("name",
				name).list();
		if (regList.size() > 0) {
			if (regList.size() > 1) {
				log.warn("There are 2 regimens with the name '" + name
						+ "' in the database. Returning the first only");
			}
			result = regList.get(0);
		}
		return result;
	}

	/**
	 * Method regimenDrugsDuplicated.
	 * 
	 * @param theRegToSave
	 *            Regimen
	 * 
	 * @return boolean
	 * @throws HibernateException
	 */
	public static boolean regimenDrugsDuplicated(Regimen theRegToSave)
			throws HibernateException {
		Iterator<RegimenDrugs> it2 = theRegToSave.getRegimenDrugs().iterator();
		Set<Integer> theDrugSet = new HashSet<Integer>();
		while (it2.hasNext()) {
			RegimenDrugs rd = it2.next();
			theDrugSet.add(rd.getDrug().getId());
		}
		if (theDrugSet.size() < theRegToSave.getRegimenDrugs().size()) {
			log.warn("Duplicates in Drug Group: " + theDrugSet.size() + ","
					+ theRegToSave.getRegimenDrugs().size());
			return true;
		}
		return false;
	}

	/**
	 * Method regimenDrugsIdentical.
	 * 
	 * @param session
	 *            Session
	 * @param theRegToSave
	 *            Regimen
	 * @return boolean
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static boolean regimenDrugsIdentical(Session session,
			Regimen theRegToSave) throws HibernateException {
		List<RegimenDrugs> regDrugs = theRegToSave.getRegimenDrugs();
		Iterator<RegimenDrugs> it2 = regDrugs.iterator();
		Set<Integer> theDrugSet = new HashSet<Integer>();
		while (it2.hasNext()) {
			RegimenDrugs rd = it2.next();
			theDrugSet.add(rd.getDrug().getId());
		}
		List<Regimen> resultList = session.createQuery("from Regimen r ")
				.list();
		Iterator<Regimen> it = resultList.iterator();

		while (it.hasNext()) {
			Regimen theReg = it.next();
			if (theRegToSave.getId() == null || theReg.getId() != theRegToSave.getId()) {
				Iterator<RegimenDrugs> it3 = theReg.getRegimenDrugs()
						.iterator();
				Set<Integer> theExistingDrugSet = new HashSet<Integer>();
				while (it3.hasNext()) {
					RegimenDrugs rd = it3.next();
					theExistingDrugSet.add(rd.getDrug().getId());
				}
				if ((theExistingDrugSet.equals(theDrugSet))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method regimenExists.
	 * 
	 * @param session
	 *            Session
	 * @param regimen
	 *            Regimen
	 * @return boolean
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static boolean regimenExists(Session session, Regimen regimen)
			throws HibernateException {
		List<Regimen> resultList = session.createQuery("from Regimen r ")
				.list();
		for (int i = 0; i < resultList.size(); i++) {
			if (regimen.equals(resultList.get(i))
					&& !regimen.getRegimenName().equalsIgnoreCase(
							resultList.get(i).getRegimenName()))
				return true;
		}
		return false;
	}

	/**
	 * Method regimenNameExists.
	 * 
	 * @param session
	 *            Session
	 * @param name
	 *            String
	 * @return boolean
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static boolean regimenNameExists(Session session, String name)
			throws HibernateException {
		List<Regimen> resultList = session.createQuery(
				"from Regimen r where " + "upper(r.regimenName) = :name")
				.setString("name", name.toUpperCase()).list();
		if (resultList.size() > 0)
			return true;
		return false;
	}

	/**
	 * Method saveRegimen.
	 * 
	 * @param sess
	 *            Session
	 * @param theRegToSave
	 *            Regimen
	 * @throws HibernateException
	 */
	public static void saveRegimen(Session sess, Regimen theRegToSave)
			throws HibernateException {
		sess.save(theRegToSave);

	}

	/**
	 * Returns the information of the prescribed drug
	 * 
	 * @param session
	 *            Session
	 * @param d
	 *            Drug
	 * @param pre
	 *            Prescription
	 * @return PrescribedDrugs
	 * @throws HibernateException
	 */
	public static PrescribedDrugs getPrescribedDrug(Session session, Drug d,
			Prescription pre) throws HibernateException {
		PrescribedDrugs id = null;

		id = (PrescribedDrugs) session.createQuery(
				"select prescribeddrugs from PrescribedDrugs as prescribeddrugs "
						+ "where prescribeddrugs.drug.id = '" + d.getId()
						+ "' and prescribeddrugs.prescription.id = '"
						+ pre.getId() + "'").setMaxResults(1).uniqueResult();

		return id;
	}

	/**
	 * Method getPrescribedDrugForPackagedDrug.
	 * 
	 * @param pack
	 *            Packages
	 * @param drug
	 *            Drug
	 * @return PrescribedDrugs
	 * @throws HibernateException
	 */
	public static PrescribedDrugs getPrescribedDrugForPackagedDrug(
			Packages pack, Drug drug) throws HibernateException {

		Prescription p = pack.getPrescription();

		List<PrescribedDrugs> preDrugs = p.getPrescribedDrugs();

		for (PrescribedDrugs preDrug : preDrugs) {
			if (preDrug.getDrug().equals(drug))
				return preDrug;
		}

		return null;
	}
}
