package model.nonPersistent;

import java.text.MessageFormat;

import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Stock;

public class StockLevelInfo {
	
	private static final String FORMAT = "{0,number,#} ({1,number,#})";

	private int dispensed;
	private int destroyed;
	private int returned;
	private int adjusted;
	private int onhand;
	private Stock stock;
	private int packSize;
	private int unitsReceived;
	
	public StockLevelInfo(Drug d) {
		packSize = d.getPackSize();
	}
	
	public StockLevelInfo(Stock stock) {
		this.stock = stock;
		packSize = stock.getDrug().getPackSize();
		unitsReceived = stock.getUnitsReceived();
	}
	
	public int getAdjusted() {
		return adjusted;
	}
	
	public int getAdjustedFull(){
		return adjusted / packSize;
	}
	
	public int getAdjustedLoose(){
		return adjusted % packSize;
	}
	
	public String getAdjustedString(){
		return MessageFormat.format(FORMAT, getAdjustedFull(), getAdjustedLoose());
	}
	
	public void setAdjusted(int adjusted) {
		this.adjusted = adjusted;
	}
	
	public int getDispensed() {
		return dispensed;
	}
	
	public int getDispensedFull(){
		return dispensed / packSize;
	}
	
	public int getDispensedLoose(){
		return dispensed % packSize;
	}
	
	public String getDispensedString(){
		return MessageFormat.format(FORMAT, getDispensedFull(), getDispensedLoose());
	}
	
	public void setDispensed(int dispensed) {
		this.dispensed = dispensed;
	}

	public int getDestroyed() {
		return destroyed;
	}
	
	public int getDestroyedFull(){
		return destroyed / packSize;
	}
	
	public int getDestroyedLoose(){
		return destroyed % packSize;
	}
	
	public String getDestroyedString(){
		return MessageFormat.format(FORMAT, getDestroyedFull(), getDestroyedLoose());
	}

	public void setDestroyed(int destroyed) {
		this.destroyed = destroyed;
	}

	public int getReturned() {
		return returned;
	}
	
	public int getReturnedFull(){
		return returned / packSize;
	}
	
	public int getReturnedLoose(){
		return returned % packSize;
	}
	
	public String getReturnedString(){
		return MessageFormat.format(FORMAT, getReturnedFull(), getReturnedLoose());
	}

	public void setReturned(int returned) {
		this.returned = returned;
	}

	public int getOnhand() {
		return onhand;
	}
	
	public int getOnhandFull(){
		return onhand / packSize;
	}
	
	public int getOnhandLoose(){
		return onhand % packSize;
	}
	
	public String getOnhandString(){
		return MessageFormat.format(FORMAT, getOnhandFull(), getOnhandLoose());
	}

	public void caluculateOnHand(){
		int totalPillsRec = unitsReceived * packSize;
		onhand = totalPillsRec - dispensed - adjusted - destroyed;	
	}
	
	public int getUnitsReceived() {
		return unitsReceived;
	}
	
	public Stock getStock() {
		return stock;
	}

	void setStock(Stock stock) {
		this.stock = stock;
	}
	
	public int getPackSize() {
		return packSize;
	}

	public void add(StockLevelInfo info) {
		dispensed += info.getDispensed();
		destroyed += info.getDestroyed();
		adjusted += info.getAdjusted();
		returned += info.getReturned();
		onhand += info.getOnhand();
		unitsReceived += info.getUnitsReceived();
	}
	
	
	
}
