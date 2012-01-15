/**
 * This abstract class encapsulates tasks with require JDBC connections to the database.
 * The generic procedure set out here is opening a connection, starting a transaction and
 * then running the task which will be implemented in subclasses
 */
package org.celllife.idart.misc.task;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author ilda
 *
 */
public abstract class JDBCTask implements IdartTask {

	protected static Connection connection;

	private static Logger log = Logger.getLogger(PackagedDrugsIndexCheck.class);

	@Override
	public void run(IProgressMonitor monitor) throws TaskException {

		try {
			monitor.setTaskName("Connecting to database");
			// open connection
			connection = JDBCUtil.currentSession();
			connection.setAutoCommit(false);

			// start transaction
			runTask(monitor);

			// commit transaction
			connection.commit();
		} catch (SQLException se) {
			log.error("SQLException in JDBCTask", se);
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new TaskException(e);
			}
			throw new TaskException(se);
		} catch (Exception e) {
			log.error("Exception in JDBCTask", e);
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new TaskException(e1);
			}
			throw new TaskException(e);
		} finally {
			try {
				JDBCUtil.closeJDBCConnection();
			} catch (SQLException e) {
				throw new TaskException(e);
			}
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}

	/**
	 * Performs the actual work of the task.
	 * 
	 * @param monitor
	 * @throws TaskException
	 *             if the task fails
	 */
	public abstract void runTask(IProgressMonitor monitor)
			throws TaskException;
}
