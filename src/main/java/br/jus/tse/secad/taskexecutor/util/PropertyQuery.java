package br.jus.tse.secad.taskexecutor.util;

public class PropertyQuery implements Comparable<PropertyQuery> {

	String key;

	String sql;
	
	String sqlSize;

	String dbID;

	public PropertyQuery(String key, String sql) {
		super();
		this.key = key;
		this.sql = sql;
		if (key.contains("source")) {
			dbID = "source";
		} else if (key.contains("target")) {
			dbID = "target";
		} else {
			throw new RuntimeException(
					"Propriedade sem identificação correta de banco [" + key + "]. Tem que ser source ou target");
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSql() {
		return sql;
	}
	
	public String getSqlSize() {
		return sqlSize;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public void setSqlSize(String sqlSize) {
		this.sqlSize = sqlSize;
	}

	public String getDbID() {
		return dbID;
	}

	public void setDbID(String dbID) {
		this.dbID = dbID;
	}

	@Override
	public int compareTo(PropertyQuery o) {
		return key.compareTo(o.key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyQuery other = (PropertyQuery) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropertyQuery [key=" + key + ", sql=" + sql + ", dbID=" + dbID + ", sqlSize=" + sqlSize + "]";
	}
		
}
