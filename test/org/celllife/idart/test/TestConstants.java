package org.celllife.idart.test;


public class TestConstants {

	public static final String dataDirectory = "test/datasets/";

	public static final String[] allTables = { "attributetype",
		"chemicalcompound", "chemicaldrugstrength", "clinic", "doctor",
		"drug", "form", "idartsite", "pharmacy", "regimen", "regimendrugs",
		"simpledomain", "users", "siteuser", "accumulateddrugs",
		"adherencerecordtmp", "alternatepatientidentifier", "appointment",
		"deleteditem", "episode", "hibernate_sequence", "logging",
		"package", "packageddrugs", "packagedruginfotmp", "patient",
		"patientattribute", "pillcount", "pregnancy", "prescribeddrugs",
		"prescription", "stock", "stockadjustment", "stocklevel",
	"stocktake" };

	public static final String[] coreDataTables = { "attributetype",
		"chemicalcompound", "chemicaldrugstrength", "clinic", "doctor",
		"drug", "form", "idartsite", "pharmacy", "regimen", "regimendrugs",
		"simpledomain", "users", "siteuser" };

	public static final String dtdFileLocation = "iDART.dtd";
}
