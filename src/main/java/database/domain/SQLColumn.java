package database.domain;

import database.annotation.AutoIncrement;
import database.annotation.PrimaryKey;

import java.lang.reflect.Field;

public class SQLColumn<E> {

    private String name;
    private Field field;
    private Class<E> fieldType;
    private boolean isPrimary;
    private boolean isAutoIncrement;

    public SQLColumn(Field field, Class<E> clazz) {
        this.field = field;
        this.fieldType = clazz;
        this.name = field.getName();
        getField().setAccessible(true);
        load();
    }

    public void load() {
        isPrimary = field.isAnnotationPresent(PrimaryKey.class);
        isAutoIncrement = field.isAnnotationPresent(AutoIncrement.class);
    }

    public E getColumnInstance(Object instance) {
        E returnInstance = null;
        try {
            returnInstance = (E) field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return returnInstance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public Class<E> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<E> fieldType) {
        this.fieldType = fieldType;
    }
}
