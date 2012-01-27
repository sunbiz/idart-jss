package model.manager;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.StudyParticipant;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.nomsic.randb.Randb;
import com.nomsic.randb.RandbXml;
import com.nomsic.randb.exception.RandbException;
import com.nomsic.randb.model.Cell;

public class StudyManager {

	protected static final int AUTO_GENERATE_NUM = 10;
	protected static final Integer[] BLOCK_SIZES = new Integer[] { 4, 6 };
	public static final String FEMALES = "FEMALES"; //$NON-NLS-1$
	public static final String MALES = "MALES"; //$NON-NLS-1$

	private static Logger log = Logger.getLogger(StudyManager.class);

	/*package private*/ static Randb randb;

	public static void randomiseStudyParticipant(StudyParticipant studyParticipant) throws HibernateException {
		Cell cell = getRandomizedStudyGroup(studyParticipant);
		if (cell == null) {
			log.error("Null cell returned during randomisation, defaulting to 50/50"); //$NON-NLS-1$
			studyParticipant.setStudyGroup(getRandomGroup());
		} else {
			studyParticipant.setRandCell(cell);
		}
	}
	
	public static void commitRandomization(StudyParticipant studyParticipant){
		Cell cell = studyParticipant.getRandCell();
		String group = (studyParticipant.getPatient().isMale() ? MALES : FEMALES); //$NON-NLS-1$ //$NON-NLS-2$
		
		if (randb != null) {
			try {
				randb.markAsUsed(group, cell);
			} catch (RandbException e) {
				log.error("Failed to commit randomization: " + cell.getUuid(),e); //$NON-NLS-1$
			}
		}
	}

	/*package private*/ static Cell getRandomizedStudyGroup(
			StudyParticipant studyParticipant) {
		initRandb();

		char sex = studyParticipant.getPatient().getSex();
		String group = (studyParticipant.getPatient().isMale() ? MALES : FEMALES); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			log.debug("Ranomizing participant: group= " + group + " sex=" + sex + " patId=" + studyParticipant.getPatient().getId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			Cell nextCell = randb.getNextCell(group);
			return nextCell;
		} catch (RandbException e) {
			log.error("Failed to randomize participant from group:" + group,e); //$NON-NLS-1$ 
			return null;
		}
	}

	private static void initRandb() {
		if (randb == null) {
			randb = new RandbXml(iDartProperties.randbDataFolder);
			randb.setAutogenerateNum(AUTO_GENERATE_NUM);
			if (!randb.blockGroupExists(MALES)) {
				try {
					randb.createBlockGroup(MALES, 20, Arrays.asList(BLOCK_SIZES), 
							Arrays.asList(StudyParticipant.GROUPS));
				} catch (RandbException e) {
					log.error("Failed to create study group for " + MALES,e); //$NON-NLS-1$ 
				}
			}

			if (!randb.blockGroupExists(FEMALES)) {
				try {
					randb.createBlockGroup(FEMALES, 20, Arrays.asList(BLOCK_SIZES), 
							Arrays.asList(StudyParticipant.GROUPS));
				} catch (RandbException e) {
					log.error("Failed to create study group for " + FEMALES,e); //$NON-NLS-1$ 
				}
			}
		}
	}

	private static String getRandomGroup() {
		if (RandomUtils.nextInt(10) < 5) {
			return StudyParticipant.GP_ACTIVE;
		} else {
			return StudyParticipant.GP_CONTROL;
		}
	}

	/**
	 * This method does not delete the studyparticipant record. It only set's
	 * the end date to today.
	 * 
	 * @param session
	 * @param participantId
	 * @throws HibernateException
	 */
	public static void removeStudyParticipant(Session session, int participantId)
			throws HibernateException {
		Date enddate = new Date();
		@SuppressWarnings("unused")
		int result = session
				.createQuery(
						"Update StudyParticipant set enddate = :endDate where patient = :participantId") //$NON-NLS-1$ 
				.setDate("endDate", enddate)
				.setInteger("participantId", participantId).executeUpdate(); //$NON-NLS-1$ 
	}

	public static Date getStudyEnrolmentDate(Session session, int patientId) {
		StudyParticipant participant = (StudyParticipant) session
				.createQuery("from StudyParticipant where patient = :patientId and endDate is null") //$NON-NLS-1$ 
				.setInteger("patientId", patientId).uniqueResult(); //$NON-NLS-1$
		return participant == null ? null : participant.getStartDate();
	}
	
	public static StudyParticipant getActiveStudyParticipant(Session session, int patientId)
			throws HibernateException {
		StudyParticipant participant = (StudyParticipant) session.createQuery("from StudyParticipant where patient = :id and enddate is null") //$NON-NLS-1$ 
				.setInteger("id", patientId).uniqueResult(); //$NON-NLS-1$ 
		return participant;
	}

	@SuppressWarnings({ "cast", "unchecked" })
	public static boolean isPatientonStudy(Session session, int id)
			throws HibernateException {
		List<StudyParticipant> participants = (List<StudyParticipant>) session
				.createQuery(
						"from StudyParticipant where patient = :id and enddate IS NULL") //$NON-NLS-1$ 
				.setInteger("id", id).list(); //$NON-NLS-1$ 
		if (participants.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method will check if the patient has ever been added to a study,
	 * active or inactive. This will then assume that a contact for the patient
	 * was created on mobilisr.
	 * 
	 * @return
	 * @throws HibernateException
	 */
	@SuppressWarnings({ "cast", "unchecked" })
	public static boolean patientEverOnStudy(Session session, int id)
			throws HibernateException {
		List<StudyParticipant> participants = (List<StudyParticipant>) session
				.createQuery("from StudyParticipant where patient = :id") //$NON-NLS-1$ 
				.setInteger("id", id).list(); //$NON-NLS-1$ 
		if (participants.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
