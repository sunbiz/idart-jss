/**
 * 
 */
package model.nonPersistent;

import java.util.Date;

/**
 * @author Rashid
 *
 */
public class BatchDetail {

	private String batchName;

	private Date dateReceived;

	private DrugDetail drugDetail;

	private StockLevelInfo info;

	/**
	 * 
	 */
	public BatchDetail() {
		super();
	}

	/**
	 * @return the batchName
	 */
	public String getBatchName() {
		return batchName;
	}

	/**
	 * @param batchName
	 *            the batchName to set
	 */
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	/**
	 * @return the dateReceived
	 */
	public Date getDateReceived() {
		return dateReceived;
	}

	/**
	 * @param dateReceived
	 *            the dateReceived to set
	 */
	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	/**
	 * @return the unitsReceived
	 */
	public int getUnitsReceived() {
		return info.getUnitsReceived();
	}

	/**
	 * @return the drugDetail
	 */
	public DrugDetail getDrugDetail() {
		return drugDetail;
	}

	/**
	 * @param drugDetail
	 *            the drugDetail to set
	 */
	public void setDrugDetail(DrugDetail drugDetail) {
		this.drugDetail = drugDetail;
	}

	public void setStockLevelInfo(StockLevelInfo info) {
		this.info = info;
	}
	
	public StockLevelInfo getStockLevelInfo(){
		return info;
	}


}
