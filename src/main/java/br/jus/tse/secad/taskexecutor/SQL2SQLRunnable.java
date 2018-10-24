package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.NamedParamStatement;
import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;

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
		PreparedStatement sourceStatement = null;
		PreparedStatement targetStatement = null;
		ResultSet sourceResultSet = null;
		ResultSet targetResultSet = null;
		try {
			log.info("Entrada [" + index + "] params[" +  namedParameterMap + "]");
			sourceConnection = factory.getSourceConnection();
			targetConnection = factory.getTargetConnection();

			String sourceSql = PropertiesUtil.getProperty("db.source.sql.1");
			NamedParamStatement namedParamStatement = new NamedParamStatement(sourceConnection, sourceSql,
					namedParameterMap);

			sourceStatement = namedParamStatement.getPreparedStatement();
			
			sourceResultSet = sourceStatement.executeQuery();
			if (!sourceResultSet.next())
				return;
			
			HashMap<String, Object> namedParameterMap = new HashMap<String, Object>(sourceResultSet.getMetaData().getColumnCount());
			
			for(int i = 1; i <= sourceResultSet.getMetaData().getColumnCount(); i++) {
				String columnName = sourceResultSet.getMetaData().getColumnName(i);
				Object columnValue = sourceResultSet.getString(i);
				namedParameterMap.put(columnName, columnValue);
			}
			
			String targetSql = PropertiesUtil.getProperty("db.target.sql.1");
			namedParamStatement = new NamedParamStatement(targetConnection, targetSql,
					namedParameterMap);
			
			targetStatement = namedParamStatement.getPreparedStatement();
			
			int count = targetStatement.executeUpdate();
			if (count > 0) {
				targetConnection.commit();
				log.info("Atualizado [" +  index + "] params[" +  namedParameterMap.get("COD_OBJETO") + "]" + " quantidade [" + count
						+ "]");
			} else {
				log.info("Não Atualizado[" + index + "] params[" +  namedParameterMap.get("COD_OBJETO") + "]" + " quantidade [" + count
					+ "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sourceResultSet != null) {
				try {
					sourceResultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (sourceStatement != null) {
				try {
					sourceStatement.close();
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
			if (targetResultSet != null) {
				try {
					targetResultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (targetStatement != null) {
				try {
					targetStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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