package model.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.celllife.idart.database.hibernate.Appointment;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.Study;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.test.HibernateTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SmsManagerTest extends HibernateTest {
	
	@Test
	public void testGetContactsDefaulted(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 1);
	}
	
	@Test
	public void testGetContactsDefaulted_multiple_appointments(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -5);
		Appointment appointment = new Appointment(patient, cal.getTime(), new Date());
		getSession().save(appointment);
		appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 1);
	}
	
	@Test(enabled=false)
	public void testGetContactsDefaulted_not_in_active_group(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_CONTROL, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsDefaulted_appointment_has_visitdate(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -5);
		Appointment appointment = new Appointment(patient, cal.getTime(), new Date());
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsDefaulted_appointment_on_diff_day(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -4);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsDefaulted_not_in_study(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsDefaulted_patient_inactive(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(false);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsDefaulted(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsExpected(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 1);
	}
	
	@Test
	public void testGetContactsExpected_multiple_appointments(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 5);
		Appointment appointment = new Appointment(patient, cal.getTime(), new Date());
		getSession().save(appointment);
		appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 1);
	}
	
	@Test(enabled=false)
	public void testGetContactsExpected_not_in_active_group(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_CONTROL, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsExpected_appointment_has_visitdate(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 5);
		Appointment appointment = new Appointment(patient, cal.getTime(), new Date());
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}

	@Test
	public void testGetContactsExpected_patient_inactive(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(false);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsExpected_appointment_on_diff_day(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Study study = AdministrationManager.getCidaStudy(getSession());
		StudyParticipant participant = new StudyParticipant(patient, study, StudyParticipant.GP_ACTIVE, new Date(), null);
		participant.setNetwork("");
		participant.setLanguage("English");
		getSession().save(participant);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 4);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
	
	@Test
	public void testGetContactsExpected_not_in_study(){
		Patient patient = new Patient();
		patient.setPatientId("attribTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');
		patient.setAccountStatus(true);
		getSession().save(patient);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 5);
		Appointment appointment = new Appointment(patient, cal.getTime(), null);
		getSession().save(appointment);
		
		getSession().flush();
		
		List<StudyParticipant> contactsDefaulted = SmsManager.getContactsExpected(getSession(), 5);
		Assert.assertEquals(contactsDefaulted.size(), 0);
	}
}
