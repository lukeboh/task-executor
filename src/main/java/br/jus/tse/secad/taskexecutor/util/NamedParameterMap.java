package br.jus.tse.secad.taskexecutor.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NamedParameterMap {
	private List<String> columnNamesList;
	private List<List<Object>> columnValuesLists;

	public NamedParameterMap(int bulkSize, ResultSet rs) throws SQLException {
		if (!rs.next()) {
			return;
		}
		int columnCount = rs.getMetaData().getColumnCount();
		columnNamesList = new ArrayList<>(columnCount);
		columnValuesLists = new ArrayList<>(columnCount);
		
		for (int i = 1; i <= columnCount; i++) {
			addColumn(rs.getMetaData().getColumnName(i));
		}
		for (int iBulk = 0; iBulk < bulkSize; iBulk++) {
			//Na primeira iteração já andou no começo do construtor.
			if (iBulk != 0 && !rs.next()) {
				return;
			}
			
			for (int i = 1; i <= columnCount; i++) {
				String columnName = rs.getMetaData().getColumnName(i);
				Object columnValue = rs.getString(i);
				addValue(columnName, columnValue);
			}
		}
	}

	public NamedParameterMap(ResultSet rs) throws SQLException {
		this(Integer.MAX_VALUE, rs);
	}

	public void addColumn(String columnName) {
		if (!columnNamesList.contains(columnName)) {
			columnNamesList.add(columnName);
			columnValuesLists.add(new ArrayList<>());
		} else {
			throw new RuntimeException("Coluna já existente [" + columnName + "]");
		}
	}

	public List<String> getColumnNames() {
		return columnNamesList;
	}

	public void addValue(String columnName, Object columnValue) {
		int index = columnNamesList.indexOf(columnName);
		List<Object> valuesList = columnValuesLists.get(index);
		valuesList.add(columnValue);
	}

	public List<Object> getValues(String columnName) {
		return columnValuesLists.get(columnNamesList.indexOf(columnName));
	}

	public Object getValue(String columnName, int index) {
		return getValues(columnName).get(index);
	}

	public int getSize() {
		return columnValuesLists.get(0).size();
	}
	
	public boolean isEmpty() {
		return columnValuesLists.get(0).size() == 0;
	}

	@Override
	public String toString() {
		return "NamedParameterMap [columnNamesList=" + columnNamesList + ", getSize()=" + getSize() + "]";
	}
}
