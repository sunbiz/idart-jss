package model.manager.excel.reports.in;

import java.util.List;

import model.manager.excel.conversion.exceptions.PatientException;
import model.manager.excel.interfaces.BaseImportColumn;
import model.manager.excel.interfaces.ImportColumn;
import model.manager.excel.interfaces.ImportConverter;
import model.manager.excel.interfaces.ImportValidator;
import model.manager.excel.interfaces.SessionBasedImportConverter;
import model.manager.excel.interfaces.SessionBasedImportValidator;

import org.hibernate.Session;

public class SingleImportColumn<T> extends BaseImportColumn<T> implements ImportColumn<T> {

	private final String columnHeader;
	private final boolean allowBlank;
	private final ImportConverter<T> converter;
	private final ImportValidator<T> validator;
	
	private int columnNumber = -1;
	private final T defaultValue;
	String rawValue;
	
	public SingleImportColumn(String columnHeader, String beanProperty) {
		super(beanProperty);
		this.columnHeader = columnHeader;
		this.allowBlank = true;
		this.converter = null;
		this.validator = null;
		this.defaultValue= null;
	}
	
	public SingleImportColumn(String columnHeader, boolean allowBlank,
			ImportConverter<T> converter, ImportValidator<T> validator,String beanProperty) {
		super(beanProperty);
		this.columnHeader = columnHeader;
		this.allowBlank = allowBlank;
		this.converter = converter;
		this.validator = validator;
		this.defaultValue= null;
	}
	
	public SingleImportColumn(String columnHeader, boolean allowBlank,
			ImportConverter<T> converter, ImportValidator<T> validator,String beanProperty,
			T defaultValue) {
		super(beanProperty);
		this.columnHeader = columnHeader;
		this.allowBlank = allowBlank;
		this.converter = converter;
		this.validator = validator;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getHeader() {
		return columnHeader;
	}

	@Override
	public boolean isAllowBlank() {
		return allowBlank;
	}
	
	@Override
	public String getConverterDescription(){
		if (converter == null){
			return "";
		}
		return converter.getDescription();
	}

	public ImportConverter<T> getConverter() {
		return converter;
	}

	public ImportValidator<T> getValidator() {
		return validator;
	}
	
	/* (non-Javadoc)
	 * @see model.manager.excel.interfaces.BaseImportColumn#findColumn(java.util.List)
	 */
	@Override
	public void findColumn(List<String> columnHeaders){
		for (int i = 0; i < columnHeaders.size(); i++) {
			if (columnHeaders.get(i).toLowerCase().contains(columnHeader.toLowerCase())){
				columnNumber = i;
				return;
			}
		}
	}
	
	@Override
	public boolean checkColumn() {
		return getColumnNumber() >= 0 || isAllowBlank();
	}

	/* (non-Javadoc)
	 * @see model.manager.excel.interfaces.BaseImportColumn#process(java.util.List, org.celllife.idart.database.hibernate.Patient, org.hibernate.Session)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean process(List<String> rawValues, Session session) throws PatientException {
		if (columnNumber < 0){
			if (!allowBlank){
				throw new PatientException(columnHeader + " is not allowed to be empty");
			} else {
				rawValue = "";
			}
		} else {
			rawValue = rawValues.get(columnNumber).trim();
		}
		
		convertedValue = null;
		
		if (rawValue.isEmpty()) {
			if (!allowBlank){
				throw new PatientException(columnHeader + " is not allowed to be empty");
			} else if (defaultValue != null){
				convertedValue = defaultValue;
			} else {
				return false;
			}
		}
		
		// if converted value hasn't already been set from the defatul value
		if (convertedValue == null) {
			if (converter != null){
				if (converter instanceof SessionBasedImportConverter){
					((SessionBasedImportConverter<?>)converter).initialise(session);
				}
				
				convertedValue = converter.convert(rawValue);
			}else {
				// assume that type is String
				convertedValue = (T) rawValue;
			}
		}
			
		if (convertedValue == null) {
			if (!allowBlank && defaultValue == null){
				throw new PatientException("Error converting " + rawValue + " for "
						+ columnHeader + " column");
			} else if (defaultValue != null){
				convertedValue = defaultValue;
			} else {
				return false;
			}
		}
		
		if (validator != null){
			if (validator instanceof SessionBasedImportValidator){
				((SessionBasedImportValidator<?>)validator).initialise(session);
			}
			
			String errorMessage = validator.validate(convertedValue);
			if (errorMessage != null){
				throw new PatientException(errorMessage);
			}
		}
		
		return true;	
	}
	
	@Override
	public String getRawValue(){
		return rawValue;
	}

	@Override
	public int getColumnNumber() {
		return columnNumber;
	}
	
	@Override
	public String toString() {
		return columnHeader + ": " + rawValue;
	}
	
}
