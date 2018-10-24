package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.ConnectionUtil;
import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;

public class SQL2SQLFactory implements RunnableFactory {
	
	private static Logger log = Logger.getLogger(SQL2SQLFactory.class);
	int size = 0;
	int index = 0;
	private ResultSet sourceResultSet;

	

	public SQL2SQLFactory() throws Exception {
		Connection sourceConnection = getSourceConnection();
		
		String sourceSqlSize = PropertiesUtil.getSourceSqlSize();
		PreparedStatement sourceStatement = sourceConnection.prepareStatement(sourceSqlSize);
		sourceResultSet = sourceStatement.executeQuery();
		sourceResultSet.next();
		size = sourceResultSet.getInt(1);
		log.info("Size [" + size + "]");
		
		sourceResultSet.close();
		sourceStatement.close();
		
		String sourceSql = PropertiesUtil.getSourceSql();
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
	
	public Connection getSourceConnection() throws ClassNotFoundException, SQLException {
		return ConnectionUtil.getSourceConnection();
	}
	
	public Connection getTargetConnection() throws ClassNotFoundException, SQLException {
		return ConnectionUtil.getTargetConnection();
	}
	
	
	public static void main(String[] args) throws Exception {
		DefaultTaskExecutor dte = new DefaultTaskExecutor(new SQL2SQLFactory());
		dte.showUI();
		dte.start();
		//dte.setThreadCount(1);
	}

}
