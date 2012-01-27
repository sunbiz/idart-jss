package org.celllife.idart.database.hibernate;
import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.manager.PatientManager;

import org.celllife.idart.misc.iDARTUtil;
import org.celllife.idart.test.HibernateTest;
import org.hibernate.criterion.Restrictions;
import org.testng.annotations.Test;

/**
 * 
 */
public class PatientAttributeTest extends HibernateTest{

	private static final String ATTRIB_TEST = "attribTest";

	@Test()
	@SuppressWarnings("unchecked")
	public void testPatientAttribute() {
		String name = PatientAttributeInterface.ARV_START_DATE;
		String SQL = "select atype from AttributeType as atype where atype.name = '" + name + "'";
		AttributeType attr = null;
		List<AttributeType> myList = getSession().createQuery(SQL).list();
		if(myList.size()> 0) {
			AttributeType at = myList.get(0);
			attr = at;
		}
		if (attr == null) {
			log.warn("Patient Attribute: ARV Start Date not present in Database. Creating attribute now. - " + new Date().toString());
			PatientManager.addAttributeTypeToDatabase(getSession(), Date.class,
					PatientAttribute.ARV_START_DATE,
					"Date for First ARV package dispensing.");
		}
	    AttributeType at = PatientManager.getAttributeTypeObject(getSession(), PatientAttributeInterface.ARV_START_DATE);
		assertEquals("ARV Start Date in Database", at.getName(), PatientAttributeInterface.ARV_START_DATE);
	}
	
	@SuppressWarnings("unchecked")
	@Test()
	public void testPatientAttribute1() {
		AttributeType type = new AttributeType();
		type.setName("test type");
		type.setDescription("test description");
		type.setDataType(Date.class);
		getSession().save(type);
		getSession().flush();
		List<AttributeType> types = getSession().createCriteria(AttributeType.class).add( Restrictions.eq("name", "test type")).list();
		for (AttributeType attributeType : types) {
			assertEquals("Name != 'test type'", attributeType.getName(),"test type");
			assertEquals("Description != 'test description'", attributeType.getDescription(),"test description");
			assertEquals("dataType != java.util.Date", attributeType.getDataType(),Date.class);
		}
	}
	
	@Test
	public void testPatientAttributeSave() {

		Patient p = PatientManager.getPatient(getSession(), ATTRIB_TEST);

		AttributeType type = new AttributeType();
		type.setName("ARV_START_DATE");
		type.setDescription("Date of first dispensing of an ARV drug package");
		type.setDataType(Date.class);
		getSession().save(type);
		getSession().flush();

		PatientAttribute patt = new PatientAttribute();
		patt.setType(type);
		patt.setValue(new SimpleDateFormat("dd MMM yyyy").format(new Date()));
		patt.setPatient(p);

		p.getAttributes().add(patt);

		PatientManager.savePatient(getSession(), p);
		getSession().flush();

		List<PatientAttribute> pattLst = PatientManager.getPatientAttributes(
				getSession(), p.getId());
		for (PatientAttribute patt1 : pattLst) {
			assertEquals("Test PatientAttribute Type ARV_START_DATE", type
					.getName(), patt1.getType().getName());
			try {
				iDARTUtil.parse(patt1.getType()
						.getDataType(), patt1.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createTestData(){
		startTransaction();
		utils.createPatient(ATTRIB_TEST);
		endTransactionAndCommit();
	}
}
