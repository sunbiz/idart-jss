/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.celllife.idart.database.hibernate.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.celllife.idart.commonobjects.iDartProperties;

/**
 * Created on 2007/02/26
 * 
 * JDBC Helper Class to allow easy access to database
 * 
 */
public class JDBCUtil {
	private static Log log = LogFactory.getLog(JDBCUtil.class);

	private static final ThreadLocal<Connection> connection = new ThreadLocal<Connection>();

	private static String drivers = null;

	private static String url = null;

	private static String username = null;

	private static String password = null;

	public static void rebuild() {
		drivers = iDartProperties.hibernateDriver;
		if (drivers != null) {
			System.setProperty("jdbc.drivers", drivers);
		}
		url = iDartProperties.hibernateConnectionUrl;
		username = iDartProperties.hibernateUsername;
		password = iDartProperties.hibernatePassword;
	}

	/**
	 * Method closeJDBCConnection.
	 * @throws SQLException
	 */
	public static void closeJDBCConnection() throws SQLException {

		Connection con = connection.get();
		if (con != null) {
			log.info("Closing JDBC connection to " + url);
			con.close();
		}
		connection.set(null);
	}

	/**
	 * Method currentSession.
	 * @return Connection
	 * @throws SQLException
	 */
	public static Connection currentSession() throws SQLException {
		if (url == null) {
			rebuild();
		}
		Connection con = connection.get();
		/*
		 * If Connection con is null, Create a connection and set it equal to
		 * Connection connection
		 */
		if (con == null) {
			log.info("Opening JDBC connection to " + url);
			con = DriverManager.getConnection(url, username, password);
			connection.set(con);
		}
		return con;
	}

	public static List<List<Object>> executeSQL(String sql, boolean selectOnly) throws DAOException {
		sql = sql.trim();
		boolean dataManipulation = false;

		String sqlLower = sql.toLowerCase();
		if (sqlLower.contains("insert") || sqlLower.contains("update") ||
				sqlLower.contains("delete") || sqlLower.contains("alter") ||
				sqlLower.contains("drop")  || sqlLower.contains("create")) {
			dataManipulation = true;
		}

		if (selectOnly && dataManipulation)
			throw new DAOException("Illegal command(s) found in query string");

		// (solution for junit tests that usually use hsql
		// hsql does not like the backtick.  Replace the backtick with the hsql
		// escape character: the double quote (or nothing).
		//		Dialect dialect = HibernateUtil.getDialect();
		//		if (HSQLDialect.class.getName().equals(dialect.getClass().getName()))
		//			sql = sql.replace("`", "");

		PreparedStatement ps = null;
		List<List<Object>> results = new Vector<List<Object>>();

		try {
			Connection conn = currentSession();
			ps = conn.prepareStatement(sql);

			if (dataManipulation == true) {
				Integer i = ps.executeUpdate();
				List<Object> row = new Vector<Object>();
				row.add(i);
				results.add(row);
			}
			else {
				ResultSet resultSet = ps.executeQuery();

				ResultSetMetaData rmd = resultSet.getMetaData();
				int columnCount = rmd.getColumnCount();

				while (resultSet.next()) {
					List<Object> rowObjects = new Vector<Object>();
					for (int x=1; x<=columnCount; x++) {
						rowObjects.add(resultSet.getObject(x));
					}
					results.add(rowObjects);
				}
			}
			ps.close();
		} catch (SQLException e) {
			log.error("Error while running sql: " + sql, e);
			throw new DAOException("Error while running sql: " + sql + " . Message: " + e.getMessage(), e);
		} finally {
			try {
				closeJDBCConnection();
			} catch (SQLException e) {
			}
		}

		return results;
	}

}
