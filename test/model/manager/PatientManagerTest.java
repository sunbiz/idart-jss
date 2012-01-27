package model.manager;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.IdentifierType;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientAttributeInterface;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.test.HibernateTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PatientManagerTest extends HibernateTest {
	
	private Patient patient;

	@BeforeMethod
	public void setup(){
		List<AttributeType> types = PatientManager.getAllAttributeTypes(getSession());
		for (AttributeType type : types) {
			getSession().delete(type);
		}
		
		getSession().createQuery("delete from PatientAttribute").executeUpdate();
		
		patient = new Patient();
		patient.setPatientId("managerTest");
		patient.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient.setSex('M');
		patient.setModified('T');

		IdentifierType type = new IdentifierType("type1",1);
		getSession().save(type);
		
		PatientIdentifier e = new PatientIdentifier();
		e.setType(type);
		e.setPatient(patient);
		e.setValue("attribTest");
		patient.getPatientIdentifiers().add(e);
		getSession().save(patient);
		
		getSession().flush();
	}
	
	@Test
	public void testGetPatient(){
		Patient patient2 = PatientManager.getPatient(getSession(), patient.getPatientId());
		Assert.assertNotNull(patient2);
		Assert.assertEquals(patient.getId(), patient2.getId());
	}
	
	@Test
	public void testGetPatient_multipleIdentifiers(){
		Patient patient2 = new Patient();
		patient2.setPatientId("attribTest1");
		patient2.setClinic(AdministrationManager.getMainClinic(getSession()));
		patient2.setSex('M');
		patient2.setModified('T');
		PatientIdentifier e = new PatientIdentifier();
		e.setType(new IdentifierType("type2",2));
		e.setPatient(patient2);
		e.setValue("attribTest");
		patient2.getPatientIdentifiers().add(e);
		getSession().save(patient2);
		
		Patient search = PatientManager.getPatient(getSession(), patient.getPatientId());
		Assert.assertNotNull(search);
		Assert.assertEquals(patient.getId(), search.getId());
	}
	
	@Test
	public void testGetPatientsWithAttribute(){
		
		AttributeType t2 = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, PatientAttributeInterface.ARV_START_DATE, "");
		PatientAttribute att2 = new PatientAttribute();
		att2.setPatient(patient);
		att2.setType(t2);
		getSession().save(att2);
		getSession().flush();
		
		List<Integer> list = PatientManager.getPatientsWithAttribute(getSession(), PatientAttributeInterface.ARV_START_DATE);
		Assert.assertTrue(!list.isEmpty());
		Assert.assertEquals(list.get(0).intValue(), patient.getId());
	}
	
	@Test
	public void testCheckPatientAttributes_noCorrectType(){
		PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv start date", "");
		PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "ARV_start Date", "");
		PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv START date", "");
		getSession().flush();
		
		AttributeType firstType = PatientManager.getAllAttributeTypes(getSession()).get(0);
		
		PatientManager.checkPatientAttributes(getSession());
		getSession().flush();
		
		List<AttributeType> types = PatientManager.getAllAttributeTypes(getSession());
		Assert.assertEquals(types.size(),1);
		Assert.assertEquals(types.get(0).getName(), firstType.getName());
	}
	
	@Test
	public void testCheckPatientAttributes_oneCorrectType(){
		PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv start date", "");
		PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "ARV_start Date", "");
		PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv START date", "");
		AttributeType correct = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, PatientAttribute.ARV_START_DATE, "");
		getSession().flush();
		
		
		PatientManager.checkPatientAttributes(getSession());
		getSession().flush();
		
		List<AttributeType> types = PatientManager.getAllAttributeTypes(getSession());
		Assert.assertEquals(types.size(),1);
		Assert.assertEquals(types.get(0).getName(), correct.getName());
	}
	
	@Test
	public void testCheckPatientAttributes_withPatientAttributes_noCorrectType(){
		AttributeType t1 = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv start date", "");
		PatientAttribute att1 = new PatientAttribute();
		att1.setPatient(patient);
		att1.setType(t1);
		getSession().save(att1);
		
		AttributeType t2 = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "ARV_start Date", "");
		PatientAttribute att2 = new PatientAttribute();
		att1.setPatient(patient);
		att1.setType(t2);
		getSession().save(att2);
		
		AttributeType t3 = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv START date", "");
		PatientAttribute att3 = new PatientAttribute();
		att1.setPatient(patient);
		att1.setType(t3);
		getSession().save(att3);
		getSession().flush();
		
		AttributeType correct = PatientManager.getAllAttributeTypes(getSession()).get(0);
		
		PatientManager.checkPatientAttributes(getSession());
		getSession().flush();
		
		List<AttributeType> types = PatientManager.getAllAttributeTypes(getSession());
		Assert.assertEquals(types.size(),1);
		Assert.assertEquals(types.get(0).getName(), correct.getName());
		
		Patient p = PatientManager.getPatient(getSession(), patient.getPatientId());
		Set<PatientAttribute> attributes = p.getAttributes();
		for (PatientAttribute pa : attributes) {
			Assert.assertEquals(pa.getType().getName(), correct.getName());
		}
	}
	
	@Test
	public void testCheckPatientAttributes_withPatientAttributes_withCorrectType(){
		AttributeType t1 = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv start date", "");
		PatientAttribute att1 = new PatientAttribute();
		att1.setPatient(patient);
		att1.setType(t1);
		getSession().save(att1);
		
		AttributeType correct = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, PatientAttribute.ARV_START_DATE, "");
		PatientAttribute att2 = new PatientAttribute();
		att1.setPatient(patient);
		att1.setType(correct);
		getSession().save(att2);
		
		AttributeType t3 = PatientManager.addAttributeTypeToDatabase(getSession(), Date.class, "arv START date", "");
		PatientAttribute att3 = new PatientAttribute();
		att1.setPatient(patient);
		att1.setType(t3);
		getSession().save(att3);
		getSession().flush();
		
		PatientManager.checkPatientAttributes(getSession());
		getSession().flush();
		
		List<AttributeType> types = PatientManager.getAllAttributeTypes(getSession());
		Assert.assertEquals(types.size(),1);
		Assert.assertEquals(types.get(0).getName(), correct.getName());
		
		Patient p = PatientManager.getPatient(getSession(), patient.getPatientId());
		Set<PatientAttribute> attributes = p.getAttributes();
		for (PatientAttribute pa : attributes) {
			Assert.assertEquals(pa.getType().getName(), correct.getName());
		}
	}

}
