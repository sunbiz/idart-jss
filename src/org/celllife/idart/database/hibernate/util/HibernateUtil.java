/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.celllife.idart.database.hibernate.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.AccumulatedDrugs;
import org.celllife.idart.database.hibernate.Alerts;
import org.celllife.idart.database.hibernate.AlternatePatientIdentifier;
import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.AtcCode;
import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.Campaign;
import org.celllife.idart.database.hibernate.CampaignParticipant;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Doctor;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Form;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.Logging;
import org.celllife.idart.database.hibernate.MessageSchedule;
import org.celllife.idart.database.hibernate.NationalClinics;
import org.celllife.idart.database.hibernate.PackagedDrugs;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.PatientStatTypes;
import org.celllife.idart.database.hibernate.PatientStatistic;
import org.celllife.idart.database.hibernate.PatientVisit;
import org.celllife.idart.database.hibernate.PatientVisitReason;
import org.celllife.idart.database.hibernate.PillCount;
import org.celllife.idart.database.hibernate.Pregnancy;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.celllife.idart.database.hibernate.SimpleDomain;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockAdjustment;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.StockTake;
import org.celllife.idart.database.hibernate.Study;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.database.hibernate.User;
import org.celllife.idart.database.hibernate.tmp.AdherenceRecord;
import org.celllife.idart.database.hibernate.tmp.DeletedItem;
import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Created on 2005/03/10
 * 
 * Hibernate Helper Class to allow easy access to Hibernate Sessions
 * 
 */
public class HibernateUtil {
	private static Log log = LogFactory.getLog(HibernateUtil.class);

	private static HibernateUtil util;

	private final SessionFactory sessionFactory;

	private static boolean validate = false;

	private HibernateUtil(String hibernateConnectionUrl,
			String hibernatePassword, String hibernateUsername,
			String hibernateDriver, String hibernateDialect) {
		// Create the session factory
		AnnotationConfiguration ac = new AnnotationConfiguration();

		if (validate)
			ac.setProperty("hibernate.hbm2ddl.auto", "validate");
		ac.setProperty("hibernate.show_sql", "false");
		ac.setProperty("hibernate.use_outer_join", "false");
		ac.setProperty("hibernate.cache.provider_class",
		"org.hibernate.cache.HashtableCacheProvider");
		ac.setProperty("hibernate.bytecode.use_reflection_optimizer", "false");
		ac.setProperty("hibernate.transaction.factory_class",
		"org.hibernate.transaction.JDBCTransactionFactory");
		ac.setProperty("hibernate.max_fetch_depth", "1");
		ac.setProperty("hibernate.default_batch_fetch_size", "4");
		ac.setProperty("hibernate.use_sql_comments", "false");
		ac.setProperty("connection.autocommit", "true");
		ac.setProperty("hibernate.connection.url", hibernateConnectionUrl);
		ac.setProperty("hibernate.connection.password", hibernatePassword);
		ac.setProperty("hibernate.connection.username", hibernateUsername);
		ac.setProperty("hibernate.connection.driver_class", hibernateDriver);
		ac.setProperty("hibernate.dialect", hibernateDialect);
		// ac.setProperty("hibernate.hbm2ddl.auto", "validate");
		// deprecated integer return types
		// ac.addSqlFunction("count", new ClassicCountFunction());
		// ac.addSqlFunction("avg", new ClassicAvgFunction());
		// ac.addSqlFunction("sum", new ClassicSumFunction());
		ac.addAnnotatedClass(AccumulatedDrugs.class);
		ac.addAnnotatedClass(AlternatePatientIdentifier.class);
		ac.addAnnotatedClass(Appointment.class);
		ac.addAnnotatedClass(ChemicalCompound.class);
		ac.addAnnotatedClass(ChemicalDrugStrength.class);
		ac.addAnnotatedClass(Clinic.class);
		ac.addAnnotatedClass(Doctor.class);
		ac.addAnnotatedClass(Drug.class);
		ac.addAnnotatedClass(Episode.class);
		ac.addAnnotatedClass(Form.class);
		ac.addAnnotatedClass(Logging.class);
		ac.addAnnotatedClass(PackagedDrugs.class);
		ac.addAnnotatedClass(Packages.class);
		ac.addAnnotatedClass(Patient.class);
		ac.addAnnotatedClass(PatientAttribute.class);
		ac.addAnnotatedClass(AttributeType.class);
		ac.addAnnotatedClass(StockCenter.class);
		ac.addAnnotatedClass(PillCount.class);
		ac.addAnnotatedClass(Pregnancy.class);
		ac.addAnnotatedClass(PrescribedDrugs.class);
		ac.addAnnotatedClass(Prescription.class);
		ac.addAnnotatedClass(RegimenDrugs.class);
		ac.addAnnotatedClass(Regimen.class);
		ac.addAnnotatedClass(SimpleDomain.class);
		ac.addAnnotatedClass(StockAdjustment.class);
		ac.addAnnotatedClass(Stock.class);
		ac.addAnnotatedClass(StockLevel.class);
		ac.addAnnotatedClass(StockTake.class);
		ac.addAnnotatedClass(User.class);
		ac.addAnnotatedClass(AdherenceRecord.class);
		ac.addAnnotatedClass(DeletedItem.class);
		ac.addAnnotatedClass(PackageDrugInfo.class);
		ac.addAnnotatedClass(Packages.class);
		ac.addAnnotatedClass(PatientStatistic.class);
		ac.addAnnotatedClass(PatientStatTypes.class);
		ac.addAnnotatedClass(PatientVisit.class);
		ac.addAnnotatedClass(PatientVisitReason.class);
		ac.addAnnotatedClass(NationalClinics.class);
		ac.addAnnotatedClass(Study.class);
		ac.addAnnotatedClass(StudyParticipant.class);
		ac.addAnnotatedClass(Campaign.class);
		ac.addAnnotatedClass(CampaignParticipant.class);
		ac.addAnnotatedClass(MessageSchedule.class);
		ac.addAnnotatedClass(Alerts.class);
		ac.addAnnotatedClass(IdentifierType.class);
		ac.addAnnotatedClass(PatientIdentifier.class);
		ac.addAnnotatedClass(AtcCode.class);
		sessionFactory = ac.buildSessionFactory();
	}

	/**
	 * @return a new session from the session factory
	 */
	public static Session getNewSession() {
		if (util == null) {
			log.info("Initialising HibernateUtil.");
			rebuildUtil();
		}

		return util.getSession();
	}

	public static void rebuildUtil() {
		util = new HibernateUtil(iDartProperties.hibernateConnectionUrl,
				iDartProperties.hibernatePassword,
				iDartProperties.hibernateUsername,
				iDartProperties.hibernateDriver,
				iDartProperties.hibernateDialect);
	}

	public Session getSession() {
		Session sess = sessionFactory.openSession();
		sess.setFlushMode(FlushMode.COMMIT);
		return sess;
	}

	public static HibernateUtil buildNewUtil(String host, String database) {
		HibernateUtil hUtil = new HibernateUtil("jdbc:postgresql://" + host
				+ ":5432/"
				+ database, iDartProperties.hibernatePassword,
				iDartProperties.hibernateUsername,
				iDartProperties.hibernateDriver,
				iDartProperties.hibernateDialect);

		return hUtil;
	}
	
	public static void setValidation(boolean validation){
		validate = validation;
		rebuildUtil();
	}
}
