package br.jus.tse.secad.taskexecutor.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NamedParameterMap {
	private List<String> columnNamesList;
	private List<String> columnTypesList;
	private List<List<Object>> columnValuesLists;

	public NamedParameterMap(int bulkSize, ResultSet rs) throws SQLException {
		if (!rs.next()) {
			return;
		}
		int columnCount = rs.getMetaData().getColumnCount();
		columnNamesList = new ArrayList<>(columnCount);
		columnTypesList = new ArrayList<>(columnCount);
		columnValuesLists = new ArrayList<>(columnCount);
		
		for (int i = 1; i <= columnCount; i++) {
			addColumn(rs.getMetaData().getColumnName(i));
			addType(rs.getMetaData().getColumnTypeName(i));
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
	
	public void addType(String type) {
		columnTypesList.add(type);
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
	
	public String getType(String columnName) {
		return columnTypesList.get(columnNamesList.indexOf(columnName));
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
		StringBuilder builder = new StringBuilder();
		builder.append("NamedParameterMap [columnNamesList=");
		builder.append(columnNamesList);
		builder.append(", columnTypesList=");
		builder.append(columnTypesList);
		
		builder.append(", getSize()=");
		builder.append(getSize());

		builder.append(", columnValuesLists=");
		
		if (columnNamesList.get(0) != null) {
			Object firstObject = columnValuesLists.get(0).get(0);
			
			builder.append(limitator(firstObject));
			builder.append(" ... ");
			List<Object> lastList = columnValuesLists.get(columnValuesLists.size() - 1);
			Object lastObject = lastList.get(lastList.size() - 1);
			
			builder.append(limitator(lastObject));
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	private String limitator (Object o) {
		if (o == null) {
			return null;
		}
		String s = o.toString();
		if (s.length() > 20) {
			return s.substring(0, 20);
		}
		return s;
	}
	

}
