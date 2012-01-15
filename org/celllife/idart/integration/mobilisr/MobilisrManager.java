package org.celllife.idart.integration.mobilisr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.celllife.idart.commonobjects.PropertiesManager;
import org.celllife.idart.commonobjects.SmsProperties;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.Campaign;
import org.celllife.mobilisr.api.rest.CampaignDto;
import org.celllife.mobilisr.api.rest.ContactDto;
import org.celllife.mobilisr.api.rest.PagedListDto;
import org.celllife.mobilisr.api.validation.MsisdnCountryRule;
import org.celllife.mobilisr.api.validation.MsisdnValidator.ValidationError;
import org.celllife.mobilisr.api.validation.ValidatorFactory;
import org.celllife.mobilisr.api.validation.ValidatorFactoryImpl;
import org.celllife.mobilisr.client.CampaignService;
import org.celllife.mobilisr.client.ContactService;
import org.celllife.mobilisr.client.MobilisrClient;
import org.celllife.mobilisr.client.exception.RestCommandException;
import org.celllife.mobilisr.client.impl.MobilisrClientImpl;
import org.celllife.mobilisr.constants.CampaignType;

public class MobilisrManager {
	
	private static MobilisrClient client;
	private static ValidatorFactory vfactory;
	
	public static List<Campaign> getCampaigns() throws RestCommandException{
		List<Campaign> campaigns = new ArrayList<Campaign>();
		PagedListDto<CampaignDto> relative = getCampaignService().getCampaigns(CampaignType.DAILY);
		PagedListDto<CampaignDto> generic = getCampaignService().getCampaigns(CampaignType.FLEXI);
		
		if(relative != null && relative.getElements() != null){
			for(CampaignDto dto : relative.getElements()){
				Campaign campaign = getCampaignFromDto(dto);
				campaigns.add(campaign);
			}
		}
		
		if(generic != null && generic.getElements() != null){
			for(CampaignDto dto : generic.getElements()){
				Campaign campaign = getCampaignFromDto(dto);
				campaigns.add(campaign);
			}
		}
		return campaigns;
	}
	/**
	 * @param dto
	 * @return
	 */
	private static Campaign getCampaignFromDto(CampaignDto dto) {
		Campaign campaign = new Campaign();
		campaign.setMobilisrId(dto.getId());
		campaign.setName(dto.getName());
		campaign.setDescription(dto.getDescription());
		if(dto.getDuration() != null)
			campaign.setDuration(dto.getDuration());
		campaign.setStartDate(dto.getStartDate());
		if(dto.getTimesPerDay() != null)
			campaign.setTimesPerDay(dto.getTimesPerDay());
		campaign.setStatus(dto.getStatus());
		campaign.setType(dto.getType());
		return campaign;
	}
	
	/**
	 * Add the Patient to the Campaign
	 * @param firstName
	 * @param lastName
	 * @param cellphone
	 * @param campaignId
	 * @param msgTime 
	 * @throws RestCommandException 
	 */
	public static void addPatientToCampaign(String firstName, String lastName, String cellphone, Long campaignId, Date msgTime) throws RestCommandException{
		
		ContactDto contactDto = new ContactDto();
		contactDto.setFirstName(firstName);
		contactDto.setLastName(lastName);
		contactDto.setMsisdn(cellphone);
		if (msgTime != null){
			contactDto.setContactMessageTimes(Arrays.asList(msgTime));
		}
		
		try {
			getCampaignService().addContactToCampaign(campaignId, contactDto);
		} catch (Exception e) {
			throw new RestCommandException(e.getMessage(), e);
		}
	}
	
	/**
	 * Remove's a patient from a campaign
	 * @param campaignId
	 * @param cellphone
	 * @throws RestCommandException 
	 */
	public static void removePatientFromCampaign(Long campaignId, String cellphone) throws RestCommandException{
		getCampaignService().removeContactFromCampaign(campaignId, cellphone);
	}
	
	/**
	 * Creates a new justSms Campaign
	 * @param campaign
	 */
	public static void createNewCampaign(CampaignDto campaign) throws RestCommandException {
		
		getCampaignService().createNewCampaign(campaign);
				
	}
	
	public static void updateMobilisrCellNo(String oldCellNo, String firstName, String lastName, String newCellNo) throws RestCommandException{

		ContactDto contactDto = new ContactDto();
		contactDto.setFirstName(firstName);
		contactDto.setLastName(lastName);
		contactDto.setMsisdn(newCellNo);

		getContactService().updateContactDetails(oldCellNo, contactDto);

	}
	
	
	private static CampaignService getCampaignService(){
		initClient();
		return client.getCampaignService();
	}
	
	private static ContactService getContactService(){
		initClient();
		return client.getContactService();
	}
	
	private static void initClient() {
		if (client == null){
			client = new MobilisrClientImpl(PropertiesManager.sms().mobilisrurl(), 
					PropertiesManager.sms().mobilisrusername(),
					PropertiesManager.sms().mobilisrpassword(), getValidator());
		}
	}
	
	private static ValidatorFactory getValidator(){
		if (vfactory == null){
			ValidatorFactoryImpl vf = new ValidatorFactoryImpl();
			String prefix = PropertiesManager.sms().msisdnPrefix();
			String regex = PropertiesManager.smsRaw().getProperty(SmsProperties.MSISDN_REGEX);
			vf.setCountryRules(Arrays.asList(new MsisdnCountryRule(iDartProperties.country, 
					prefix, regex)));
			vfactory = vf;
		}
		return vfactory;
	}
	
	public static ValidationError validateMsisdn(String msisdn){
		ValidationError error = getValidator().validateMsisdn(msisdn);
		return error;
	}
}
