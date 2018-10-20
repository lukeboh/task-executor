package br.jus.tse.secad.taskexecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class SQL2SQLRunnable implements Runnable {

	private static Logger log = Logger.getLogger(SQL2SQLRunnable.class);

	public int index;
	public String pmt1, pmt2, pmt3;

	private SQL2SQLFactory factory;

	public SQL2SQLRunnable(SQL2SQLFactory sql2sqlFactory, int index, String pmt1, String pmt2, String pmt3) {
		this.factory = sql2sqlFactory;
		this.index = index;
		this.pmt1 = pmt1;
		this.pmt2 = pmt2;
		this.pmt3 = pmt3;

	}

	public void run() {
		Connection targetConnection = null;
		PreparedStatement targetStatement = null;
		ResultSet targetResultSet = null;
		try {
			log.info("Entrada [" + index + "] [" + pmt1 + "] [" + pmt2 + "] [" + pmt3 + "]");
			targetConnection = factory.getTargetConnection();

			String targetSql= factory.getProperties().getProperty("db.target.sql.1");
			targetStatement = targetConnection.prepareStatement(targetSql);
			targetStatement.setString(1, pmt1);
			targetResultSet = targetStatement.executeQuery();
			targetResultSet.next();
			int pmt4 = targetResultSet.getInt(1);
			targetResultSet.close();
			targetStatement.close();

			targetSql = factory.getProperties().getProperty("db.target.sql.2");
			targetStatement = targetConnection.prepareStatement(targetSql);
			targetStatement.setString(1, pmt1);
			targetStatement.setString(2, pmt2);
			targetStatement.setString(3, pmt3);
			targetStatement.setInt(4, pmt4);
			targetStatement.executeUpdate();
			targetConnection.commit();
			log.info("Saída [" + index + "] [" + pmt1 + "] [" + pmt2 + "] [" + pmt3 + "] [" + pmt4 + "]");

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