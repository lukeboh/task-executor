package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.ConnectionUtil;
import br.jus.tse.secad.taskexecutor.util.NamedParameterMap;
import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;
import br.jus.tse.secad.taskexecutor.util.PropertyQuery;

public class SQL2SQLFactory implements TaskFactory {

	private static Logger log = Logger.getLogger(SQL2SQLFactory.class);
	int size = 0;
	int index = 0;
	int bulkSize = 1;
	private ResultSet rs;

	public SQL2SQLFactory() throws Exception {
		
		bulkSize = PropertiesUtil.getBulkSize();

		PropertyQuery pq0 = PropertiesUtil.getPropertyQueryList().get(0);

		Connection connection = null;

		if ("source".equals(pq0.getDbID())) {
			connection = getSourceConnection();
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

	public Task next() {
		try {
			NamedParameterMap npm = new NamedParameterMap(bulkSize, rs);
			if (!npm.isEmpty()) {
				index += npm.getSize();
				return new SQL2SQLTask(this, index, npm);	
			} else {
				return null;
			}
			
		} catch (SQLException e1) {
			log.error("Falha de banco", e1);
		}
		return null;
	}

	public int getSize() {
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
		// dte.setThreadCount(1);
	}

}
