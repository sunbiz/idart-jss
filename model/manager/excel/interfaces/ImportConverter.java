package model.manager.excel.interfaces;

import model.manager.excel.conversion.exceptions.PatientException;

public interface ImportConverter<T> {
	
	public T convert(String rawValue) throws PatientException;

	public String getDescription();

}
