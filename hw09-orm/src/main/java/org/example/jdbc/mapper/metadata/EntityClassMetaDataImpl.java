package org.example.jdbc.mapper.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.example.jdbc.annotations.Id;
import org.example.jdbc.mapper.EntityClassMetaData;

@SuppressWarnings({"java:S3011", "java:S3864"})
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final String className;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {

        this.className = clazz.getSimpleName().toLowerCase();

        try {
            this.constructor = clazz.getConstructor();

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + clazz.getName() + "must have a no-args constructor", e);
        }

        this.allFields = Arrays.stream(clazz.getDeclaredFields())
                //noinspection sonar:S3011
                .map(field -> {
                    field.setAccessible(true);
                    return field;
                })
                .toList();

        this.idField = this.allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Class " + clazz.getName() + " must have a field annotated with @Id"));
        //noinspection sonar:S3011
        this.idField.setAccessible(true);

        this.fieldsWithoutId = this.allFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                //noinspection sonar:S3011
                .map(field -> {
                    field.setAccessible(true);
                    return field;
                })
                .toList();
    }

    @Override
    public String getName() {
        return this.className;
    }

    @Override
    public Constructor<T> getConstructor() {
        return this.constructor;
    }

    @Override
    public Field getIdField() {
        return this.idField;
    }

    @Override
    public List<Field> getAllFields() {
        return List.copyOf(this.allFields);
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return List.copyOf(this.fieldsWithoutId);
    }
}
