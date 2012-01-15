package org.celllife.idart.misc.task;

import java.util.Date;
import java.util.List;

import model.manager.DrugManager;
import model.manager.StockManager;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Stock;
import org.celllife.idart.database.hibernate.StockAdjustment;
import org.celllife.idart.database.hibernate.StockLevel;
import org.celllife.idart.database.hibernate.StockTake;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.database.hibernate.util.TransactionalCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.hibernate.Session;

public class RecalculateSockTask implements IdartTask {
	
	private static Logger log = Logger.getLogger(RecalculateSockTask.class);

	private boolean adjustNegaiveLevels = false;

	private StockTake stockTake;

	@Override
	public boolean init(String[] args) {
		if (args.length > 0){
			if (args[0].equals("-adjust")){
				adjustNegaiveLevels = true;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws TaskException {
		Session session = HibernateUtil.getNewSession();
		
		if (adjustNegaiveLevels){
			new TransactionalCommand(session) {
				@Override
				protected void executeInTransaction() {
					openStockTake(getSession());
				}
			}.run();
		}

		List<Drug> allDrugs = DrugManager.getAllDrugs(session);
		
		monitor.beginTask("Updating stock levels for all stock", allDrugs.size());
		
		for (final Drug drug : allDrugs) {
			monitor.setTaskName("Update stock levels for " + drug.getName());
			
			List<Stock> stockForDrug = StockManager.getAllStockForDrug(session, drug);
			for (final Stock stock : stockForDrug) {
				if (monitor.isCanceled()){
					throw new OperationCanceledException("Stock level calculation cancelled");
				}
				
				new TransactionalCommand(session) {
					@Override
					protected void executeInTransaction() {
						monitor.subTask("Calculating for batch " + stock.getBatchNumber());
						StockLevel sl = StockManager.updateStockLevel(getSession(), stock);
						
						if (sl == null){
							return;
						}
						
						if ((sl.getFullContainersRemaining() < 0 ||
								sl.getLoosePillsRemaining() < 0) 
								&& adjustNegaiveLevels){
							
							int unitsRemaining = drug.getPackSize()
							* sl.getFullContainersRemaining() + sl.getLoosePillsRemaining();

							log.info("Creating adjustment for batch " + stock.getBatchNumber()
									+ " for drug " + drug.getName() + ": " + unitsRemaining);
							
							StockAdjustment st = new StockAdjustment();
							st.setAdjustedValue(unitsRemaining);
							st.setCaptureDate(new Date());
							st.setNotes("Zero negative stock level");
							st.setStock(stock);
							st.setStockCount(0);
							st.setStockTake(stockTake);
							getSession().save(st);
							
							StockManager.updateStockLevel(getSession(), stock);
						}
						
					}
				}.run();
				
				monitor.internalWorked(1.0/stockForDrug.size());
			}
		}
		
		if (adjustNegaiveLevels){
			new TransactionalCommand(session) {
				@Override
				protected void executeInTransaction() {
					closeStockTake(getSession());
				}
			}.run();
		}
		
		monitor.done();
	}

	private void closeStockTake(Session session) {
		stockTake.setEndDate(new Date());
		stockTake.setOpen(false);
		session.saveOrUpdate(stockTake);
	}

	private void openStockTake(Session session) {
		stockTake = new StockTake();
		stockTake.setOpen(true);
		stockTake.setStartDate(new Date());
		stockTake.setStockTakeNumber("Zero negative stock: " + new Date());
		session.save(stockTake);
	}

	@Override
	public String getHelpText() {
		String help = "This task recalculates ALL the iDART stock levels" +
				" based on the amounts received, dispensed, destroyed" +
				", returned and adjusted.\nIt can also create a stock take" +
				" and adjust any negative levels to zero if required.\n\n";
		help += "  Optional arguments:\n";
		help += "    -adjust: if this argument is passed then a stock take" +
				" will be created and negative stock levels will be adjusted to zero.\n" +
				"        Once complete it is possible to view the stock take report" +
				" by going to the reports screen.\n";
		help += "  Usage example:\n";
		help += "    go.bat recalculateStock -adjust (Windows)\n";
		help += "    ./go.sh recalculateStock -adjust (Linux)\n";
		return help;
	}

	@Override
	public String getDescription() {
		return "Recalculate iDART stock levels";
	}

}
