package database;

import database.domain.SQLRecord;
import database.domain.SQLTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class SQLDatabase {

    Logger logger = LoggerFactory.getLogger(SQLDatabase.class);

    private String DATABASE_PREFIX = "jdbc:sqlite:";
    private String DATABASE_SUFFIX = ".db";

    private Connection connection;
    private Map<Class<?>, SQLTable<?>> cacheTables = new HashMap<>();

    public SQLDatabase(String database) {
        connection(database);

    }

    public void connection(String database) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DATABASE_PREFIX + database + DATABASE_SUFFIX);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable(Class<?> clazz) {
        SQLTable<?> table = getTableData(clazz);
        execute(SQLQuery.createTable(table));
    }

    public void insert(Object data) {
        Class<?> dataClass = data.getClass();
        SQLTable table = getTableData(dataClass);
        SQLRecord record = table.getRecord(data);
        execute(SQLQuery.insertRecord(record));
    }

    public <E, T> E getData(Class<E> clazz, T data) {
        SQLTable<E> table = getTableData(clazz);
        E recordData = null;
        try {
            ResultSet result = executeQuery(SQLQuery.select(table, data));
            if (result.next()) {
                SQLRecord<E> record = new SQLRecord<>(table, result);
                table.getRecords().add(record);
                recordData = record.getInstance();
            }
            result.close();
            result.getStatement().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recordData;
    }

    public <E, T> E getData(Class<E> clazz, String fieldName, T data) {
        return getData(clazz, Collections.singletonList(fieldName), Collections.singletonList(data));
    }

    public <E, T> E getData(Class<E> clazz, List<String> fieldName, List<T> data) {
        SQLTable<E> table = getTableData(clazz);
        E recordData = null;
        try {
            ResultSet result = executeQuery(SQLQuery.select(table, fieldName, data));
            if (result.next()) {
                SQLRecord<E> record = new SQLRecord<>(table, result);
                table.getRecords().add(record);
                recordData = record.getInstance();
            }
            result.close();
            result.getStatement().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recordData;
    }

    public <E> List<E> getAllData(Class<E> clazz) {
        List<E> list = new ArrayList<>();
        SQLTable<E> table = getTableData(clazz);

        ResultSet result = executeQuery(SQLQuery.selectAll(table));
        try {
            while (result.next()) {
                SQLRecord record = new SQLRecord(table, result);
                list.add((E) record.getInstance());
            }
            result.close();
            result.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void update(Object data) {
        Class<?> clazz = data.getClass();
        SQLTable table = getTableData(clazz);
        SQLRecord record = table.getRecord(data);
        record.load();
        execute(SQLQuery.update(record));
    }

    public void delete(Object data) {
        Class<?> clazz = data.getClass();
        SQLTable table = getTableData(clazz);
        SQLRecord record = table.getRecord(data);
        execute(SQLQuery.delete(record));
    }

    public SQLTable getTableData(Class<?> clazz) {
        SQLTable table = cacheTables.get(clazz);
        if (table == null) {
            table = new SQLTable(clazz);
            cacheTables.put(clazz, table);
        }
        return table;
    }

    public int execute(String query) {
        logger.debug(query);
        int id = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys != null && keys.next()) {
                id = keys.getInt(1);
            }
            keys.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public ResultSet executeQuery(String query) {
        logger.debug(query);
        try {
            ResultSet resultSet = connection.prepareStatement(query).executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
