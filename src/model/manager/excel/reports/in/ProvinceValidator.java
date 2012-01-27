package model.manager.excel.reports.in;

import java.util.List;

import model.manager.AdministrationManager;
import model.manager.excel.interfaces.SessionBasedImportValidator;

import org.hibernate.Session;

public class ProvinceValidator implements SessionBasedImportValidator<String> {

	private Session session;
	private List<String> provinces;

	@Override
	public String validate(String rawValue) {
		for (String prov : provinces) {
			if (prov.equalsIgnoreCase(rawValue)) {
				return null;
			}
		}
		return "Invalid Province. Please enter a valid province.";
	}

	@Override
	public void initialise(Session hsession) {
		this.session = hsession;
		this.provinces = AdministrationManager.getProvinces(session);
	}

}
