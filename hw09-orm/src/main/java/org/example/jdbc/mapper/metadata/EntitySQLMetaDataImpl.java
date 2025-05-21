package org.example.jdbc.mapper.metadata;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import org.example.jdbc.mapper.EntityClassMetaData;
import org.example.jdbc.mapper.EntitySQLMetaData;

@SuppressWarnings({"java:S3011", "java:S6201"})
public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {

        String tableName = entityClassMetaData.getName();
        String idFieldName = entityClassMetaData.getIdField().getName();

        String allFieldsCommaSeparated =
                entityClassMetaData.getAllFields().stream().map(Field::getName).collect(Collectors.joining(", "));

        String fieldsWithoutIdCommaSeparated = entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));

        String placeholdersForInsert =
                entityClassMetaData.getFieldsWithoutId().stream().map(f -> "?").collect(Collectors.joining(", "));

        String updateSetClauses = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> field.getName() + " = ?")
                .collect(Collectors.joining(", "));

        this.selectAllSql = String.format("SELECT %s FROM %s", allFieldsCommaSeparated, tableName);
        this.selectByIdSql =
                String.format("SELECT %s FROM %s WHERE %s = ?", allFieldsCommaSeparated, tableName, idFieldName);
        this.insertSql = String.format(
                "INSERT INTO %s (%s) VALUES (%s)", tableName, fieldsWithoutIdCommaSeparated, placeholdersForInsert);
        this.updateSql = String.format("UPDATE %s SET %s WHERE %s = ?", tableName, updateSetClauses, idFieldName);
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }
}
