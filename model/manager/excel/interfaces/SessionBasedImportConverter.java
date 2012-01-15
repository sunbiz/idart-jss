package model.manager.excel.interfaces;


import org.hibernate.Session;

public interface SessionBasedImportConverter<T> extends ImportConverter<T> {

	public void initialise(Session session);
	
}
