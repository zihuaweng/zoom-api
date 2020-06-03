package database.domain;

import database.annotation.TableName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class SQLTable<T> {

    private String CHARSET = "UTF8";

    private String tableName;
    private Class<T> clazz;
    private List<SQLColumn> columns = new ArrayList<>();
    private List<SQLRecord<T>> records = new ArrayList<>();

    public SQLTable(Class<T> clazz) {
        this.clazz = clazz;
        load(clazz);
    }

    private void load(Class<T> clazz) {
        if (clazz.isAnnotationPresent(TableName.class)) {
            this.tableName = clazz.getAnnotation(TableName.class).value();
        }
        columns.clear();
        for (Field field: clazz.getDeclaredFields()) {
            int modifier = field.getModifiers();
            if (!Modifier.isStatic(modifier) && !Modifier.isFinal(modifier) && !Modifier.isTransient(modifier)) {
                columns.add(new SQLColumn(field, field.getType()));
            }
        }
    }

    public SQLRecord<T> getRecord(T data) {
        for (SQLRecord record: records) {
            if (record.getInstance() == data) {
                return record;
            }
        }
        SQLRecord<T> record = new SQLRecord<T>(this, data);
        records.add(record);
        return record;
    }

    public SQLColumn getPrimaryKey() {
        for (SQLColumn column: columns) {
            if (column.isPrimary()) {
                return column;
            }
        }
        return null;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<SQLColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLColumn> columns) {
        this.columns = columns;
    }

    public List<SQLRecord<T>> getRecords() {
        return records;
    }

    public void setRecords(List<SQLRecord<T>> records) {
        this.records = records;
    }

    public String getCHARSET() {
        return CHARSET;
    }

    public void setCHARSET(String CHARSET) {
        this.CHARSET = CHARSET;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
