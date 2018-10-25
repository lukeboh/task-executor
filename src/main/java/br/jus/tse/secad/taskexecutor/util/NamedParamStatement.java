package br.jus.tse.secad.taskexecutor.util;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NamedParamStatement {

	private Connection conn;
	private PreparedStatement prepStmt;
	private List<String> fields = new ArrayList<String>();
	private String sql;
	private boolean isUpdate;

	public NamedParamStatement(Connection conn, String sql, NamedParameterMap namedParamMap) throws SQLException {
		this.conn = conn;
		this.sql = sql;
		String sqlLower = this.sql.toLowerCase();
		isUpdate = sqlLower.contains("update") || sqlLower.contains("insert") || sqlLower.contains("delete");

		int pos;
		while ((pos = sql.indexOf(":")) != -1) {
			int end = sql.substring(pos).indexOf(" ");
			if (end == -1)
				end = sql.length();
			else
				end += pos;
			if (isUpdate) {
				fields.add(sql.substring(pos + 1, end));
				sql = sql.substring(0, pos) + "?" + sql.substring(end);
			} else {
				for (int i = 0; i < namedParamMap.getSize(); i++) {
					fields.add(sql.substring(pos + 1, end));
				}
				sql = sql.substring(0, pos) + getQuestionMarks(namedParamMap.getSize()) + sql.substring(end);
			}
		}
		prepStmt = conn.prepareStatement(sql);
		setMap(namedParamMap);
	}

	public String getQuestionMarks(int size) {
		StringBuffer questions = new StringBuffer("?");
		for (int i = 1; i < size; i++) {
			questions.append(",?");
		}
		return questions.toString();
	}

	public Connection getConnection() {
		return conn;
	}
	
	public PreparedStatement getPreparedStatement() {
		return prepStmt;
	}

	public ResultSet executeQuery() throws SQLException {
		return prepStmt.executeQuery();
	}

	public void close() throws SQLException {
		prepStmt.close();
	}

	public void setInt(String name, int value) throws SQLException {
		prepStmt.setInt(getIndex(name), value);
	}

	public void setString(String name, String value) throws SQLException {
		prepStmt.setString(getIndex(name), value);
	}

	public void setArray(String name, Array value) throws SQLException {
		prepStmt.setArray(getIndex(name), value);
	}

	public void setMap(NamedParameterMap npm) throws SQLException {
		if (npm.getSize() == 1) {
			for (String columnName : npm.getColumnNames()) {
				setString(columnName, (String) npm.getValue(columnName, 0));
				if (isUpdate)
					prepStmt.addBatch();
			}
		} else {
			if (!isUpdate) {// SELECT
				for (String columnName : npm.getColumnNames()) {
					// Array array = conn.createArrayOf(/* npm.getType(columnName) */ "VARCHAR",
					// npm.getValues(columnName).toArray());
					// setArray(columnName, array);
					List<Object> values = npm.getValues(columnName);
					for (int i = 0; i < npm.getSize(); i++) {
						prepStmt.setString(i + 1, (String) values.get(i));
					}
				}
			} else {// UPDATE
				for (int i = 0; i < npm.getSize(); i++) {
					for (String columnName : npm.getColumnNames()) {
						setString(columnName, (String) npm.getValue(columnName, i));
					}
					prepStmt.addBatch();
				}
			}
		}
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	private int getIndex(String name) {
		return fields.indexOf(name) + 1;
	}
}