package org.celllife.idart.misc.execute;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Usage of following class can go as ...
 * <P>
 * 
 * <PRE>
 * &lt;CODE&gt;
 * 		SysCommandExecutor cmdExecutor = new SysCommandExecutor();
 * 		cmdExecutor.setOutputLogDevice(new LogDevice());
 * 		cmdExecutor.setErrorLogDevice(new LogDevice());
 * 		int exitStatus = cmdExecutor.runCommand(commandLine);
 * &lt;/CODE&gt;
 * </PRE>
 * 
 * </P>
 * 
 * OR
 * 
 * <P>
 * 
 * <PRE>
 * &lt;CODE&gt;
 * 		SysCommandExecutor cmdExecutor = new SysCommandExecutor();
 * 		int exitStatus = cmdExecutor.runCommand(commandLine);
 * 		String cmdError = cmdExecutor.getCommandError();
 * 		String cmdOutput = cmdExecutor.getCommandOutput();
 * &lt;/CODE&gt;
 * </PRE>
 * 
 * </P>
 */
public class SysCommandExecutor {
	private ILogDevice fOuputLogDevice = null;
	private ILogDevice fErrorLogDevice = null;
	private String fWorkingDirectory = null;
	private Map<String, String> fEnvironmentVarMap = null;

	private StringBuffer fCmdOutput = null;
	private StringBuffer fCmdError = null;
	private AsyncStreamReader fCmdOutputThread = null;
	private AsyncStreamReader fCmdErrorThread = null;

	public void setOutputLogDevice(ILogDevice logDevice) {
		fOuputLogDevice = logDevice;
	}

	public void setErrorLogDevice(ILogDevice logDevice) {
		fErrorLogDevice = logDevice;
	}

	public void setWorkingDirectory(String workingDirectory) {
		fWorkingDirectory = workingDirectory;
	}

	public void setEnvironmentVar(String name, String value) {
		if (fEnvironmentVarMap == null) {
			fEnvironmentVarMap = new HashMap<String, String>();
		}

		fEnvironmentVarMap.put(name, value);
	}

	public String getCommandOutput() {
		return fCmdOutput.toString();
	}

	public String getCommandError() {
		return fCmdError.toString();
	}

	public int runCommand(String commandLine) throws Exception {
		/* run command */
		Process process = runCommandHelper(commandLine);

		/* start output and error read threads */
		startOutputAndErrorReadThreads(process.getInputStream(), process
				.getErrorStream());

		/* wait for command execution to terminate */
		int exitStatus = -1;
		try {
			exitStatus = process.waitFor();

		} catch (Throwable ex) {
			throw new Exception(ex.getMessage());

		} finally {
			/* notify output and error read threads to stop reading */
			notifyOutputAndErrorReadThreadsToStopReading();
		}

		return exitStatus;
	}

	private Process runCommandHelper(String commandLine) throws IOException {
		Process process = null;
		if (fWorkingDirectory == null) {
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens());
		} else {
			process = Runtime.getRuntime().exec(commandLine, getEnvTokens(),
					new File(fWorkingDirectory));
		}

		return process;
	}

	private void startOutputAndErrorReadThreads(InputStream processOut,
			InputStream processErr) {
		fCmdOutput = new StringBuffer();
		if (fOuputLogDevice != null) {
			fCmdOutputThread = new AsyncStreamReader(processOut, fCmdOutput,
					fOuputLogDevice, "OUTPUT");
			fCmdOutputThread.start();
		}

		fCmdError = new StringBuffer();
		if (fErrorLogDevice != null) {
			fCmdErrorThread = new AsyncStreamReader(processErr, fCmdError,
					fErrorLogDevice, "ERROR");
			fCmdErrorThread.start();
		}

	}

	private void notifyOutputAndErrorReadThreadsToStopReading() {
		if (fCmdOutputThread != null) {
			fCmdOutputThread.stopReading();
		}
		if (fCmdErrorThread != null) {
			fCmdErrorThread.stopReading();
		}
	}

	private String[] getEnvTokens() {
		if (fEnvironmentVarMap == null)
			return null;

		String[] envTokenArray = new String[fEnvironmentVarMap.size()];

		int nEnvVarIndex = 0;
		for (String key : fEnvironmentVarMap.keySet()) {
			String envVarToken = key + "=" + fEnvironmentVarMap.get(key);
			envTokenArray[nEnvVarIndex++] = envVarToken;
		}

		return envTokenArray;
	}
}
