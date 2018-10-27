package br.jus.tse.secad.taskexecutor.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
	private static Properties properties;
	private static List<PropertyQuery> propertyQueryList = new ArrayList<>();
	private static HashMap<String, PropertyQuery> propertyQueryMap = new HashMap<>();
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	private static final String QUEUE_SIZE = "queue.size";
	private static final String BULK_SIZE = "bulk.size";
	private static final String SQL_PREFIX = "db.sql";
	private static final String SQL_SIZE_SUFFIX = ".size";

	static {
		properties = new Properties();
		try {
			properties.load(new FileInputStream(CONFIG_PROPERTIES_FILENAME));
			loadQueryList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getQueueSize() {
		return Integer.parseInt(getProperty(QUEUE_SIZE, "200"));
	}
	
	public static int getBulkSize() {
		return Integer.parseInt(getProperty(BULK_SIZE, "1"));
	}

	public static List<PropertyQuery> getPropertyQueryList() {
		return propertyQueryList;
	}

	public static String getProperty(String property) {
		return properties.getProperty(property);
	}

	public static String getProperty(String property, String defautValue) {
		return properties.getProperty(property, defautValue);
	}

	public static Properties getProperties() {
		return properties;
	}
	
	private static void loadQueryList() {
		for (Map.Entry<Object, Object> pair : properties.entrySet()) {
			String key = (String) pair.getKey();
			if (key.startsWith(SQL_PREFIX)) {
				int i = key.indexOf(SQL_SIZE_SUFFIX);

				if (i > 0) {
					PropertyQuery pq = propertyQueryMap.get(key.substring(0, i));
					if (pq == null) {
						pq = new PropertyQuery(key.substring(0, i), null);
						propertyQueryMap.put(key.substring(0, i), pq);
					}
					pq.setSqlSize((String) pair.getValue());
				} else {
					PropertyQuery pq = propertyQueryMap.get(key);
					if (pq == null) {
						pq = new PropertyQuery(key, (String) pair.getValue());
						propertyQueryMap.put(key, pq);
					} else {
						pq.setSql((String) pair.getValue());
					}
				}
			}
		}

		propertyQueryList = new ArrayList<PropertyQuery>(propertyQueryMap.values());
		Collections.sort(propertyQueryList);
		propertyQueryList = Collections.unmodifiableList(propertyQueryList);
	}

}
