package org.celllife.idart.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import liquibase.change.custom.CustomSqlChange;
import liquibase.change.custom.CustomSqlRollback;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.UpdateStatement;

import org.celllife.idart.database.hibernate.AtcCode;
import org.celllife.idart.database.hibernate.ChemicalCompound;
import org.celllife.idart.database.hibernate.ChemicalDrugStrength;
import org.celllife.idart.database.hibernate.Drug;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.hibernate.Session;

public class LinkDrugsToAtcCodes_3_8_9 implements CustomSqlChange, CustomSqlRollback {

	@Override
	public String getConfirmationMessage() {
		return "Drugs mapped to ATC Codes";
	}

	@Override
	public void setFileOpener(ResourceAccessor fileOpener) {
	}

	@Override
	public void setUp() throws SetupException {
	}

	@Override
	public ValidationErrors validate(Database arg0) {
		return null;
	}
	
	@Override
	public liquibase.statement.SqlStatement[] generateStatements(Database arg0)
			throws CustomChangeException {
		Session sess = HibernateUtil.getNewSession();
		
		List<SqlStatement> statements = new ArrayList<SqlStatement>();
		
		@SuppressWarnings("unchecked")
		List<Drug> drugs = sess.createQuery("from Drug").list();
		for (Drug drug : drugs) {
			if (drug.getAtccode() != null){
				continue;
			}
			Set<ChemicalCompound> ccs = new HashSet<ChemicalCompound>();
			Set<ChemicalDrugStrength> cds = drug.getChemicalDrugStrengths();
			for (ChemicalDrugStrength cd : cds) {
				ccs.add(cd.getChemicalCompound());
			}
			
			Set<AtcCode> atccodes = drug.getAtccodes();
			if (atccodes == null){
				continue;
			}
			for (AtcCode atcCode : atccodes) {
				if (atcCode.containsExactChemicalCompounds(ccs)){
					statements.add(new UpdateStatement(null, "drug")
							.addNewColumnValue("atccode_id", atcCode.getId())
							.setWhereClause("id = " + drug.getId()));
					break;
				}
			}
		}
		return statements.toArray(new SqlStatement[statements.size()]);
	}

	@Override
	public SqlStatement[] generateRollbackStatements(Database arg0)
			throws CustomChangeException, UnsupportedChangeException,
			RollbackImpossibleException {
		return new SqlStatement[] {
				new UpdateStatement(null, "drug").addNewColumnValue("atccode_id", null)
		};
	}

}
