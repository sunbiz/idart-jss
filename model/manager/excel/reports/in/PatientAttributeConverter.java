package model.manager.excel.reports.in;

import java.util.Date;

import model.manager.excel.conversion.exceptions.PatientException;
import model.manager.excel.interfaces.ImportConverter;

import org.celllife.idart.database.hibernate.AttributeType;
import org.celllife.idart.database.hibernate.PatientAttribute;
import org.celllife.idart.misc.iDARTUtil;

public class PatientAttributeConverter implements ImportConverter<PatientAttribute> {

	private AttributeType type;
	
	public PatientAttributeConverter(AttributeType type) {
		this.type = type;
	}

	@Override
	public PatientAttribute convert(String rawValue) throws PatientException {
		Object value = convertToType(rawValue);
		if (value == null){
			return null;
		}
		String valueString = iDARTUtil.toString(type.getDataType(), value);
		PatientAttribute patt = new PatientAttribute();
		if (type != null) {
			patt.setType(type);
			patt.setValue(valueString);
			return patt;
		} 
		
		return null;
	}

	private Object convertToType(String rawValue) throws PatientException {
		ImportConverter<?> converter = getConverter();
		if (converter == null){
			return rawValue;
		}
		
		return converter.convert(rawValue);
	}
	
	private ImportConverter<?> getConverter(){
		Class<?> dataType = type.getDataType();
		if (dataType.equals(String.class)){
			return null;
		} else if (dataType.equals(Date.class)){
			return new DateConverter();
		} else if (dataType.equals(Boolean.class)){
			return new BooleanConverter();
		} else if (dataType.equals(Double.class)){
			return new DoubleConverter();
		} else if (dataType.equals(Integer.class)){
			return new IntegerConverter();
		}
		return null;
	}
	
	@Override
	public String getDescription() {
		ImportConverter<?> converter = getConverter();
		if (converter == null){
			return "";
		}
		return converter.getDescription();
	}
}
