package model.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.test.HibernateTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PatientEpisodeTest extends HibernateTest {

	private SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy");
	private Date dateOne;
	private Date dateTwo;
	private Date dateThree;

	@BeforeClass
	public void setup() throws ParseException {
		dateOne = df.parse("01-01-2007");
		dateTwo = df.parse("02-01-2007");
		dateThree = df.parse("03-01-2007");
	}

	@Test()
	public void testInsertPatientEpisodeAccordingToDate() {
		/*
		 * Expecting patient that has two episodes, one with startDate = dateOne
		 * and the other with startDate = dateThree.
		 * 
		 * Will attempt to add an episode with a startDate = dateTwo and verify
		 * that it is inserted in between the other two.
		 */
		Patient p = PatientManager.getPatient(getSession(), "0000");
		Episode e2 = new Episode();
		e2.setStartDate(dateTwo);
		e2.setStartReason("transfer in");
		PatientManager.insertPatientEpisodeAccordingToDate(p, e2);

		List<Episode> episodes = p.getEpisodes();
		assertTrue("Wrong number of episodes", episodes.size() == 3);
		assertTrue("Episode not inserted correctly", episodes.get(0)
				.getStartDate().compareTo(dateOne) == 0);
		assertTrue("Episode not inserted correctly", episodes.get(1)
				.getStartDate().compareTo(dateTwo) == 0);
		assertTrue("Episode not inserted correctly", episodes.get(2)
				.getStartDate().compareTo(dateThree) == 0);

	}

	@Test()
	public void testHasPreviousEpisodes() {
		/*
		 * Expecting a patient that has one or more CLOSED episodes.
		 */
		Patient p2 = PatientManager.getPatient(getSession(), "0001");
		assertTrue(PatientManager.hasPreviousEpisodes(p2));
	}

	@Test()
	public void testHasNoPreviousEpisodes() {
		/*
		 * Expecting patient that has no CLOSED episodes.
		 */
		Patient p2 = PatientManager.getPatient(getSession(), "0002");
		assertFalse(PatientManager.hasPreviousEpisodes(p2));
	}

	/**
	 * 
	 * @param None
	 * @return None
	 */
	@Test()
	public void testEpisodes() {
		Patient p2 = PatientManager.getPatient(getSession(), "0003");
		Episode e = PatientManager.getMostRecentEpisode(p2);
		assertFalse("Episode is open", e.isOpen());
		assertTrue("Episode stop date not as expected", e.getStopDate().compareTo(dateTwo) == 0);
		assertTrue("Episode stop reason not as expected",e.getStopReason().equals("Transfer out"));
	}

	/**
	 * 
	 * @param None
	 * @return None
	 */
	@Test()
	public void testMultipleEpisodes() {
		Patient p2 = PatientManager.getPatient(getSession(), "0004");
		assertTrue(p2.getEpisodes().size() > 1);
		assertTrue(PatientManager.getMostRecentEpisode(p2).isOpen());
		assertTrue(PatientManager.getMostRecentEpisode(p2).getStartDate()
				.compareTo(dateThree) == 0);
		assertTrue(PatientManager.getMostRecentEpisode(p2).getStartReason()
				.equalsIgnoreCase("transfer in"));
	}

	public void createData() throws Exception {
		setup();
		startTransaction();

		createDataInsertPatientEpisodeAccordingToDate();
		createDataHasPreviousEpisodesData();
		createDataHasNoPreviousEpisodes();
		createDataEpisodes();
		createDataMultipleEpisodes();

		getSession().flush();
		endTransactionAndCommit();
	}

	private void createDataHasNoPreviousEpisodes() {
		Patient p = utils.createPatient("0002");
		Episode e2 = new Episode();
		e2.setStartDate(new Date());
		e2.setClinic(p.getCurrentClinic());
		e2.setStartReason("transfer in");
		PatientManager.addEpisodeToPatient(p, e2);

	}

	private void createDataInsertPatientEpisodeAccordingToDate() {
		Patient p = utils.createPatient("0000");

		Episode e1 = new Episode();
		e1.setStartDate(dateOne);
		e1.setClinic(p.getCurrentClinic());
		e1.setStartReason("transfer in");
		PatientManager.addEpisodeToPatient(p, e1);

		Episode e3 = new Episode();
		e3.setStartDate(dateThree);
		e3.setClinic(p.getCurrentClinic());
		e3.setStartReason("transfer in");
		PatientManager.addEpisodeToPatient(p, e3);
	}

	private void createDataHasPreviousEpisodesData() {
		Patient p = utils.createPatient("0001");
		Date date = new Date();
		Episode e1 = new Episode();
		e1.setStartDate(date);
		e1.setStartReason("transfer in");
		e1.setStartNotes("e1");
		e1.setStopDate(date);
		e1.setStopReason("Transfer out");
		e1.setClinic(p.getCurrentClinic());

		date = new Date();
		Episode e2 = new Episode();
		e2.setStartDate(date);
		e2.setStartNotes("e2");
		e2.setStartReason("transfer in");
		e2.setStopDate(date);
		e2.setStopReason("Transfer out");
		e2.setClinic(p.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p, e1);
		PatientManager.addEpisodeToPatient(p, e2);
	}

	private void createDataEpisodes() {
		Patient p = utils.createPatient("0003");
		Episode episode = new Episode();
		episode.setStartDate(dateOne);
		episode.setStartReason("transfer in");
		episode.setStopDate(dateTwo);
		episode.setStopReason("Transfer out");
		episode.setClinic(p.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p, episode);
	}

	private void createDataMultipleEpisodes() {
		Patient p = utils.createPatient("0004");
		Episode episode = new Episode();
		episode.setStartDate(dateOne);
		episode.setStartReason("transfer in");
		episode.setStopDate(dateTwo);
		episode.setStopReason("Transfer out");
		episode.setClinic(p.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p, episode);

		Episode e2 = new Episode();
		e2.setStartDate(dateThree);
		e2.setStartReason("transfer in");
		e2.setClinic(p.getCurrentClinic());
		PatientManager.addEpisodeToPatient(p, e2);
	}

}
