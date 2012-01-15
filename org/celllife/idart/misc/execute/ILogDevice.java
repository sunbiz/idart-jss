package org.celllife.idart.misc.execute;
public interface ILogDevice
{
	public enum mode {
		DEBUG, INFO, WARN, ERROR
	}
	public void log(String str);
}
