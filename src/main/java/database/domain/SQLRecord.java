package database.domain;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SQLRecord<T> {

    private SQLTable<T> table;
    private Map<SQLColumn<?>, Object> data = new HashMap<>();
    private T instance;

    public SQLRecord(SQLTable<T> table, T instance) {
        this.table = table;
        this.instance = instance;
        load();
    }

    public SQLRecord(SQLTable<T> table, ResultSet resultSet) {
        setTable(table);
        loadFromResultSet(resultSet);
        save();
    }

    public void load() {
        data.clear();
        for (SQLColumn<?> column: table.getColumns()) {
            data.put(column, column.getColumnInstance(instance));
        }
    }

    public void loadFromResultSet(ResultSet resultSet) {
        try {
            instance = table.getClazz().newInstance();
            for (SQLColumn column: table.getColumns()) {
                Class<?> clazz = column.getFieldType();
                Object res = resultSet.getObject(column.getName());
                data.put(column, convertToJava(res, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getPrimaryKeyValue() {
        return data.get(table.getPrimaryKey());
    }

    public void save() {
        for (SQLColumn column: table.getColumns()) {
            if (column.isPrimary() && column.isAutoIncrement()) continue;
            try {
                column.getField().set(instance, data.get(column));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object convertToJava(Object value, Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return value.toString();
        } else if (clazz.equals(Long.TYPE)) {
            return Long.parseLong(value.toString());
        } else if (clazz.equals(Date.class)) {
            if (value instanceof Date) {
                Date date = (Date) value;
                return new Date(date.getTime());
            }
        }
        return value;
    }

    public T getInstance() {
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public Map<SQLColumn<?>, Object> getData() {
        return data;
    }

    public void setData(Map<SQLColumn<?>, Object> data) {
        this.data = data;
    }

    public SQLTable<T> getTable() {
        return table;
    }

    public void setTable(SQLTable<T> table) {
        this.table = table;
    }
}
