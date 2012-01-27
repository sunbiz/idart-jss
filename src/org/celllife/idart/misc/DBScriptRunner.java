package org.celllife.idart.misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.DatabaseTools;
import org.celllife.idart.misc.execute.ILogDevice;
import org.celllife.idart.misc.execute.Log4jDevice;
import org.celllife.idart.misc.execute.SysCommandExecutor;

public class DBScriptRunner {
	private String password;
	private String dbname;
	private String port;
	private String user;
	private String host;
	private static Logger log = Logger.getLogger(DBScriptRunner.class);

	public DBScriptRunner() {
		init();
	}

	private void init() {
		user = iDartProperties.hibernateUsername;

		password = iDartProperties.hibernatePassword;

		Map<String, String> map = DatabaseTools._().decomposeConnectionURL();
		dbname = map.get(DatabaseTools.DBNAME);
		host = map.get(DatabaseTools.DBHOST);
		port = map.get(DatabaseTools.DBPORT);
	}

	public String getDbname() {
		return dbname;
	}

	public File generatePgpassFile() {
		String userHome = System.getProperty("user.home");
		File pgpassFile = new File(userHome, "pgpass");

		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(pgpassFile));
			out.println(host + ":" + port + ":*:" + user + ":" + password);
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
				log.warn("Unable to chmod pgpass file.");
			} catch (InterruptedException e) {
				log.warn("Chmod opperation interrupted");
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

		command += getPsqlCommand("psql") + " -h " + host + " -p " + port
		+ " -d " + dbname + " -U " + user
		+ (isScript ? " -f " : " -c \"") + commandParam
		+ (isScript ? "" : "\"");

		return command;
	}

	protected String getPsqlCommand(String commandName) throws UpdateException {
		if (System.getProperty("os.name").toUpperCase().startsWith("WINDOWS")) {
			String postgresDir = "C:\\Program Files\\PostgreSQL";
			Double maxVersion = Double.valueOf(0d);
			File pg = new File("C:\\Program Files\\PostgreSQL");
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

	public void runUpdateScript() {
		runScript(getUpdateScriptPath());
	}

	private String getUpdateScriptPath() {
		String updateScriptFile = "idart_"
			+ iDartProperties.idartVersionNumber.substring(0, 3)
			+ ".0_to-latest-sqldiff.sql";

		File updateScript = new File(iDartProperties.updateScriptDir
				+ File.separator + updateScriptFile);

		if (!updateScript.exists()) {
			log.error("Update script does not exits: " + updateScriptFile);
			return "";
		}

		return updateScript.getAbsolutePath();
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

	public void dropDatabase() {
		try {
			String command = getPsqlCommand("dropdb") + " -h " + host + " -p "
			+ port + " -U " + user + " " + dbname;
			log.warn("Dropping database: " + dbname);
			run(command);
		} catch (UpdateException e) {
			log.error("Error dropping database.", e);
		}
	}

	public void createDatabase() {
		try {
			String command = getPsqlCommand("createdb") + " -h " + host
			+ " -p " + port + " -U " + user + " " + dbname;
			log.warn("Creating database: " + dbname);
			run(command);
		} catch (UpdateException e) {
			log.error("Error dropping database.", e);
		}
	}

	private void run(String command) {
		File pgpass = generatePgpassFile();
		SysCommandExecutor cmdExecutor = new SysCommandExecutor();
		cmdExecutor.setOutputLogDevice(new Log4jDevice(ILogDevice.mode.DEBUG));
		cmdExecutor.setErrorLogDevice(new Log4jDevice(ILogDevice.mode.ERROR));
		cmdExecutor.setEnvironmentVar("PGPASSFILE", pgpass.getAbsolutePath());
		try {
			log.info("Executing command: " + command);
			cmdExecutor.runCommand(command);
		} catch (Exception e) {
			log.warn("Error during command execution: " + e.getMessage());
		}
		if (pgpass.exists()) {
			pgpass.delete();
		}
	}
}
