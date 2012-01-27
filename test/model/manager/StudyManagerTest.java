package model.manager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.celllife.idart.test.IDARTtest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.nomsic.randb.RandbXml;
import com.nomsic.randb.model.Block;
import com.nomsic.randb.model.BlockGroup;
import com.nomsic.randb.model.Cell;

public class StudyManagerTest extends IDARTtest{
	
	@BeforeMethod
	public void setup() throws IOException{
		FileUtils.deleteDirectory(new File(iDartProperties.randbDataFolder));
		StudyManager.randb = null;
	}
	
	@Test
	public void testRandomization(){
		Patient patient = new Patient();
		patient.setSex('M');
		StudyParticipant sp = new StudyParticipant();
		sp.setPatient(patient);
		
		Cell cell = StudyManager.getRandomizedStudyGroup(sp);
		Assert.assertNotNull(cell);
	}
	
	@Test
	public void testRandomization_bulk(){
		StudyParticipant sp = new StudyParticipant();
		String[] gender = genderProvider();
		int male = 0;
		int female = 0;
		for (String string : gender) {
			Patient patient = new Patient();
			patient.setSex(string.charAt(0));
			if(patient.isMale()){
				male++;
			} else {
				female++;
			}
			sp.setPatient(patient);
			
			Cell cell = StudyManager.getRandomizedStudyGroup(sp);
			Assert.assertNotNull(cell);
			sp.setRandCell(cell);
			StudyManager.commitRandomization(sp);
			
			if ((male+female)%100 == 0){
				// simulate logging out and in
				StudyManager.randb = null;
			}
		}	
		RandbXml randb = new RandbXml(iDartProperties.randbDataFolder);
		BlockGroup mgp = randb.getBlockGroup(StudyManager.MALES);
		int msize = 0;
		for (Block b : mgp.getBlocks()){
			msize += b.size();
		}
		
		Integer maxBlockSize = Collections.max(Arrays.asList(StudyManager.BLOCK_SIZES));
		int tollerance = StudyManager.AUTO_GENERATE_NUM*maxBlockSize;
		Assert.assertTrue(male <= msize && msize <= male + tollerance);
		
		BlockGroup fgp = randb.getBlockGroup(StudyManager.FEMALES);
		int fsize = 0;
		for (Block b : fgp.getBlocks()){
			fsize += b.size();
		}
		Assert.assertTrue(female <= fsize && fsize <= female + tollerance);
	}
	
	public String[] genderProvider(){
		Random random = new Random();
		String[] o = new String[1000];
		for (int i = 0; i < o.length; i++) {
			o[i] =  random.nextInt(10) > 5 ? "M" : "F";
		}
		return o;
	}

}
