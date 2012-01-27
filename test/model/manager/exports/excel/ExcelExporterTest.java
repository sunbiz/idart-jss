package model.manager.exports.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.celllife.idart.test.IDARTtest;
import org.testng.annotations.Test;

public class ExcelExporterTest extends IDARTtest{
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	@Test
	public void testGetEntitySet() throws ParseException{
		ExcelReportObject o = new ExcelReportObject();
		o.setPharmacy("1");
		o.setStartDate(sdf.parse("01-01-2007"));
		o.setEndDate(sdf.parse("01-05-2010"));
		new RowPerPackageExcelExporter().getPatientSet(o);
	}

}
