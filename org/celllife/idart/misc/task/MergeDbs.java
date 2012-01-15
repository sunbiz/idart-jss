package org.celllife.idart.misc.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.PatientManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.AlternatePatientIdentifier;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.Pregnancy;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class MergeDbs implements IdartTask {

	private static Logger log = Logger.getLogger(MergeDbs.class);

	private HibernateUtil hibernateUtil;
	private Session leftSession;

	@Override
	public String getDescription() {
		return "Merge patients from secondary database into primary database.";
	}

	@Override
	public String getHelpText() {
		return "Expected arguments:\n" + "	1: hostname of secondary database\n"
		+ "	2: name of secondary database\n" + "\n"
				+ "e.g. merge localhost idart2";
	}

	@Override
	public boolean init(String[] args) {
		try {
			hibernateUtil = HibernateUtil.buildNewUtil(args[0], args[1]);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(IProgressMonitor monitor) throws TaskException {
		leftSession = getLeftSession();
		Transaction tx = leftSession.beginTransaction();

		try {
			List<String> patids = leftSession.createQuery(
			"select p.patientId from Patient p").list();

			List<Patient> missingPts = getRightSession().createCriteria(
					Patient.class).add(
							Restrictions.not(Restrictions.in("patientId", patids)))
							.list();

			process: for (Patient patient : missingPts) {
				Integer id = null;
				patient.setId(id);
				if (patient.getAlternateIdentifiers().size() > 0) {
					for (AlternatePatientIdentifier aid : patient
							.getAlternateIdentifiers()) {
						if (patids.contains(aid.getIdentifier())) {
							continue process;
						}
					}
				}

				// FIXME: (simon - multi ids) merge db's - cater for multiple patient identifiers
				List<Patient> patientsByAltId = PatientManager
					.getPatientsByAltId(leftSession, null, patient.getPatientId());
				if (patientsByAltId.size() > 0) {
					continue process;
				}

				log.info("Processing patient: " + patient.getPatientId());
				Set<PatientAttribute> attributes = patient.getAttributes();
				patient.setAttributes(new HashSet<PatientAttribute>());
				for (PatientAttribute pa : attributes) {
					pa.setId(id);
					patient.getAttributes().add(pa);
				}

				Set<AlternatePatientIdentifier> ais = patient
				.getAlternateIdentifiers();
				patient
				.setAlternateIdentifiers(new HashSet<AlternatePatientIdentifier>());
				for (AlternatePatientIdentifier ai : ais) {
					ai.setId(id);
					patient.getAlternateIdentifiers().add(ai);
				}

				Set<Appointment> appointments = patient.getAppointments();
				patient.setAppointments(new HashSet<Appointment>());
				for (Appointment app : appointments) {
					app.setId(id);
					patient.getAppointments().add(app);
				}

				List<Episode> episodes = patient.getEpisodes();
				patient.setEpisodes(new ArrayList<Episode>());
				for (Episode episode : episodes) {
					episode.setId(id);
					patient.getEpisodes().add(episode);
				}

				Set<Pregnancy> pregnancies = patient.getPregnancies();
				patient.setPregnancies(new HashSet<Pregnancy>());
				for (Pregnancy pregnancy : pregnancies) {
					pregnancy.setId(id);
					patient.getPregnancies().add(pregnancy);
				}

				Set<Prescription> prescriptions = patient.getPrescriptions();
				patient.setPrescriptions(new HashSet<Prescription>());
				for (Prescription pre : prescriptions) {
					pre.setId(id);
					patient.getPrescriptions().add(pre);

					Doctor doctor = AdministrationManager.getDoctor(
							leftSession, pre.getDoctor().getFullname());
					if (doctor == null) {
						doctor = createDoctor(pre.getDoctor());
					}
					pre.setDoctor(doctor);

					List<PrescribedDrugs> prescribedDrugs = pre
					.getPrescribedDrugs();
					pre.setPrescribedDrugs(new ArrayList<PrescribedDrugs>());
					for (PrescribedDrugs preDrug : prescribedDrugs) {
						preDrug.setId(id);
						pre.getPrescribedDrugs().add(preDrug);
					}
					Set<Packages> packages = pre.getPackages();
					pre.setPackages(new HashSet<Packages>());
					for (Packages pack : packages) {
						pack.setId(id);
						pre.getPackages().add(pack);

						Set<AccumulatedDrugs> accumulatedDrugs = pack
						.getAccumulatedDrugs();
						pack
						.setAccumulatedDrugs(new HashSet<AccumulatedDrugs>());
						for (AccumulatedDrugs accum : accumulatedDrugs) {
							accum.setId(id);
							pack.getAccumulatedDrugs().add(accum);
						}

						List<PackagedDrugs> packagedDrugs = pack
						.getPackagedDrugs();
						pack.setPackagedDrugs(new ArrayList<PackagedDrugs>());
						for (PackagedDrugs pdrug : packagedDrugs) {
							pdrug.setId(id);
							pack.getPackagedDrugs().add(pdrug);

							Stock stock = getStock(pdrug);
							pdrug.setStock(stock);
						}

						Set<PillCount> pillCounts = pack.getPillCounts();
						pack.setPillCounts(new HashSet<PillCount>());
						for (PillCount pc : pillCounts) {
							pc.setId(id);
							pack.getPillCounts().add(pc);
						}
					}
				}
				leftSession.save(patient);
				leftSession.flush();
				log.info("Patient saved: " + patient.getPatientId());
			}
		} catch (Exception e) {
			tx.rollback();
		} finally {
			tx.commit();
			leftSession.close();
		}
	}

	private Doctor createDoctor(Doctor doctor) {
		log.info("Creating doctor: " + doctor.getFullname());
		doctor.setId(null);
		leftSession.save(doctor);
		leftSession.flush();
		return doctor;
	}

	private Stock getStock(PackagedDrugs pdrug) {
		Stock stock = StockManager.getSoonestExpiringStock(leftSession, pdrug
				.getStock().getDrug(), pdrug.getAmount(),
				getPreferredStockCenter());

		if (stock == null) {
			log.info("Adding stock for drug: "
					+ pdrug.getStock().getDrug().getName());
			stock = new Stock();
			Drug drug = DrugManager.getDrug(leftSession, pdrug.getStock()
					.getDrug().getName());
			if (drug == null) {
				drug = createDrug(pdrug.getStock().getDrug());
			}
			stock.setDrug(drug);
			stock.setBatchNumber("TMP BATCH");
			stock.setDateReceived(new Date());
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 10);
			stock.setExpiryDate(cal.getTime());
			stock.setHasUnitsRemaining('T');
			stock.setManufacturer("");
			stock.setModified('F');
			stock.setStockCenter(getPreferredStockCenter());
			stock.setUnitsReceived(100);
			leftSession.save(stock);
			leftSession.flush();
			StockManager.updateStockLevel(leftSession, stock);
		}
		return stock;
	}

	private Drug createDrug(Drug drug) {
		log.warn("Creating drug: " + drug.getName());
		drug.setId(null);
		Set<ChemicalDrugStrength> chemicalDrugStrengths = drug
		.getChemicalDrugStrengths();
		drug.setChemicalDrugStrengths(new HashSet<ChemicalDrugStrength>());
		for (ChemicalDrugStrength cs : chemicalDrugStrengths) {
			cs.setId(null);
			drug.getChemicalDrugStrengths().add(cs);
		}
		drug.setStock(new HashSet<Stock>());
		leftSession.save(drug);
		leftSession.flush();
		return drug;
	}

	private StockCenter getPreferredStockCenter() {
		return AdministrationManager.getPreferredStockCenter(leftSession);
	}

	public Session getRightSession() {
		return hibernateUtil.getSession();
	}

	public Session getLeftSession() {
		return HibernateUtil.getNewSession();
	}

}
