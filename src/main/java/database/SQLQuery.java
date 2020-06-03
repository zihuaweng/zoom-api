package database;

import database.domain.SQLColumn;
import database.domain.SQLRecord;
import database.domain.SQLTable;

import java.util.*;

public class SQLQuery {

    public static String createTable(SQLTable table) {
        SQLQueryBuilder builder = new SQLQueryBuilder();
        StringJoiner values = new StringJoiner(",");
        List<SQLColumn> columns = table.getColumns();
        for (SQLColumn column : columns) {
            StringJoiner columnString = new StringJoiner(" ");
            columnString.add(column.getName())
                    .add(sqlType(column.getFieldType()));
            if (column.isPrimary()) {
                columnString.add(primaryKey());
                if (column.isAutoIncrement()) {
                    columnString.add(autoIncrement());
                }
            }
            values.add(columnString.toString());
        }
        builder.createTable(table.getTableName(), values.toString());
        return builder.getQuery();
    }

    public static String deleteTable() {
        return null;
    }

    public static String insertRecord(SQLRecord record) {
        return insertRecords(Collections.singletonList(record));
    }

    public static String insertRecords(List<SQLRecord> records) {
        SQLTable table = records.get(0).getTable();
        SQLQueryBuilder builder = new SQLQueryBuilder();
        // Get all columns
        List<SQLColumn> columns = table.getColumns();
        StringJoiner columnString = new StringJoiner(",");
        for (SQLColumn column : columns) {
            if (column.isPrimary() && column.isAutoIncrement()) continue;
            columnString.add(column.getName());
        }
        // Get all field values
        StringJoiner valueString = new StringJoiner(",");
        for (SQLRecord record : records) {
            StringJoiner recordString = new StringJoiner(",");
            for (SQLColumn column : columns) {
                if (column.isPrimary() && column.isAutoIncrement()) continue;
                recordString.add(convertToSQL(record.getData().get(column), column.getFieldType()));
            }
            valueString.add(recordString.toString());
        }
        builder.insertInto(table.getTableName(), columnString.toString(), valueString.toString());
        return builder.getQuery();
    }

    public static String selectAll(SQLTable table) {
        SQLQueryBuilder builder = new SQLQueryBuilder();
        builder.select(table.getTableName());
        return builder.getQuery();
    }

    public static <E, T> String select(SQLTable<E> table, List<String> fieldName, List<T> data) {
        SQLQueryBuilder builder = new SQLQueryBuilder();
        SQLQueryBuilder conditionBuilder = new SQLQueryBuilder();
        for (int i = 1; i < fieldName.size(); i++) {
            conditionBuilder.and()
                    .value(fieldName.get(i))
                    .equalsTo()
                    .value(data.get(i), data.get(i).getClass());
        }
        builder.select(table.getTableName())
                .where()
                .value(fieldName.get(0))
                .equalsTo()
                .value(data.get(0), data.get(0).getClass());
        if (conditionBuilder.length() != 0) {
            builder.value(conditionBuilder.getQuery());
        }
        return builder.getQuery();
    }

    public static <E, T> String select(SQLTable<E> table, T data) {
        SQLQueryBuilder builder = new SQLQueryBuilder();
        builder.select(table.getTableName())
                .where()
                .value(table.getPrimaryKey().getName())
                .equalsTo()
                .value(data, data.getClass());
        return builder.getQuery();
    }

    public static String update(SQLRecord record) {
        SQLTable table = record.getTable();

        StringJoiner valueString = new StringJoiner(",");
        List<SQLColumn> columns = table.getColumns();
        for (SQLColumn column : columns) {
            if (column.isPrimary()) continue;
            valueString.add(column.getName() + "=" + convertToSQL(record.getData().get(column), column.getFieldType()));
        }

        SQLQueryBuilder builder = new SQLQueryBuilder();
        builder.update(table.getTableName())
                .value(valueString.toString())
                .where()
                .value(table.getPrimaryKey().getName())
                .equalsTo()
                .value(record.getPrimaryKeyValue(), table.getPrimaryKey().getFieldType());
        return builder.getQuery();
    }

    public static String delete(SQLRecord record) {
        SQLTable table = record.getTable();

        SQLQueryBuilder builder = new SQLQueryBuilder();
        builder.deleteFrom(table.getTableName())
                .where()
                .value(table.getPrimaryKey().getName())
                .equalsTo()
                .value(record.getPrimaryKeyValue(), table.getPrimaryKey().getFieldType());
        return builder.getQuery();
    }

    public static String convertToSQL(Object data, Class<?> clazz) {
        String value = "'" + data + "'";
        if (clazz.equals(Integer.class) || clazz.equals(Long.TYPE)) {
            value = data.toString();
        } else if (clazz.equals(Date.class)) {
            value = new Date(((Date) data).getTime()).toString();
        }
        return value;
    }

    public static String sqlType(Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return "TEXT";
        } else if (clazz.equals(Integer.TYPE) || clazz.equals(Integer.class)) {
            return "INTEGER";
        } else if (clazz.equals(Long.TYPE)) {
            return "BIGINT";
        } else if (clazz.equals(Double.class)) {
            return "DOUBLE";
        } else if (clazz.equals(Boolean.class)) {
            return "TINYINT(1)";
        } else if (clazz.equals(Float.class)) {
            return "FLOAT";
        } else {
            return "TEXT";
        }
    }

    public static String autoIncrement() {
        return "AUTOINCREMENT";
    }

    public static String primaryKey() {
        return "PRIMARY KEY";
    }
}
