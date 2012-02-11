package model.manager.exports.iedea;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.PackageManager;
import model.manager.PatientManager;

import org.celllife.idart.database.hibernate.AtcCode;
import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.Episode;
import org.celllife.idart.database.hibernate.Packages;
import org.celllife.idart.database.hibernate.Patient;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.database.hibernate.PatientAttributeInterface;
import org.celllife.idart.database.hibernate.PatientIdentifier;
import org.celllife.idart.database.hibernate.PrescribedDrugs;
import org.celllife.idart.database.hibernate.Prescription;
import org.celllife.idart.misc.iDARTUtil;
import org.hibernate.Session;
import org.iedea.ARKEKapaExport;
import org.iedea.ARKEKapaExport.ART;
import org.iedea.ARKEKapaExport.DEM;
import org.iedea.ARKEKapaExport.PAT;
import org.iedea.ARKEKapaExport.VIS;

public class IedeaExport {

	private Session session;
	private ARKEKapaExport export;

	public IedeaExport(ARKEKapaExport export) {
		this.export = export;
	}

	public void writeDataExport(Session sess, Patient patient) {
		this.session = sess;

		populatePAT(patient);
		populateDEM(patient);
		populateVIS(patient);
		populateART(patient);
	}

	private void populatePAT(Patient patient) {
		PAT pat = new PAT();
		pat.setPATIENT(String.valueOf(patient.getId()));
		pat.setFACILITY(patient.getCurrentClinic().getClinicName());
		Date dob = patient.getDateOfBirth();
		pat.setBIRTHDMY(dob);
		pat.setBIRTHY(getYear(dob));
		pat.setBIRTHM(getMonth(dob));
		char sex = patient.getSex();
		pat.setGENDER(convertSex(sex));
		Episode firstEpisode = patient.getEpisodes().get(0);
		pat.setFRSVISDMY(firstEpisode.getStartDate());
		pat.setENTRY(95);
		pat.setMODE(95);
		pat.setHIVTYPE(95);
		pat.setHIVTEST(95);
		PatientAttribute arvAtt = patient.getAttributeByName(PatientAttributeInterface.ARV_START_DATE);
		Date arvStartDate = null;
		if (arvAtt != null)
			arvStartDate = (Date) arvAtt.getObjectValue();
		
		pat.setHAART(arvStartDate == null ? 0 : 1);
		pat.setHAARTDMY(arvStartDate);
		pat.setFHVSTAGEWHO(95);
		pat.setFHVSDI1("95");
		pat.setFHVSDI2("95");
		pat.setFHVSDI3("95");
		pat.setFHVSDI4("95");
		pat.setEXPY(95);
		pat.setMTCTY(95);
		pat.setPEPY(95);
		pat.setTBFHV(95);
		pat.setTBFHV(95);
		pat.setWKSTBFHV(95);
		
		if (sex == 'M' || arvStartDate == null)
			pat.setPREGFHV(88);
		else if (iDARTUtil.getAgeAt(dob, arvStartDate) < 10)
			pat.setPREGFHV(88);
		else {
			boolean pregnantAtDate = patient.isPregnantAtDate(arvStartDate);
			pat.setPREGFHV(pregnantAtDate ? 1 : 0);
		}
		
		if (arvStartDate == null) {
			pat.setMETHODINTOART(88);
			pat.setFROMLOCATION("88");
		} else if (arvStartDate.before(firstEpisode.getStartDate())
				|| firstEpisode.getStartReason().equalsIgnoreCase("Transferred In")){
			// patient was transferred in on ARV's
			pat.setMETHODINTOART(1);
			pat.setTRANSFERINDMY(firstEpisode.getStartDate());
			pat.setFROMLOCATION("95");
		} else {
			pat.setMETHODINTOART(0);
			pat.setFROMLOCATION("88");
		}
		
		Packages mostRecentPack = PackageManager.getMostRecentCollectedPackage(session, patient);
		if (mostRecentPack != null){
			pat.setLASTCONTACTDMY(mostRecentPack.getPickupDate());
			pat.setLASTCONTACTT(95);
		}
		
		Episode mostRecentEpisode = patient.getMostRecentEpisode();
		if (!mostRecentEpisode.isOpen()){
			if (Episode.REASON_DECEASED.equalsIgnoreCase(mostRecentEpisode.getStopReason())){
				pat.setOUTCOME(11);
			} else if ("Transferred Out".equalsIgnoreCase(mostRecentEpisode.getStopReason())){
				pat.setOUTCOME(31);
			} else if ("Lost to Follow Up".equalsIgnoreCase(mostRecentEpisode.getStopReason())){
				pat.setOUTCOME(31);
			} else {
				pat.setOUTCOME(90);
			}
			Date stopDate = mostRecentEpisode.getStopDate();
			pat.setOUTCOMEDMY(stopDate);
			pat.setOUTCOMEY(8888);
			pat.setOUTCOMEM(88);
		} else {
			pat.setOUTCOME(23);
		}
		
		if (Episode.REASON_DECEASED.equalsIgnoreCase(mostRecentEpisode.getStopReason())){
			pat.setDEATHC1(95d);
			pat.setDEATHC2(95d);
			pat.setDEATHC3(95d);
			pat.setDEATHN1("N");
			pat.setDEATHN2("N");
			pat.setDEATHN3("N");
		} else {
			pat.setDEATHC1(88d);
			pat.setDEATHC2(88d);
			pat.setDEATHC3(88d);
		}
		
		boolean isPead = isPead(patient);
		pat.setCAREG(isPead ? 95 : 88);
		pat.setDISCLCG(isPead ? 95 : 88);
		pat.setDISCLCHILD(isPead ? 95 : 88);
		pat.setWEIGHTBIRTH(isPead ? 95 : 88);
		pat.setBRSTFD(isPead ? 95 : 88);
		pat.setBRSTFDESTDUR(isPead ? 95 : 88);
		
		export.getTables().add(pat);
	}
	
	private void populateDEM(Patient patient) {
		DEM dem = new DEM();
		dem.setPATIENT(String.valueOf(patient.getId()));
		
		List<PatientIdentifier> identifiers = new ArrayList<PatientIdentifier>(patient.getPatientIdentifiers());
		Collections.sort(identifiers, new Comparator<PatientIdentifier>() {
			@Override
			public int compare(PatientIdentifier o1, PatientIdentifier o2) {
				return o1.getType().getIndex() - o2.getType().getIndex();
			}
		});
		
		dem.setFOLDERNUMBER(identifiers.get(0).getValue());
		if (identifiers.size() > 1)
			dem.setFOLDERNUMBER(identifiers.get(1).getValue());
		
		dem.setSURNAME(patient.getLastname());
		dem.setFIRSTNAME(patient.getFirstNames());
		dem.setHOMEADDRESS1(patient.getAddress1());
		dem.setHOMEADDRESS2(patient.getAddress2());
		dem.setHOMEADDRESS3(patient.getAddress3());
		dem.setHOMEADDRESS4(patient.getProvince());
		dem.setRACE(patient.getRace());
		dem.setCELLNUMBER(patient.getCellphone());
		
		export.getTables().add(dem);
	}
	
	private void populateVIS(Patient patient) {
		List<Packages> packages = PackageManager.getAllCollectedPackagesForPatient(session, patient);
		for (Packages pack : packages) {
			VIS vis = new VIS();
			vis.setPATIENT(String.valueOf(patient.getId()));
			vis.setVISITDMY(pack.getPickupDate());
			Clinic c = patient.getClinicAtDate(pack.getPickupDate());
			if (c == null || c.isMainClinic()){
				vis.setVISITFAC(1);
			} else {
				vis.setVISITFAC(2);
			}
			vis.setTBSTATUS(99);
			vis.setWHOSTAGE(95);
			
			// Co-trimoxazole
			AtcCode j01ee01 = AdministrationManager.getAtccodeFromCode(session, "J01EE01");
			if (j01ee01 != null){
				boolean cotrimoxazole = containsCompound(pack.getPrescription(), "J01EE01");
				vis.setCTX(cotrimoxazole ? 1 : 2);
			} else {
				// if code is not in database then we can't ascertain
				vis.setCTX(99);
			}
			
			// Isoniazid
			AtcCode j04ac01 = AdministrationManager.getAtccodeFromCode(session, "J04AC01");
			if (j04ac01 != null){
				boolean isoniazid = containsCompound(pack.getPrescription(), "J04AC01");
				vis.setINH(isoniazid ? 1 : 2);
			} else {
				// if code is not in database then we can't ascertain
				vis.setINH(99);
			}
			
			// Fluconazole
			AtcCode j02ac01 = AdministrationManager.getAtccodeFromCode(session, "J02AC01");
			if (j02ac01 != null){
				boolean fluconazole = containsCompound(pack.getPrescription(), "J02AC01");
				vis.setFLU(fluconazole ? 1 : 2);
			} else {
				// if code is not in database then we can't ascertain
				vis.setFLU(99);
			}
			
			
			int age = patient.getAgeAt(pack.getPickupDate());
			vis.setSCHOOLY((age < 5 || age > 16) ? 88 : 95);
			
			export.getTables().add(vis);
			
			// add fake visits if the patient receives more than a one months supply
			int weeksover = pack.getWeekssupply() - 4;
			while (weeksover > 0){
				weeksover -= 4;
				VIS extraVis = new VIS();
				extraVis.setPATIENT(vis.getPATIENT());
				extraVis.setVISITDMY(iDARTUtil.add(vis.getVISITDMY(),30));
				extraVis.setVISITFAC(vis.getVISITFAC());
				extraVis.setTBSTATUS(vis.getTBSTATUS());
				extraVis.setWHOSTAGE(vis.getWHOSTAGE());
				extraVis.setCTX(vis.getCTX());
				extraVis.setINH(vis.getINH());
				extraVis.setFLU(vis.getFLU());
				extraVis.setSCHOOLY(vis.getSCHOOLY());
				
				export.getTables().add(extraVis);
			}
		}
	}
	
	private void populateART(Patient patient) {
		List<ArtDto> arts = PatientManager.getIedeaArtData(session, patient);
		Prescription mostRecentScript = patient.getMostRecentPrescription();
		boolean mostRecentScriptValid = mostRecentScript == null ? false : iDARTUtil.getDaysBetween(mostRecentScript.getDate(), new Date()) < 180;
		boolean patientIsActive = patient.getMostRecentEpisode().isOpen();
		
		for (ArtDto dto : arts) {
			ART art = new ART();
			art.setPATIENT(String.valueOf(patient.getId()));
			if (dto.getCode() == null || dto.getCode().isEmpty()){
				art.setARTID("J05A");
			} else {
				art.setARTID(dto.getCode());
			}
			
			art.setARTSDDMY(dto.getStartdate());
			art.setARTSDY(getYear(dto.getStartdate()));
			art.setARTSDM(getMonth(dto.getStartdate()));
			art.setARTRS(95);
			if (dto.getForm().toUpperCase().startsWith("CAPSULE")
				|| dto.getForm().toUpperCase().startsWith("TABLET")){
				art.setARTFORM(1);
			} else if (dto.getForm().toUpperCase().startsWith("SYRUP")
				|| dto.getForm().toUpperCase().startsWith("SUSPENSION")){
				art.setARTFORM(2);
			} else {
				art.setARTFORM(99);
			}
			
			art.setARTCOMB(95);
			
			if (dto.getCode() != null && !dto.getCode().isEmpty()){
				if (!patientIsActive || !mostRecentScriptValid || 
						!containsCompound(mostRecentScript, dto.getCode())){
					// if patient is not active any longer or
					// if most recent script is valid but doesn't contain drug with this ATC code then assume patient is no longer on it
					art.setARTEDDMY(dto.getEnddate());
					art.setARTEDY(getYear(dto.getEnddate()));
					art.setARTEDM(getMonth(dto.getEnddate()));
					art.setNODOSES(888);
					art.setNOWEEKS(888);
				}
			}
			
			art.setARTENDRS(99.5);
			art.setINFOSOURCE(1);
			export.getTables().add(art);
			
		}
		
	}
	
	private boolean containsCompound(Prescription script, String atccode) {
		List<PrescribedDrugs> pds = script.getPrescribedDrugs();
		for (PrescribedDrugs pd : pds) {
			Set<AtcCode> atccodes = pd.getDrug().getAtccodes();
			for (AtcCode atcCode: atccodes) {
				if (atcCode.getCode().equalsIgnoreCase(atccode)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * adult: first visit at your facility was after their 16th birthday
	 * pead: first visit at your facility is before their 16th birthday 
	 */
	private boolean isPead(Patient patient) {
		Date startDate = patient.getEpisodes().get(0).getStartDate();
		int ageAt = patient.getAgeAt(startDate);
		return ageAt < 16;
	}

	private Integer convertSex(char sex) {
		switch(sex){
		case 'M':
			return 1;
		case 'F':
			return 2;
		default:
			return 95;
		}
	}

	private int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	private int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH);
	}
	
	

}
