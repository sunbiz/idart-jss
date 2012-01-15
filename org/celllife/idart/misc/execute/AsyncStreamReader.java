package org.celllife.idart.misc.execute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class AsyncStreamReader extends Thread {
	private StringBuffer fBuffer = null;
	private InputStream fInputStream = null;
	private String fThreadId = null;
	private boolean fStop = false;
	private ILogDevice fLogDevice = null;
	private final String fNewLine;

	public AsyncStreamReader(InputStream inputStream, StringBuffer buffer,
			ILogDevice logDevice, String threadId) {
		fInputStream = inputStream;
		fBuffer = buffer;
		fThreadId = threadId;
		fLogDevice = logDevice;

		fNewLine = System.getProperty("line.separator");
	}

	public String getBuffer() {
		return fBuffer.toString();
	}

	@Override
	public void run() {
		try {
			readCommandOutput();
		} catch (Exception ex) {
			// ex.printStackTrace(); //DEBUG
		}
	}

	private void readCommandOutput() throws IOException {
		BufferedReader bufOut = new BufferedReader(new InputStreamReader(
				fInputStream));
		String line = null;
		while ((fStop == false) && ((line = bufOut.readLine()) != null)) {
			fBuffer.append(line + fNewLine);
			printToDisplayDevice(line);
		}
		bufOut.close();
		// printToConsole("END OF: " + fThreadId); //DEBUG

	}

	private void printToDisplayDevice(String line) {
		if (fLogDevice != null) {
			fLogDevice.log(line);
		} else {
			// printToConsole(line);//DEBUG
		}
	}


	public void stopReading() {
		fStop = true;
	}

	private void printToDisplayDevice(char c) {
		if (fLogDevice != null) {
			fLogDevice.log(String.valueOf(c));
		} else {
			printToConsole(c);
		}
	}

	private synchronized void printToConsole(char c) {
		System.out.print(c);
	}
}
