package model.manager.excel.interfaces;

import java.util.List;

import model.manager.excel.conversion.exceptions.PatientException;

import org.celllife.idart.database.hibernate.Patient;
import org.hibernate.Session;

public interface ImportColumn<T> {

	public void findColumn(List<String> columnHeaders);

	public boolean process(List<String> rawValues, Session session) throws PatientException;

	void applyValue(Patient currentPatient) throws PatientException;

	String getRawValue();

	T getConvertedValue();
	
	boolean checkColumn();

	int getColumnNumber();

	public String getHeader();

	boolean isAllowBlank();

	String getConverterDescription();

}