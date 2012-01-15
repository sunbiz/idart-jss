package org.celllife.idart.misc.execute;

import org.apache.log4j.Logger;

public class Log4jDevice implements ILogDevice {

	private static Logger log = Logger.getLogger(Log4jDevice.class);

	private final ILogDevice.mode mode;

	public Log4jDevice(mode mode) {
		super();
		this.mode = mode;
	}

	@Override
	public void log(String str) {
		switch (mode) {
		case DEBUG:
			log.debug(str);
			break;
		case INFO:
			log.info(str);
			break;
		case WARN:
			log.warn(str);
			break;
		case ERROR:
			log.error(str);
			break;
		default:
			break;
		}

	}

}
