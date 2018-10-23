package br.jus.tse.secad.taskexecutor.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NamedParamStatement {
	
	private PreparedStatement prepStmt;
	private List<String> fields = new ArrayList<String>();
	
	public NamedParamStatement(Connection conn, String sql) throws SQLException {
		int pos;
		while ((pos = sql.indexOf(":")) != -1) {
			int end = sql.substring(pos).indexOf(" ");
			if (end == -1)
				end = sql.length();
			else
				end += pos;
			fields.add(sql.substring(pos + 1, end));
			sql = sql.substring(0, pos) + "?" + sql.substring(end);
		}
		prepStmt = conn.prepareStatement(sql);
	}
	public NamedParamStatement(Connection conn, String sql, HashMap<String, Object> namedParamMap) throws SQLException {
		this(conn, sql);
		setMap(namedParamMap);
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
	
	public void setMap(HashMap<String, Object> namedParamMap) throws SQLException {
		for (String columnName : namedParamMap.keySet()) {
			setString(columnName, (String) namedParamMap.get(columnName));
		}
	}

	private int getIndex(String name) {
		return fields.indexOf(name) + 1;
	}
}