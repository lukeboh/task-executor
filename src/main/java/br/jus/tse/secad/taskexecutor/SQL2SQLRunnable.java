package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.NamedParamStatement;
import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;
import br.jus.tse.secad.taskexecutor.util.PropertyQuery;

public class SQL2SQLRunnable implements Runnable {

	private static Logger log = Logger.getLogger(SQL2SQLRunnable.class);

	public int index;

	private SQL2SQLFactory factory;

	private HashMap<String, Object> namedParameterMap;

	public SQL2SQLRunnable(SQL2SQLFactory sql2sqlFactory, int index2, HashMap<String, Object> namedParameterMap) {
		this.factory = sql2sqlFactory;
		this.index = index2;
		this.namedParameterMap = namedParameterMap;
	}

	public void run() {
		Connection sourceConnection = null;
		Connection targetConnection = null;
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int countUpdate = 0;
		
		try {
			log.info("Entrada [" + index + "] params[" + namedParameterMap + "]");
			sourceConnection = factory.getSourceConnection();
			targetConnection = factory.getTargetConnection();

			for (int j = 1; j < PropertiesUtil.getPropertyQueryList().size(); j++) {
				PropertyQuery pq = PropertiesUtil.getPropertyQueryList().get(j);
				Connection connection = null;
				if ("source".equals(pq.getDbID())) {
					connection = sourceConnection;
				} else {
					connection = targetConnection;
				}

				NamedParamStatement namedParamStatement = new NamedParamStatement(connection, pq.getSql(),
						namedParameterMap);
				stmt = namedParamStatement.getPreparedStatement();

				if (!namedParamStatement.isUpdate()) {
					rs = stmt.executeQuery();
					if (!rs.next())
						return;

					namedParameterMap = new HashMap<String, Object>(rs.getMetaData().getColumnCount());

					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						String columnName = rs.getMetaData().getColumnName(i);
						Object columnValue = rs.getString(i);
						namedParameterMap.put(columnName, columnValue);
					}
				} else {
					countUpdate += stmt.executeUpdate();
				}
			}
			
			if (countUpdate > 0) {
				targetConnection.commit();
				log.info("Atualizado [" + index + "] params[" + namedParameterMap.get("COD_OBJETO") + "]"
						+ " quantidade [" + countUpdate + "]");
			} else {
				log.info("Não Atualizado[" + index + "] params[" + namedParameterMap.get("COD_OBJETO") + "]"
						+ " quantidade [" + countUpdate + "]");
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (sourceConnection != null)
				try {
					sourceConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (targetConnection != null)
				try {
					targetConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}