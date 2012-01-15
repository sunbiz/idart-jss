package model.manager;

import java.util.List;

import org.celllife.idart.database.hibernate.Campaign;
import org.celllife.idart.database.hibernate.CampaignParticipant;
import org.celllife.mobilisr.constants.CampaignStatus;
import org.celllife.mobilisr.constants.CampaignType;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class CampaignManager {

	public static void addCampaigns(Session session, List<Campaign> campaigns){
		for(Campaign campaign : campaigns){
			session.saveOrUpdate(campaign);
		}
	}
	
	public static void addCampaign(Session session, Campaign campaign) throws HibernateException{
		session.save(campaign);
	}
	
	@SuppressWarnings("unchecked")
	public static Campaign getCampaignById(Session session, Long id) throws HibernateException{
		List<Campaign> campaigns = session.createQuery("from Campaign where id = :id").setLong("id", id).list();
		if(campaigns.size() == 1){
			return campaigns.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Campaign getCampaignByMobilisrId(Session session, Long id) throws HibernateException{
		List<Campaign> campaigns = session.createQuery("from Campaign where mobilisrid = :id").setLong("id", id).list();
		if(campaigns.size() == 1){
			return campaigns.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Campaign> getCampaigns(Session session) throws HibernateException{
		List<Campaign> campaigns = session
				.createQuery(
						"from Campaign where type = :type and status = :status")
				.setParameter("type", CampaignType.DAILY.name())
				.setParameter("status", CampaignStatus.ACTIVE.name()).list();
		return campaigns;
	}
	
	@SuppressWarnings("unchecked")
	public static Campaign getCampaignByName(Session session, String name) throws HibernateException {
		List<Campaign> campaigns = session.createQuery("from Campaign where name = :name").setString("name", name).list();
		if(campaigns.size() == 1){
			return campaigns.get(0);
		}else{
			return null;
		}
	}
	
	public static void addCampaignParticipant(Session session, CampaignParticipant campaignParticipant)throws HibernateException {
		session.save(campaignParticipant);
	}
	
	@SuppressWarnings({ "unchecked", "cast" })
	public static Long getPatientRegisteredMobilisrCampaignId(Session session, int id) {
		List<CampaignParticipant> participants = (List<CampaignParticipant>)session.createQuery("from CampaignParticipant where patient = :id").setInteger("id", id).list();
		if(participants.size() == 1){
			CampaignParticipant campaignParticipant = participants.get(0);
			return campaignParticipant.getCampaign().getMobilisrId();
		}else{
			return null;
		}
	}

	public static void removeCampaignParticipant(Session session, int patientId) throws HibernateException {
		session.createQuery("Delete CampaignParticipant where patient_id = :patientId").setInteger("patientId", patientId).executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isCampaignAddedToIDart(Session session, long mobilisrId) throws HibernateException {
		List<Campaign> campaigns = session.createQuery("From Campaign where mobilisrid = :mobilisrId").setLong("mobilisrId", mobilisrId).list();
		if(campaigns.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public static void updateCampaigns(Session session, List<Campaign> campaigns) throws HibernateException {
		if(campaigns != null && campaigns.size() > 0 ){
			for(Campaign campaign : campaigns){
				if(isCampaignAddedToIDart(session, campaign.getMobilisrId())){
					updateIDartCampaign(session, campaign);
				}else if (CampaignStatus.ACTIVE.name().equals(campaign.getStatus())){
					session.save(campaign);
				}
			}
		}
	}
	
	private static int updateIDartCampaign(Session session, Campaign campaign) throws HibernateException{
		return session.createQuery("Update Campaign set description = :description, duration = :duration, name = :name," +
				" startdate = :startdate, status = :status, timesperday = :timesperday, type = :type  " +
				"where mobilisrid = :mobilisrid").setString("description", campaign.getDescription()).
				setInteger("duration", campaign.getDuration()).setString("name", campaign.getName()).
				setTimestamp("startdate", campaign.getStartDate()).setString("status", campaign.getStatus()).
				setInteger("timesperday", campaign.getTimesPerDay()).setString("type", campaign.getType()).
				setLong("mobilisrid", campaign.getMobilisrId()).executeUpdate();		
	}

}
