package org.celllife.idart.gui.reprintLabels.tableViewerUtils;

import org.celllife.idart.database.hibernate.tmp.PackageDrugInfo;

public class ReprintLabelsViewModel {
		String text;
		PackageDrugInfo pdi;
		Integer num;
		
		public ReprintLabelsViewModel(String text, Integer num) {
			this.text = text;
			this.num = num;
		}
		
		public ReprintLabelsViewModel(PackageDrugInfo pdi, Integer num) {
			this.pdi = pdi;
			this.num = num;
		}
		
		public String getDisplayText() {
			if (pdi != null){
				return "      " + pdi.getDrugName() + " " + pdi.getSummaryQtyInHand();
			}
			return text;
		}
		
		public boolean isDrug(){
			return pdi != null;
		}
		
		public PackageDrugInfo getPdi() {
			return pdi;
		}
		
		public Integer getNum() {
			return num;
		}

		public void setNum(Integer num) {
			this.num = num;
		}
	}