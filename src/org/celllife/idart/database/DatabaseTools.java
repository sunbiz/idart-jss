package org.celllife.idart.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.hibernate.util.JDBCUtil;
import org.celllife.idart.misc.UpdateException;
import org.celllife.idart.misc.Version;
import org.celllife.idart.misc.execute.ILogDevice;
import org.celllife.idart.misc.execute.Log4jDevice;
import org.celllife.idart.misc.execute.SysCommandExecutor;
import org.hibernate.JDBCException;

public class DatabaseTools {

	private static final String SOUTH_AFRICA_CLINICS = "org/celllife/idart/database/changelog-South_Africa_clinics.xml";
	private static final String NIGERIA_CLINICS = "org/celllife/idart/database/changelog-Nigeria_clinics.xml";

	private static Logger log = Logger.getLogger(DatabaseTools.class);

	private static final String MASTER_CHANGELOG = "org/celllife/idart/database/changelog-master.xml";
	private static final String BASELINE_CHANGELOG_CORE = "org/celllife/idart/database/baseline-with-core-data.xml";
	private static final String BASELINE_CHANGELOG_TEST = "org/celllife/idart/database/baseline-with-test-data.xml";
	public static final String TEST_CHANGELOG = "org/celllife/idart/database/changelog-test.xml";
	public static final String DBNAME = "DBNAME";
	public static final String DBPORT = "DBPORT";
	public static final String DBHOST = "DBHOST";

	private static DatabaseTools instance;
	private Map<String, String> map;

	private DatabaseTools() {
		map = decomposeConnectionURL();
	}

	public static DatabaseTools _() {
		if (instance == null) {
			instance = new DatabaseTools();
		}
		return instance;
	}

	public boolean checkConnection() {
		try {
			JDBCUtil.currentSession();
			JDBCUtil.closeJDBCConnection();
			return true;
		} catch (SQLException e) {
			log.error("Error creating hibernate connection.", e);
		}
		return false;
	}

	public boolean checkDatabase() throws ConnectException, DatabaseException, DatabaseEmptyException {
		if (!checkConnection())
			throw new ConnectException("Unable to connect to database");

		if (!getTables().contains("users"))
			throw new DatabaseEmptyException(
				"Database is missing some/all tables.");

		try {
			List<List<Object>> list = JDBCUtil.executeSQL("select id from users", true);
			return list.size() > 0;
		} catch (Exception e) {
			throw new DatabaseException("Error retreiving user list.", e);
		}
	}

	public String composeUrl(Map<String, String> propMap) {
		return String.format("jdbc:postgresql://%s:%s/%s", propMap.get(DBHOST),
				propMap.get(DBPORT), propMap.get(DBNAME));
	}

	public boolean createDatabase(boolean includeTest, boolean runUpdate) {
		if (includeTest) {
			try {
				update(BASELINE_CHANGELOG_TEST);
			} catch (DatabaseException e) {
				log.error("Error creating database.", e);
				return false;
			}
		} else {
			try {
				update(BASELINE_CHANGELOG_CORE);
			} catch (DatabaseException e) {
				log.error("Error creating database.", e);
				return false;
			}
		}
		if (runUpdate) {
			try {
				update();
			} catch (DatabaseException e) {
				log.error("Error updating database.", e);
				return false;
			}
		}
		return true;
	}

	public Map<String, String> decomposeConnectionURL() {
		Map<String, String> propMap = new HashMap<String, String>();
		String fullUrl = iDartProperties.hibernateConnectionUrl;
		String[] splitUrl = fullUrl.split("/");

		propMap.put(DBNAME, splitUrl[splitUrl.length - 1]);
		String hostAndPort = splitUrl[splitUrl.length - 2];
		propMap.put(DBHOST, hostAndPort.split(":")[0]);
		propMap.put(DBPORT, hostAndPort.split(":")[1]);

		return propMap;
	}

	private File generatePgpassFile() {
		String userHome = System.getProperty("user.home");
		File pgpassFile = new File(userHome, "pgpass");

		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(pgpassFile));
			out.println(map.get(DatabaseTools.DBHOST) + ":"
					+ map.get(DatabaseTools.DBPORT) + ":*:"
					+ iDartProperties.hibernateUsername + ":"
					+ iDartProperties.hibernatePassword);
		} catch (IOException e) {
			log.error("Unable to write to pgpass file.");
		} finally {
			if (out != null) {
				out.close();
			}
		}

		if (!System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")) {
			Process pChmod;
			try {
				pChmod = Runtime.getRuntime().exec(
						"chmod 600 " + pgpassFile.getAbsolutePath());
				pChmod.waitFor();
			} catch (IOException e) {
				log.error("Unable to chmod pgpass file.");
			} catch (InterruptedException e) {
				log.error("Chmod opperation interrupted");
			}

		}

		return pgpassFile;
	}

	private String getCommand(String toExecute, boolean isScript)
	throws UpdateException {
		String commandParam = toExecute;
		if (isScript) {
			File script = new File(toExecute);

			if (!script.exists()) {
				log.error("Script does not exits: " + toExecute);
				return "";
			}

			commandParam = script.getAbsolutePath();
		}

		String command = "";

		String osName = System.getProperty("os.name");
		if (osName.equalsIgnoreCase("windows vista")) {

		}
		if (osName.toUpperCase().startsWith("WINDOWS")) {
			if (osName.contains("98")) {
				command = "command.com /C";
			} else {
				command = "cmd /C";
			}
		}

		command += getPsqlCommand("psql") + " -h " + map.get(DBHOST) + " -p "
		+ map.get(DBPORT) + " -d " + map.get(DBNAME) + " -U "
		+ iDartProperties.hibernateUsername
		+ (isScript ? " -f " : " -c \"") + commandParam
		+ (isScript ? "" : "\"");

		return command;
	}

	public List<String> getTables() throws DatabaseException {
		try {
			List<String> tables = new ArrayList<String>();
			Connection conn = JDBCUtil.currentSession();
			DatabaseMetaData md = conn.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				tables.add(rs.getString(3));
			}
			JDBCUtil.closeJDBCConnection();
			return tables;
		} catch (SQLException e) {
			throw new DatabaseException("Error retreiving list of tables.");
		}
	}

	public boolean isOldVersion() {
		try {
			List<List<Object>> result = JDBCUtil.executeSQL(
					"SELECT value FROM simpledomain "
					+ "WHERE name = 'database_version'", true);
			if (result.size() <= 0)
				return false;
			List<Object> row = result.get(0);
			if (row.size() <= 0)
				return false;
			String versionString = (String) row.get(0);
			Version threeFiveZero = Version.parse("3.5.0");
			Version v = Version.parse(versionString);
			if (v.compareTo(threeFiveZero) < 0)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	private void safeUpdate(String changelog, Connection session)
	throws Exception, JDBCException {
		ResourceAccessor fileOpener = new ClassLoaderResourceAccessor();
		DatabaseFactory databaseFactory = DatabaseFactory.getInstance();
		log.info("Running liquibase file: " + changelog);
		
		Database database = databaseFactory
				.findCorrectDatabaseImplementation(new JdbcConnection(session));

		Liquibase liquibase = new Liquibase(changelog, fileOpener, database);

		// http://trac.jmatter.org/trac/browser/jmatter-complet/trunk/jmatter
		// /src/com/u2d/persist/LiquibaseCommander.java?rev=1387

		liquibase.forceReleaseLocks();
		liquibase.update(null);

	}

	public void update() throws DatabaseException {
		update(MASTER_CHANGELOG);

		if(iDartProperties.country.equalsIgnoreCase("South Africa")) {
			if (checkClinicCount())
				return;
			
			update(SOUTH_AFRICA_CLINICS);
		} else if(iDartProperties.country.equalsIgnoreCase("Nigeria")) {
			update(NIGERIA_CLINICS);
		}
	}

	/**
	 * Liquibase takes a long time to compute the checksum for the
	 * national clinics changeset to rather just check the count
	 * 
	 * @return
	 */
	private boolean checkClinicCount() {
		int nationalClinicCount = getNationalClinicCount();
		if(iDartProperties.country.equalsIgnoreCase("South Africa")) {
			return nationalClinicCount == 4199;
		}
		return true;
	}

	private int getNationalClinicCount() {
		try {
			ResultSet rs = JDBCUtil.currentSession()
					.prepareStatement("select count(*) from nationalclinics")
					.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			log.error("Error getting national clinic count",e);
			return 0;
		}
	}

	public void update(String changelog) throws DatabaseException {
		try {
			safeUpdate(changelog, JDBCUtil.currentSession());
			
		} catch (Exception e) {
			log.error("Error updating database.", e);
			throw new DatabaseException(e);
		} finally {
			try {
				JDBCUtil.closeJDBCConnection();
			} catch (SQLException e) {
			}
		}

	}

	public void createDatabase() {
		try {
			String command = getPsqlCommand("createdb") + " -h "
			+ map.get(DatabaseTools.DBHOST) + " -p "
			+ map.get(DatabaseTools.DBPORT) + " -U "
			+ iDartProperties.hibernateUsername + " "
			+ map.get(DatabaseTools.DBNAME);
			log.warn("Creating database: " + map.get(DatabaseTools.DBNAME));
			run(command);
		} catch (UpdateException e) {
			log.error("Error dropping database.", e);
		}
	}

	public void dropDatabase() {
		try {
			String command = getPsqlCommand("dropdb") + " -h "
			+ map.get(DatabaseTools.DBHOST) + " -p "
			+ map.get(DatabaseTools.DBPORT) + " -U "
			+ iDartProperties.hibernateUsername + " "
			+ map.get(DatabaseTools.DBNAME);
			log.warn("Dropping database: " + map.get(DatabaseTools.DBNAME));
			run(command);
		} catch (UpdateException e) {
			log.error("Error dropping database.", e);
		}
	}

	private String getPsqlCommand(String commandName) throws UpdateException {
		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")) {
			String postgresDir = "C:\\Program Files\\PostgreSQL";
			Double maxVersion = Double.valueOf(0d);
			File pg = new File(postgresDir);
			File[] listFiles = pg.listFiles();
			for (File file : listFiles) {
				try {
					if (file.isDirectory()) {
						Double version = Double.valueOf(file.getName());
						if (version > maxVersion) {
							maxVersion = version;
						}
					}
				} catch (Exception e) {
				}
			}
			if (maxVersion > 8) {
				postgresDir = postgresDir + "\\" + maxVersion.toString();
			}
			File psql = new File(postgresDir + "\\bin\\" + commandName + ".exe");
			if (!psql.exists())
				throw new UpdateException("Can not find command:" + commandName);

			return "\"" + psql.getAbsolutePath() + "\"";
		} else
			return commandName;
	}

	private void run(String command) {
		File pgpass = generatePgpassFile();
		SysCommandExecutor cmdExecutor = new SysCommandExecutor();
		cmdExecutor.setOutputLogDevice(new Log4jDevice(ILogDevice.mode.DEBUG));
		cmdExecutor.setErrorLogDevice(new Log4jDevice(ILogDevice.mode.ERROR));
		// cmdExecutor.setEnvironmentVar("PGPASSFILE",
		// pgpass.getAbsolutePath());
		try {
			log.info("Executing command: " + command);
			cmdExecutor.runCommand(command);
		} catch (Exception e) {
			log.error("Error during command execution",e);
		}
		if (pgpass.exists()) {
			pgpass.delete();
		}
	}

	public void runScript(String scriptPath) {
		try {
			String command = getCommand(scriptPath, true);
			log.debug(command);
			run(command);
		} catch (UpdateException e) {
			log.error("Error update database.", e);
		}
	}

	public void refresh() {
		map = decomposeConnectionURL();
	}
}
