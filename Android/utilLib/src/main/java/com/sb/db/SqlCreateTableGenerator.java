package com.sb.db;

import java.util.List;

public class SqlCreateTableGenerator {
	private String tableName;
	private List<Column> columns;
	
	public SqlCreateTableGenerator(String name, List<Column> columns) {
		tableName = name;
		this.columns = columns;
	}

    public String build() {
		// We should switch this to something better suited for appending strings.
		String finalString = "";
		for (Column column: columns) {
			finalString += column.toString()
                    + (column.isNotNullable() ? " NOT NULL" : "")
                    + (column.isUnique() ? " UNIQUE" : "")
                    + ",";
		}
		
		return "CREATE TABLE " 
		+ tableName 
		+ "(" 
		+ finalString.substring(0, finalString.length()-1) 
		+ ")";
	}
}
