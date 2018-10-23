package br.jus.tse.secad.taskexecutor;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SQL2SQLFactory implements RunnableFactory {
	
	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	private static final String JDBC_DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";

	private static final String DB_SOURCE_USER = "db.source.user";
	private static final String DB_SOURCE_PASSWORD = "db.source.password";
	private static final String DB_SOURCE_URL = "db.source.url";
	private static final String DB_SOURCE_SQL_SIZE = "db.source.sql.size";
	private static final String DB_SOURCE_SQL = "db.source.sql";

	private static final String DB_TARGET_USER = "db.target.user";
	private static final String DB_TARGET_PASSWORD = "db.target.password";
	private static final String DB_TARGET_URL = "db.target.url";
	
	
	private static Logger log = Logger.getLogger(SQL2SQLFactory.class);

	int size = 0;

	int index = 0;

	private Connection sourceConnection;

	private ResultSet sourceResultSet;

	private Properties properties;

	public SQL2SQLFactory() throws Exception {
		Class.forName(JDBC_DRIVER_NAME);
		
		properties = new Properties();
		properties.load(new FileInputStream(CONFIG_PROPERTIES_FILENAME));
		
		sourceConnection = getSourceConnection();
		
		String sourceSqlSize = properties.getProperty(DB_SOURCE_SQL_SIZE);
		PreparedStatement sourceStatement = sourceConnection.prepareStatement(sourceSqlSize);
		sourceResultSet = sourceStatement.executeQuery();
		sourceResultSet.next();
		size = sourceResultSet.getInt(1);
		log.info("Size [" + size + "]");
		
		sourceResultSet.close();
		sourceStatement.close();
		
		String sourceSql = properties.getProperty(DB_SOURCE_SQL);
		sourceStatement = sourceConnection.prepareStatement(sourceSql);
		sourceResultSet = sourceStatement.executeQuery();
	}
	
	
	public Runnable next() {
		try {
			if (!sourceResultSet.next())
				return null;
			else {
				index++;
				
				HashMap<String, Object> namedParameterMap = new HashMap<String, Object>(sourceResultSet.getMetaData().getColumnCount());
				
				for(int i = 1; i <= sourceResultSet.getMetaData().getColumnCount(); i++) {
					String columnName = sourceResultSet.getMetaData().getColumnName(i);
					Object columnValue = sourceResultSet.getString(i);
					namedParameterMap.put(columnName, columnValue);
				}
				
				return new SQL2SQLRunnable(this, index, namedParameterMap);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public int size() {
		return size;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public Connection getSourceConnection() throws ClassNotFoundException, SQLException {
		String url = properties.getProperty(DB_SOURCE_URL);
		String user = properties.getProperty(DB_SOURCE_USER);
		String password = properties.getProperty(DB_SOURCE_PASSWORD);
		
		return createConnection(url, user, password);
	}
	
	public Connection getTargetConnection() throws ClassNotFoundException, SQLException {
		String url = properties.getProperty(DB_TARGET_URL);
		String user = properties.getProperty(DB_TARGET_USER);
		String password = properties.getProperty(DB_TARGET_PASSWORD);
		
		return createConnection(url, user, password);
	}
	
	private Connection createConnection(String url, String user, String password) throws ClassNotFoundException, SQLException {
		Connection c = DriverManager.getConnection(url, user, password);
		c.setAutoCommit(false);
		return c;
	}
	
	public static void main(String[] args) throws Exception {
		DefaultTaskExecutor dte = new DefaultTaskExecutor(new SQL2SQLFactory());
		dte.showUI();
		dte.start();
		//dte.setThreadCount(1);
	}

}
