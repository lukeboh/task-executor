package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import br.jus.tse.secad.taskexecutor.util.NamedParamStatement;
import br.jus.tse.secad.taskexecutor.util.NamedParameterMap;
import br.jus.tse.secad.taskexecutor.util.PropertiesUtil;
import br.jus.tse.secad.taskexecutor.util.PropertyQuery;

public class SQL2SQLRunnable implements Runnable {

	private static Logger log = Logger.getLogger(SQL2SQLRunnable.class);

	public int index;

	private SQL2SQLFactory factory;

	private NamedParameterMap namedParameterMap;

	public SQL2SQLRunnable(SQL2SQLFactory sql2sqlFactory, int index2, NamedParameterMap namedParameterMap) {
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
			// log.info("Entrada [" + index + "] params[" + namedParameterMap + "]");
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
					namedParameterMap = new NamedParameterMap(rs);
					if (namedParameterMap.isEmpty())
						return;
				} else {
					countUpdate += stmt.executeUpdate();
				}
			}

			if (countUpdate > 0) {
				targetConnection.commit();
				log.info("Atualizado [" + index + "] params[" + namedParameterMap + "]" + " quantidade [" + countUpdate
						+ "]");
			} else {
				log.info("Não Atualizado[" + index + "] params[" + namedParameterMap + "]" + " quantidade ["
						+ countUpdate + "]");
			}

		} catch (Exception e) {
			log.error("Falha com params[ " + namedParameterMap + "]", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("Falha no ResultSet com params[ " + namedParameterMap + "]", e);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error("Falha no Statement com params[ " + namedParameterMap + "]", e);
				}
			}
			if (sourceConnection != null)
				try {
					sourceConnection.close();
				} catch (SQLException e) {
					log.error("Falha na conexão source com params[ " + namedParameterMap + "]", e);
				}
			if (targetConnection != null)
				try {
					targetConnection.close();
				} catch (SQLException e) {
					log.error("Falha na conexão target com params[ " + namedParameterMap + "]", e);
				}
		}
	}
}