package model.manager.excel.reports.in;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.excel.interfaces.SessionBasedImportConverter;

import org.celllife.idart.database.hibernate.Clinic;
import org.celllife.idart.database.hibernate.User;
import org.hibernate.Session;

public class ClinicConverter implements SessionBasedImportConverter<Clinic> {

	private Session session;
	private List<Clinic> clinics;
	private List<User> userList;

	@Override
	public Clinic convert(String rawValue) {
		for (Clinic c : clinics) {
			if (c.getClinicName().equalsIgnoreCase(rawValue)) {
				return c;
			}
		}
		
		Clinic newClinic = new Clinic();
		newClinic.setClinicName(rawValue);
		newClinic.setMainClinic(false);
		Set<User> users = new HashSet<User>();
		users.addAll(userList);
		newClinic.setUsers(users);
		session.save(newClinic);
		session.flush();
		clinics.add(newClinic);
		return newClinic;
	}

	@Override
	public void initialise(Session hsession) {
		this.session = hsession;
		this.clinics = AdministrationManager.getClinics(session);
		this.userList = AdministrationManager.getUsers(session);
	}
	
	@Override
	public String getDescription() {
		return "A clinic name";
	}
}
