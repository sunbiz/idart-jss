package org.celllife.idart.commonobjects;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.celllife.idart.misc.iDARTRuntimeException;

import com.pholser.util.properties.PropertyBinder;

public class PropertiesManager {

	private static enum Props{
		SMS("sms.properties"),
		IDART("idart.properties");
		
		private final String path;

		private Props(String path) {
			this.path = path;
		}
	}
	
	private static final Logger log = Logger.getLogger(PropertiesManager.class);
	
	private static Map<Props, Object> propMap = new HashMap<Props, Object>();
	private static Map<Props, Properties> rawPropMap = new HashMap<Props, Properties>();

	public static final SmsProperties sms() {
		if (propMap.get(Props.SMS) == null){
			loadProperties(SmsProperties.class, Props.SMS);
		}
		return (SmsProperties) propMap.get(Props.SMS);
	}
	
	public static final Properties smsRaw() {
		if (propMap.get(Props.SMS) == null){
			loadProperties(SmsProperties.class, Props.SMS);
		}
		return rawPropMap.get(Props.SMS);
	}

	/*package private*/ static <T> void loadProperties(Class<T> propClass, Props prop) {
		log.info("Loading properties for " + propClass);
		PropertyBinder<T> binder = PropertyBinder.forType(propClass);
		File file = new File(prop.path);
		try {
			FileInputStream inStream = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(inStream);
			rawPropMap.put(prop, properties);
			
			T bind = binder.bind(properties);
			propMap.put(prop, bind);
		} catch (IOException e) {
			throw new iDARTRuntimeException("Error reading properties from: " + prop.path, e);
		}
	}
	
	/**
	 * Generates a string of name value pairs for the fields in this class.
	 * 
	 * @return String listing all the values of the properties in this class.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException 
	 */
	public static String printProperties() {
		StringBuffer props = new StringBuffer();
		try {
			for (Props p : Props.values()){
				if (!propMap.containsKey(p)){
					continue;
				}
				Object object = propMap.get(p);
				Class<?>[] interfaces = object.getClass().getInterfaces();
				if (interfaces.length <= 0 ){
					continue;
				}
				Method[] methods = interfaces[0].getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					props.append(p).append("-");
					props.append(methods[i].getName());
					props.append(" : '");
					Method method = methods[i];
					Object value = method.invoke(object);
					String val = "";
					if (value != null) {
						val = value.getClass().cast(value).toString();
					}
					props.append(val);
					props.append("'\n");
				}
			}
			return props.toString();
		} catch (Exception e) {
			return "Error printing properties: " + e.getMessage();
		}
	}

	private PropertiesManager() {
	}

}
