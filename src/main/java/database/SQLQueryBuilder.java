package database;


import java.util.Date;
import java.util.StringJoiner;

public class SQLQueryBuilder {

    private StringJoiner query;

    public SQLQueryBuilder() {
        query = new StringJoiner(" ");
    }

    public SQLQueryBuilder notNull() {
        query.add("NOT NULL");
        return this;
    }

    public SQLQueryBuilder nullable() {
        query.add("NULL");
        return this;
    }

    public SQLQueryBuilder equalsTo() {
        query.add("=");
        return this;
    }

    public SQLQueryBuilder all() {
        query.add("*");
        return this;
    }

    public SQLQueryBuilder createTable(String name, String columns) {
        query.add("CREATE TABLE IF NOT EXISTS").add(name).add("(" + columns + ")");
        return this;
    }

    public SQLQueryBuilder deleteTable(String name) {
        query.add("DROP TABLE").add(name);
        return this;
    }

    public SQLQueryBuilder clearTable(String name) {
        query.add("TRUNCATE TABLE").add(name);
        return this;
    }

    public SQLQueryBuilder insertInto(String name, String columns, String values) {
        query.add("INSERT INTO").add(name).add("(" + columns + ")").add("VALUES (" + values + ")");
        return this;
    }

    public SQLQueryBuilder deleteFrom(String name) {
        query.add("DELETE FROM").add(name);
        return this;
    }

    public SQLQueryBuilder update(String name) {
        query.add("UPDATE").add(name).add("SET");
        return this;
    }

    public SQLQueryBuilder set() {
        query.add("SET");
        return this;
    }

    public SQLQueryBuilder select(String name) {
        query.add("SELECT * FROM").add(name);
        return this;
    }

    public SQLQueryBuilder select(String column, String tableName) {
        query.add("SELECT").add(column).add("FROM");
        return this;
    }

    public SQLQueryBuilder where() {
        query.add("WHERE");
        return this;
    }

    public SQLQueryBuilder unique() {
        query.add("UNIQUE");
        return this;
    }

    public SQLQueryBuilder values() {
        query.add("VALUES");
        return this;
    }

    public SQLQueryBuilder and() {
        query.add("AND");
        return this;
    }

    public SQLQueryBuilder value(String data) {
        query.add(data);
        return this;
    }

    public SQLQueryBuilder value(Object data, Class<?> clazz) {
        String value = "'" + data + "'";
        if (clazz.equals(Integer.class)) {
            value = data.toString();
        } else if (clazz.equals(Date.class)) {
            value = new Date(((java.util.Date) data).getTime()).toString();
        }
        query.add(value);
        return this;
    }

    public String getQuery() {
        return query.toString();
    }

    public int length() {
        return query.length();
    }
}
