package model.manager.excel.interfaces;


import org.hibernate.Session;

public interface SessionBasedImportValidator<T> extends ImportValidator<T> {

	public void initialise(Session session);
	
}
