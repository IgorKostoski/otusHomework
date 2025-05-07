package org.example.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final String fileName;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
            mapper.writeValue(writer, data);
        } catch (IOException e) {
            throw new FileProcessException("Error writing file: " + fileName, e);
        } catch (Exception e) {
            throw new FileProcessException("Unexpected error writing file: " + fileName, e);
        }
    }
}
