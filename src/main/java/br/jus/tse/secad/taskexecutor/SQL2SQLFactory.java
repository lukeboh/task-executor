package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.ConnectionUtil;
import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;
import br.jus.tse.secad.taskexecutor.util.PropertyQuery;

public class SQL2SQLFactory implements RunnableFactory {
	
	private static Logger log = Logger.getLogger(SQL2SQLFactory.class);
	int size = 0;
	int index = 0;
	private ResultSet rs;

	

	public SQL2SQLFactory() throws Exception {
		
		PropertyQuery pq0 = PropertiesUtil.getPropertyQueryList().get(0);
		
		Connection connection = null;
		
		if ("source".equals(pq0.getDbID())) {
			connection =  getSourceConnection();
		} else {
			connection = getTargetConnection();
		}
		
		PreparedStatement stmt = connection.prepareStatement(pq0.getSqlSize());
		rs = stmt.executeQuery();
		rs.next();
		size = rs.getInt(1);
		log.info("Size [" + size + "]");
		
		rs.close();
		stmt.close();
		
		stmt = connection.prepareStatement(pq0.getSql());
		rs = stmt.executeQuery();
	}
	
	
	public Runnable next() {
		try {
			if (!rs.next())
				return null;
			else {
				index++; 
				
				HashMap<String, Object> namedParameterMap = new HashMap<String, Object>(rs.getMetaData().getColumnCount());
				
				for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String columnName = rs.getMetaData().getColumnName(i);
					Object columnValue = rs.getString(i);
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
