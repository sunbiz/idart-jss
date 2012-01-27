package model.nonPersistent;

import model.manager.exports.columns.DrugDetailsEnum;

public class ExportDrugInfo {
	private String chemicalCompoundName;
	private int quantityDispensed;
	private int accumulatedDrugs;
	private double amountPerTime;
	private int timesPerDay;
	private int drugId;
	private String batchNumber;
	private boolean isARV;
	
	
	public ExportDrugInfo(String chemicalCompoundName, int quantityDispensed, int accumulatedDrugs, double amountPerTime, int timesPerDay){
		this.chemicalCompoundName = chemicalCompoundName;
		this.quantityDispensed = quantityDispensed;
		this.accumulatedDrugs = accumulatedDrugs;
		this.amountPerTime = amountPerTime;
		this.timesPerDay = timesPerDay;
	}
	
	/**
	 * @return the chemicalCompoundName
	 */
	public String getChemicalCompoundName() {
		return chemicalCompoundName;
	}
	/**
	 * @param chemicalCompoundName the chemicalCompoundName to set
	 */
	public void setChemicalCompoundName(String chemicalCompoundName) {
		this.chemicalCompoundName = chemicalCompoundName;
	}
	/**
	 * @return the quantityDispensed
	 */
	public int getQuantityDispensed() {
		return quantityDispensed;
	}
	/**
	 * @param quantityDispensed the quantityDispensed to set
	 */
	public void setQuantityDispensed(int quantityDispensed) {
		this.quantityDispensed = quantityDispensed;
	}
	/**
	 * @return the accumulatedDrugs
	 */
	public int getAccumulatedDrugs() {
		return accumulatedDrugs;
	}
	/**
	 * @param accumulatedDrugs the accumulatedDrugs to set
	 */
	public void setAccumulatedDrugs(int accumulatedDrugs) {
		this.accumulatedDrugs = accumulatedDrugs;
	}
	/**
	 * @return the amoundPerTime
	 */
	public double getAmountPerTime() {
		return amountPerTime;
	}
	/**
	 * @param amoundPerTime the amoundPerTime to set
	 */
	public void setAmountPerTime(double amountPerTime) {
		this.amountPerTime = amountPerTime;
	}
	/**
	 * @return the timesPerDay
	 */
	public int getTimesPerDay() {
		return timesPerDay;
	}
	/**
	 * @param timesPerDay the timesPerDay to set
	 */
	public void setTimesPerDay(int timesPerDay) {
		this.timesPerDay = timesPerDay;
	}
	
	public ExportDrugInfo(){
	}
	
	@Override
	public String toString(){
		String x = "ExportDruginfo\n" +
		"Chemical Compound Name:" + chemicalCompoundName + "\n" +
		"Quantity Dispensed: " + quantityDispensed + "\n" +
		"Accumulated Drugs: " + accumulatedDrugs + "\n" +
		"Amount Per Time: " + amountPerTime + "\n" +
		"Times Per Day: " + timesPerDay + "\n" +
		"Num days till runout: " + ((quantityDispensed + accumulatedDrugs)/(amountPerTime * timesPerDay))
		;
		return x;
	}
	
	public double getDaysTillRunout(){
		return (quantityDispensed + accumulatedDrugs)/(amountPerTime * timesPerDay);
	}

	public void setDrugId(int drugId) {
		this.drugId = drugId;
	}
	
	public int getDrugId(){
		return drugId;
	}

	public void setBatch(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	
	public String getBatch(){
		return batchNumber;
	}

	public void setIsARV(boolean isARV) {
		this.isARV = isARV;
	}

	public boolean isARV(){
		return isARV;
	}
	
	public Object getField(DrugDetailsEnum field){
		switch (field) {
		case ACCUMULATEDDRUGS:
			return getAccumulatedDrugs();
		case AMOUNTPERTIME:
			return getAmountPerTime();
		case BATCHNUMBER:
			return getBatch();
		case CHEMICALCOMPOUNDNAME:
			return getChemicalCompoundName();
		case DRUGID:
			return getDrugId();
		case ISARV:
			return isARV();
		case QUANTITYDISPENSED:
			return getQuantityDispensed();
		case TIMESPERDAY:
			return getTimesPerDay();
		default:
			return null;
		}
	}
}
