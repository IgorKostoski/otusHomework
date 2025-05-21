package org.example.jdbc.mapper;

import org.example.core.repository.DataTemplate;
import org.example.core.repository.DataTemplateException;
import org.example.core.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
@SuppressWarnings({"java:S3011", "java:56201"})
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    
    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return createEntityFromResultSet(rs);
                }
                return null;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        Function<ResultSet, List<T>> rsHandler = rs -> {
            List<T> resultList = new ArrayList<>();
            try {
                while (rs.next()) {
                    resultList.add(createEntityFromResultSet(rs));
                }
                return resultList;
            } catch (SQLException | ReflectiveOperationException e) {
                throw new DataTemplateException(e);
            }
        };

        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rsHandler)
                .orElseThrow(() -> new DataTemplateException(
                        new RuntimeException("Unexpected error: findAll query returned no result wrapped")));
    }

    @Override
    public long insert(Connection connection, T object) {
        try {
            List<Object> params = entityClassMetaData.getFieldsWithoutId().stream()
                    .map(field -> {
                        try {
                            //noinspection sonar:S3011
                            return field.get(object);
                        } catch (IllegalAccessException e) {
                            throw new DataTemplateException(e);
                        }
                    })
                    .toList();
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        try {
            List<Object> params = new ArrayList<>();
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                params.add(field.get(object));
            }

            params.add(entityClassMetaData.getIdField().get(object));

            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private T createEntityFromResultSet(ResultSet rs) throws ReflectiveOperationException, SQLException {
        T entity = entityClassMetaData.getConstructor().newInstance();
        for (Field field : entityClassMetaData.getAllFields()) {
            Object value = rs.getObject(field.getName());

            if (field.getType() == Long.class && value instanceof Integer integerValue) {
                value = integerValue.longValue();
            }
            field.set(entity, value);
        }
        return entity;
    }
}
