package org.example.dataprocessor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.example.model.Measurement;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        // читает файл, парсит и возвращает результат
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new FileProcessException("File not found: " + fileName);
            }

            return mapper.readValue(inputStream, new TypeReference<List<Measurement>>() {});
        } catch (IOException e) {
            throw new FileProcessException("Error reading or parsing file: " + fileName, e);
        } catch (Exception e) {
            throw new FileProcessException("Unexpected error reading  or parsing file: " + fileName, e);
        }
    }
}
