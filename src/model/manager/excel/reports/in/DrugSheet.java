package model.manager.excel.reports.in;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.manager.AdministrationManager;
import model.manager.DrugManager;
import model.manager.excel.conversion.exceptions.DrugException;
import model.manager.excel.conversion.exceptions.FormException;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Form;

/**
 */
public class DrugSheet extends BaseImportSheet {

	private final String name = "Drug Name";

	private int nameCol = -1;

	private final String form = "Form";

	private int formCol = -1;

	private final String packsize = "Packsize";

	private int packsizeCol = -1;

	private final String sideTreatment = "Side Treatment";

	private int sideTreatmentCol = -1;
	
	private final String chemicalName1 = "Name1";

	private int chemicalName1Col = -1;
	
	private final String chemicalAcronym1 = "Acronym1";

	private int chemicalAcronym1Col = -1;
	
	private final String chemicalStrength1 = "Strength1";

	private int chemicalStrength1Col = -1;
	
	private final String chemicalName2 = "Name2";

	private int chemicalName2Col = -1;
	
	private final String chemicalAcronym2 = "Acronym2";

	private int chemicalAcronym2Col = -1;
	
	private final String chemicalStrength2 = "Strength2";

	private int chemicalStrength2Col = -1;

	private Logger log = null;

	private Drug currentDrug = null;

	private Form currentForm = null;

	public DrugSheet() {
		super("Sheet1");
		log = Logger.getLogger(DrugSheet.class);
	}

	/**
	 * Method to find the locations of expected columns
	 * @return boolean
	 * @see model.manager.excel.interfaces.ReadExcelReportInterface#findColumns()
	 */
	@Override
	public void findColumns(List<String> headerRow) {
		boolean checker = true;
		while (checker) {
			for (int i = 0; i < headerRow.size(); i++) {
				String txt = headerRow.get(i);
				if (txt.equalsIgnoreCase(name)) {
					// We have a drug name
					nameCol = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(form)) {
					// We have a form
					formCol = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(packsize)) {
					packsizeCol = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(chemicalName1)) {
					chemicalName1Col = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(chemicalName2)) {
					chemicalName2Col = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(chemicalAcronym1)) {
					chemicalAcronym1Col = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(chemicalAcronym2)) {
					chemicalAcronym2Col = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(chemicalStrength1)) {
					chemicalStrength1Col = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(chemicalStrength2)) {
					chemicalStrength2Col = i;
					checker = false;
				} else if (txt.equalsIgnoreCase(sideTreatment)) {
					sideTreatmentCol = i;
					checker = false;
				} else {
					log.debug("Could not find column: " + txt);
				}
			}
			dataStartRow++;
		}
	}

	/**
	 * Reads in a specific row, if the row entries are like the ones in memory,
	 * don't add them,
	 * 
	 * @param row int
	 * @param getSession() Session
	 */
	@Override
	protected boolean readRow(int rowNumber, List<String> row) {
		try {

			/**
			 * Check if any of the required fields are missing
			 */
			if (row.get(nameCol).equalsIgnoreCase(""))
				throw new DrugException(
						"Cannot create Drug, drug name is missing");

			else if (row.get(packsizeCol) == (""))
				throw new DrugException(
						"Cannot create Drug, packsize is missing");

			else if (row.get(formCol) == (""))
				throw new DrugException(
						"Cannot create Drug, form of drug is missing");

			else {
				/**
				 * First we check if the form exists.
				 */
				currentForm = AdministrationManager.getForm(getSession(), row.get(formCol));

				if (currentForm == null)
					throw new FormException("This form of drug does not exist");

				currentDrug = new Drug();

				/**
				 * Get the default instructions. If we cannot get it from
				 * spreadsheet, we set them to zero
				 */
				currentDrug.setDefaultAmnt(0);
				currentDrug.setDefaultTimes(0);
				

				currentDrug.setDispensingInstructions1("");
				currentDrug.setDispensingInstructions2("");
				currentDrug.setStockCode("");
				currentDrug.setForm(currentForm);
				currentDrug.setModified('T');
				currentDrug.setName(row.get(nameCol));

				int packSize = 0;
				try {
					/**
					 * Check Length as packsize might have ml extension
					 */
					String psize = row.get(packsizeCol);
					String pack_size = "";
					if (psize.length() > 2) {
						psize.replaceAll(" ", "");
						if (psize.substring(psize.length() - 2)
								.equalsIgnoreCase("ml"))
							pack_size = psize.substring(0, psize.length() - 3);
						else
							pack_size = row.get(packsizeCol);
					} else
						pack_size = row.get(packsizeCol);
					packSize = Integer.parseInt(pack_size);
				} catch (NumberFormatException nbe) {
					packSize = -1;
				}
				if (packSize == -1)
					throw new DrugException(
							"Cannot create drug, invalid packsize");

				currentDrug.setPackSize(packSize);
				if (sideTreatmentCol != -1) {
					if (row.get(sideTreatmentCol).startsWith("F") || row.get(sideTreatmentCol).startsWith("f") 
							|| row.get(sideTreatmentCol)
									.equalsIgnoreCase(""))
						currentDrug.setSideTreatment('F');

					else
						currentDrug.setSideTreatment('T');
				} else
					currentDrug.setSideTreatment('F');
				
				/**
				 * Add The chemical compounds if present
				 */
				if(currentDrug.getSideTreatment() == 'F') {
					
					Set<ChemicalDrugStrength> strengths = new HashSet<ChemicalDrugStrength>();
					
					if("".equals(row.get(chemicalName1Col)) && "".equals(row.get(chemicalAcronym1Col)) 
							&& "".equals(row.get(chemicalStrength1Col))) {
						
						// Do nothing
					}
					else if("".equals(row.get(chemicalName1Col)) || "".equals(row.get(chemicalAcronym1Col)) 
							|| "".equals(row.get(chemicalStrength1Col))) {
						
						throw new DrugException(
						"Cannot create drug, Not enough information for chemical compound");
						
					}
					else {
						
						ChemicalCompound cc = DrugManager.getChemicalCompoundByName(getSession(), row.get(chemicalName1Col));
						
						if(cc == null)
							cc = DrugManager.getChemicalCompoundByAcronym(getSession(), row.get(chemicalAcronym1Col));
						
						if(cc == null) {
							throw new DrugException(
							"Cannot create drug, Cannot find 1st chemical compound");
						}
						else {
							
						
						
							int strength;
							try {
								strength = Integer.parseInt(row.get(chemicalStrength1Col));
							} catch (NumberFormatException nfe) {
								
								throw new DrugException(
								"Cannot create drug, Invalid Chemical Strength");
							}
							
							ChemicalDrugStrength chemicalDrugStrength = new ChemicalDrugStrength();
							chemicalDrugStrength.setChemicalCompound(cc);
							chemicalDrugStrength.setDrug(currentDrug);
							chemicalDrugStrength.setStrength(strength);
							
							
							strengths.add(chemicalDrugStrength);
						}
						
					}
					
					// Compound 2
					if("".equals(row.get(chemicalName2Col)) && "".equals(row.get(chemicalAcronym2Col)) 
							&& "".equals(row.get(chemicalStrength2Col))) {
						
						// Do nothing
					}
					else if("".equals(row.get(chemicalName2Col)) || "".equals(row.get(chemicalAcronym2Col)) 
							|| "".equals(row.get(chemicalStrength2Col))) {
						
						throw new DrugException(
						"Cannot create drug, Not enough information for chemical compound");
						
					}
					else {
						
						ChemicalCompound cc = DrugManager.getChemicalCompoundByName(getSession(), row.get(chemicalName2Col));
						
						if(cc == null)
							cc = DrugManager.getChemicalCompoundByAcronym(getSession(), row.get(chemicalAcronym2Col));
						
						if(cc == null) {
							throw new DrugException(
							"Cannot create drug, Cannot find 2nd chemical compound");
						}
						else {
							
							int strength;
							try {
								strength = Integer.parseInt(row.get(chemicalStrength2Col));
							} catch (NumberFormatException nfe) {
								
								throw new DrugException(
								"Cannot create drug, Invalid Chemical Strength");
							}
							
							ChemicalDrugStrength chemicalDrugStrength = new ChemicalDrugStrength();
							chemicalDrugStrength.setChemicalCompound(cc);
							chemicalDrugStrength.setDrug(currentDrug);
							chemicalDrugStrength.setStrength(strength);
							
							
							
							strengths.add(chemicalDrugStrength);
						}
					}
					if(strengths.size() > 0)
						currentDrug.setChemicalDrugStrengths(strengths);
				}

				DrugManager.saveDrug(getSession(), currentDrug);
			}
			return true;
		} catch (Exception pe) {
			log.info("Could not create Form");
			copyRowToErrorFile(rowNumber, pe.getMessage());
			return false;
		}
	}

	
	@Override
	public String checkColumns() {
		/**
		 * If one or more of the columns containing drug name, form or
		 * packsize is not found in the spreadsheet, we cannot proceed any
		 * further
		 */
		if ((nameCol == -1) || (formCol == -1) || (packsizeCol == -1))
			return "Sheet must contain the following columns: " + name
			 + ", " + form + ", " + packsize;
		
		return null;
	}

}