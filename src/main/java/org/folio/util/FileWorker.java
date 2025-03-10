package org.folio.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.folio.model.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.folio.FolioUpdateBibMappingRulesApplication.exitWithError;

@Slf4j
public class FileWorker {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static File configurationFile;

    public static Configuration getConfiguration() {
        return getMappedFile(configurationFile, Configuration.class);
    }

    public static InputStream getResourceFile(String name) {
        try {
            return ResourceUtils.getURL("classpath:" + name).openStream();
        } catch (IOException e) {
            exitWithError("Failed to read file: " + name);
            return null;
        }
    }

    public static <T> T getMappedFile(File file, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            exitWithError("Failed to map file value: " + file.getName());
            return null;
        }
    }

    public static JsonNode getJsonObject(String name) {
        try {
            var file = getResourceFile(name);
            return OBJECT_MAPPER.readTree(file);
        } catch (IOException e) {
            exitWithError("Failed to map json file value: " + name);
            return null;
        }
    }
}