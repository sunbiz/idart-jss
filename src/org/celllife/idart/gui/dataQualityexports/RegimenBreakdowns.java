package org.celllife.idart.gui.dataQualityexports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.Regimen;
import org.celllife.idart.database.hibernate.RegimenDrugs;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import java.util.*;

public class RegimenBreakdowns extends DataQualityBase {

	protected List<Regimen> list1;
	protected List<Object[]> list2 = new ArrayList<Object[]>();
	private final String[] RegimenHeadings = new String[] { "DRUG GROUP NAME",
			"REGIMEN", "DRUG 1", "CHEMICAL COMPOUND", "DRUG 2",
			"CHEMICAL COMPOUND", "DRUG 3", "CHEMICAL COMPOUND", "DRUG 4",
			"CHEMICAL COMPOUND", "DRUG 5", "CHEMICAL COMPOUND", "DRUG 6",
			"CHEMICAL COMPOUND", };
    Date date = new Date(); 
	@SuppressWarnings("unchecked")
	@Override
	public void getData() {
		setHeadings(RegimenHeadings);

		list1 = HibernateUtil.getNewSession().createQuery("from Regimen ")
				.list();
		for (Regimen regimen : list1) {
			String[] obj = new String[2+regimen.getRegimenDrugs().size()*2];

			obj[0] = (regimen.getRegimenName().replace(",", "/"));
			obj[1] = regimen.getDrugGroup();

			int i = 2;
			for (RegimenDrugs rd : regimen.getRegimenDrugs()) {

				if (rd != null) {
					Drug drug = rd.getDrug();
					obj[i] = drug.getName();
					i++;
					for (ChemicalDrugStrength cds : drug.getChemicalDrugStrengths()) {
						if (obj[i] == null){
							obj[i] = "";
						}
						obj[i] = obj[i] + cds.getChemicalCompound().getName() + " / ";
					}
					if (obj[i] != null){
						obj[i] = obj[i].substring(0,obj[i].length() - 3);
					}
					i++;
				} else
					break;
			}
			data.add(obj);
		}

	}

	@Override
	public String getFileName() {
		return "Regimen Breakdowns -"+ new SimpleDateFormat("dd.MM.yyyy").format(date)+" - IDART(regimen breakdowns)" ;
	}

	@Override
	public void performJob(IProgressMonitor monitor) throws ReportException {
		super.performJob(monitor);

	}

	@Override
	public String getMessage() {
		return "Regimen Breakdowns";

	}

	
}
