package br.jus.tse.secad.taskexecutor.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionUtil {

	private static HikariConfig configSource = new HikariConfig();
	private static HikariDataSource dsSource;
	
	private static HikariConfig configTarget = new HikariConfig();
	private static HikariDataSource dsTarget;

	static {
		configSource = new HikariConfig("source-datasource.properties");
		dsSource = new HikariDataSource(configSource);
		
		configTarget = new HikariConfig("target-datasource.properties");
		dsTarget = new HikariDataSource(configTarget);
	}


	public static Connection getSourceConnection() throws SQLException {
		return dsSource.getConnection();
	}
	
	public static Connection getTargetConnection() throws SQLException {
		return dsTarget.getConnection();
	}

}
