package model.manager.excel.interfaces;

import model.manager.excel.conversion.exceptions.PatientException;

import org.apache.commons.beanutils.PropertyUtils;
import org.celllife.idart.database.hibernate.Patient;

public abstract class BaseImportColumn<T> implements ImportColumn<T> {
	
	private final String beanProperty;
	protected T convertedValue;

	public BaseImportColumn(String beanProperty) {
		super();
		this.beanProperty = beanProperty;
	}

	@Override
	public void applyValue(Patient currentPatient) throws PatientException {
		try {
			if (beanProperty.contains(".")){
				PropertyUtils.setNestedProperty(currentPatient, beanProperty, convertedValue);
			} else {
				PropertyUtils.setProperty(currentPatient, beanProperty, convertedValue);
			}
		} catch (Exception e) {
			throw new PatientException("Error applying value '" + getRawValue()
					+ "' to patient property '" + beanProperty + "'");
		}
	}
	
	@Override
	public T getConvertedValue() {
		return convertedValue;
	}

}