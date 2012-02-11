package org.celllife.idart.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;
import liquibase.statement.core.UpdateStatement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class fixes this bug: http://jira.cell-life.org/browse/IDART-290
 * 
 * <p>The class has two steps:
 * <p>First it looks for patients with duplicate patientid fields in the patient table
 * and renames them to id-duplicate.
 * <p>Secondly it looks for all patients that do not have any patientidentifers and adds
 * patientidentifiers for them.
 */
public class FixMissingIdentifiers implements CustomSqlChange {
	
	private static Logger log = Logger.getLogger(FixMissingIdentifiers.class);

	@Override
	public String getConfirmationMessage() {
		return "Fix patients with duplicate patientid's or missing patientidentifier's";
	}

	@Override
	public void setUp() throws SetupException {
	}

	@Override
	public void setFileOpener(ResourceAccessor arg0) {
	}

	@Override
	public ValidationErrors validate(Database arg0) {
		 return new ValidationErrors();
	}

	@Override
	public SqlStatement[] generateStatements(Database arg0)
			throws CustomChangeException {
		List<SqlStatement> statements = new ArrayList<SqlStatement>();
		
		JdbcConnection con = (JdbcConnection) arg0.getConnection();
		try {
			renameDuplicatePatientIds(statements, con);
			
			// need to execute these statements to avoid missing some missing elements in the next step
			arg0.execute(statements.toArray(new SqlStatement[] {}), new ArrayList<SqlVisitor>());
			
			statements = new ArrayList<SqlStatement>();
			insertMissingIdentifiers(statements, con);
			return statements.toArray(new SqlStatement[] {});
		} catch (DatabaseException e) {
			throw new CustomChangeException(e);
		} catch (SQLException e) {
			throw new CustomChangeException(e);
		} catch (LiquibaseException e) {
			throw new CustomChangeException(e);
		}
	}

	private void renameDuplicatePatientIds(List<SqlStatement> statements,
			JdbcConnection con) throws SQLException, DatabaseException {
		
		ResultSet rs_dupPatIds = con.prepareStatement("select patientid from patient group by patientid having count(patientid) > 1").executeQuery();
		while (rs_dupPatIds.next()){
			String patientid = rs_dupPatIds.getString("patientid");
			PreparedStatement statement = con.prepareStatement("select id from patient where patientid = ? order by id desc");
			statement.setString(1, patientid);
			ResultSet rs_patIds = statement.executeQuery();
			int duplicateCount = 0;
			while (rs_patIds.next()){
				int p_id = rs_patIds.getInt("id");
				PreparedStatement s_pid = con.prepareStatement("select id from patientidentifier where patient_id = ?" +
						" and value = ?");
				s_pid.setInt(1, p_id);
				s_pid.setString(2, patientid);
				ResultSet rs_patIdents = s_pid.executeQuery();
				
				markPatientIdAsDuplicate(statements, patientid, p_id, duplicateCount);
				if (rs_patIdents.next()){
					int ident_id = rs_patIdents.getInt("id");
					// there is an identifier for this patient with this value
					markPatientIdentifierAsDuplicate(statements, patientid, ident_id, duplicateCount);
				}
				duplicateCount++;
			}
		}
	}

	private void insertMissingIdentifiers(List<SqlStatement> statements,
			JdbcConnection con) throws SQLException, DatabaseException {
		ResultSet rs_patMissingIds = con.prepareStatement("select id, patientid from patient where patientid not in (select value from patientidentifier)").executeQuery();
		while (rs_patMissingIds.next()){
			int id = rs_patMissingIds.getInt("id");
			String patientid = rs_patMissingIds.getString("patientid");
			insertNewPatientIdentifier(statements, patientid, id);
		}
	}

	private void insertNewPatientIdentifier(List<SqlStatement> statements,
			String patientid, int p_id) {
		log.info("Creating new patient identifier for patient: id=" + p_id + " identifier= " + patientid);
		RawSqlStatement is = new RawSqlStatement("insert into patientidentifier (id, patient_id, value, type_id)" +
				" values (nextval('hibernate_sequence'), "+p_id+", '"
				+ patientid+"', 1)");
		statements.add(is);
	}

	private void markPatientIdentifierAsDuplicate(
			List<SqlStatement> statements, String patientid, int ident_id, int count) {
		
		if (count == 0){
			return;
		}
		
		log.info("Marking patient identifier as duplicate: " + patientid);
		UpdateStatement us = new UpdateStatement(null, "patientidentifier");
		us.addNewColumnValue("value", patientid + StringUtils.repeat("-duplicate", count));
		us.setWhereClause("id = ?");
		us.addWhereParameter(ident_id);
		statements.add(us);
	}

	private void markPatientIdAsDuplicate(List<SqlStatement> statements, String patientid,
			int id, int count) {
		if (count == 0){
			return;
		}
		log.info("Marking patientid as duplicate: " + patientid);
		UpdateStatement us = new UpdateStatement(null, "patient");
		us.addNewColumnValue("patientid", patientid + StringUtils.repeat("-duplicate", count));
		us.setWhereClause("id = ?");
		us.addWhereParameter(id);
		statements.add(us);
	}
}
