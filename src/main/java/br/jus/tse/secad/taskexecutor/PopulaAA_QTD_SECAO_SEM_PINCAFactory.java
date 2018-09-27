package br.jus.tse.secad.taskexecutor;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PopulaAA_QTD_SECAO_SEM_PINCAFactory implements RunnableFactory {
	private static Logger log = Logger.getLogger(PopulaAA_QTD_SECAO_SEM_PINCAFactory.class);

	int size = 0;

	int index = 0;

	private Connection connection;

	private ResultSet resultSet;

	public PopulaAA_QTD_SECAO_SEM_PINCAFactory() throws Exception {
		
		Properties prop = new Properties();
		prop.load(new FileInputStream("config.properties"));
		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = prop.getProperty("producao.URL");
		String user = prop.getProperty("producao.user");
		String password = prop.getProperty("producao.password");
		String sqlSize = prop.getProperty("sqlSize");
		
		connection = null;
		connection = DriverManager.getConnection(url, user, password);
		
		PreparedStatement statement = connection.prepareStatement(sqlSize);
		resultSet = statement.executeQuery();
		resultSet.next();
		size = resultSet.getInt(1);
		log.info("Size [" + size + "]");
		
		resultSet.close();
		statement.close();
		
		String sql = prop.getProperty("sql");
		statement = connection.prepareStatement(sql);
		resultSet = statement.executeQuery();
	}

	public Runnable next() {
		try {
			if (!resultSet.next())
				return null;
			else {
				index++;
				String codObjetoSecao = resultSet.getString(1);
				return new PopulaAA_QTD_SECAO_SEM_PINCARunnable(index, codObjetoSecao);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public int size() {
		return size;
	}
	
	public static void main(String[] args) throws Exception {
		DefaultTaskExecutor dte = new DefaultTaskExecutor(new PopulaAA_QTD_SECAO_SEM_PINCAFactory());
		dte.showUI();
		dte.start();
	}

}
