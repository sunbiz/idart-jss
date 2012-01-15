package model.manager.excel.reports.in;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.excel.conversion.exceptions.PatientException;
import model.manager.excel.interfaces.BaseImportColumn;
import model.manager.excel.interfaces.ImportColumn;

import org.hibernate.Session;

public class MultiImportColumn<T> extends BaseImportColumn<Set<T>> implements ImportColumn<Set<T>> {
	
	private List<ImportColumn<T>> columns = new ArrayList<ImportColumn<T>>();
	private final boolean allowAllBlank;
	private final String headerPrefix;
	
	public MultiImportColumn(String headerPrefix, String beanProperty, boolean allowAllBlank) {
		super(beanProperty);
		this.headerPrefix = headerPrefix;
		this.allowAllBlank = allowAllBlank;
	}
	
	public String getHeaderPrefix() {
		return headerPrefix;
	}
	
	public void addColumn(ImportColumn<T> column){
		columns.add(column);
	}
	
	@Override
	public void findColumn(List<String> columnHeaders) {
		for (ImportColumn<T> col : columns) {
			col.findColumn(columnHeaders);
		}
	}
	
	@Override
	public boolean checkColumn() {
		if (allowAllBlank)
			return true;
		
		boolean allBlank = true;
		for (ImportColumn<T> col : columns) {
			if (col.getColumnNumber() >= 0)
				allBlank = false;
		}
		return !allBlank;
	}

	@Override
	public boolean process(List<String> rawValues, Session session) throws PatientException {
		boolean processSuccess = false;
		Set<T> set = new HashSet<T>();
		for (ImportColumn<T> col : columns) {
			boolean success = col.process(rawValues, session);
			if (success){
				set.add(col.getConvertedValue());
			}
			processSuccess |= success;
		}
		convertedValue = set;
		return processSuccess;
	}

	@Override
	public String getRawValue() {
		return "Patient attribute set";
	}

	@Override
	public Set<T> getConvertedValue() {
		return convertedValue;
	}
	
	@Override
	public int getColumnNumber() {
		return -1;
	}

	@Override
	public String getHeader() {
		StringBuilder sb = new StringBuilder();
		for (ImportColumn<T> col : columns) {
			sb.append(headerPrefix).append("(");
			sb.append(col.getHeader()).append(")").append("|");
		}
		return sb.substring(0,sb.length()-1);
	}
	
	@Override
	public boolean isAllowBlank() {
		return allowAllBlank;
	}
	
	public int getSize(){
		return columns.size();
	}
	
	@Override
	public String getConverterDescription() {
		StringBuilder sb = new StringBuilder();
		for (ImportColumn<T> col : columns) {
			sb.append(col.getConverterDescription()).append("|");
		}
		return sb.substring(0,sb.length()-1);
	}
}
