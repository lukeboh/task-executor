package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.NamedParamStatement;

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
		Connection targetConnection = null;
		PreparedStatement targetStatement = null;
		ResultSet targetResultSet = null;
		try {
			log.info("Entrada [" + namedParameterMap.get("COD_OBJETO") + "]");
			targetConnection = factory.getTargetConnection();

			String targetSql = factory.getProperties().getProperty("db.target.sql.1");
			NamedParamStatement namedParamStatement = new NamedParamStatement(targetConnection, targetSql,
					namedParameterMap);

			targetStatement = namedParamStatement.getPreparedStatement();

			int count = targetStatement.executeUpdate();
			if (count > 0) {
				targetConnection.commit();
				log.info("Atualizado [" + index + "] [" + namedParameterMap.get("COD_OBJETO") + "]" + " quantidade [" + count
						+ "]");
			} else {
				log.info("Não Atualizado[" + index + "] [" + namedParameterMap.get("COD_OBJETO") + "]" + " quantidade [" + count
					+ "]");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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