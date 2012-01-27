/**
 * 
 */
package model.nonPersistent;

import java.util.ArrayList;
import java.util.List;

import org.celllife.idart.database.hibernate.Drug;

/**
 * @author Rashid
 * 
 */
public class DrugDetail {

	private String drugDetails;

	private StockLevelInfo info;

	private List<BatchDetail> batches = new ArrayList<BatchDetail>();

	/**
	 * 
	 */
	public DrugDetail(Drug d) {
		super();
		info = new StockLevelInfo(d);
	}

	/**
	 * @return the drugDetails
	 */
	public String getDrugDetails() {
		return drugDetails;
	}

	/**
	 * @param drugDetails
	 *            the drugDetails to set
	 */
	public void setDrugDetails(String drugDetails) {
		this.drugDetails = drugDetails;
	}

	/**
	 * @return the unitsReceived
	 */
	public int getUnitsReceived() {
		return info.getUnitsReceived();
	}

	/**
	 * @return the batches
	 */
	public List<BatchDetail> getBatches() {
		return batches;
	}

	/**
	 * @param batches
	 *            the batches to set
	 */
	public void setBatches(List<BatchDetail> batches) {
		this.batches = batches;
	}
	
	public StockLevelInfo getStockLevelInfo() {
		return info;
	}

	/**
	 * This method adds a BatchDetail to the Lis and updates all the values
	 * affected.
	 * 
	 * @param bd
	 */
	public void AddBatchDetail(BatchDetail bd) {
		if (bd == null)
			return;

		batches.add(bd);
		info.add(bd.getStockLevelInfo());
	}

}
