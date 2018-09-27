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
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			log.info("Entrada [" + index + "] [" + pmt1 + "] [" + pmt2 + "] [" + pmt3 + "]");
			connection = factory.createConnection();

			String sql2 = factory.getProperties().getProperty("sql2");
			statement = connection.prepareStatement(sql2);
			statement.setString(1, pmt1);
			rs = statement.executeQuery();
			rs.next();
			int pmt4 = rs.getInt(1);
			rs.close();
			statement.close();

			String sqlUpdate = factory.getProperties().getProperty("sqlUpdate");
			statement = connection.prepareStatement(sqlUpdate);
			statement.setString(1, pmt1);
			statement.setString(2, pmt2);
			statement.setString(3, pmt3);
			statement.setInt(4, pmt4);
			statement.executeUpdate();
			connection.commit();
			log.info("Saída [" + index + "] [" + pmt1 + "] [" + pmt2 + "] [" + pmt3 + "] [" + pmt4 + "]");

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
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}