package br.jus.tse.secad.taskexecutor.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
	private static Properties properties;
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	private static final String DB_SOURCE_SQL_SIZE = "db.source.sql.size";
	private static final String DB_SOURCE_SQL = "db.source.sql";
	private static final String QUEUE_SIZE = "queue.size";
	

	static {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(CONFIG_PROPERTIES_FILENAME));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getSourceSqlSize() {
		return getProperty(DB_SOURCE_SQL_SIZE);
	}

	public static String getSourceSql() {
		return getProperty(DB_SOURCE_SQL);
	}
	
	public static int getQueueSize() {
		return Integer.parseInt(getProperty(QUEUE_SIZE, "200"));
	}
	
	public static String getProperty(String property) {
		return properties.getProperty(property);
	}
	
	public static String getProperty(String property, String defautValue) {
		return properties.getProperty(property, defautValue);
	}

}
