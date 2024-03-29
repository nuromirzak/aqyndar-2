package org.nurma.aqyndar.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "voice-service.storage.type", havingValue = "local")
@Log4j2
public class LocalStorageService implements AudioStorageService {
    private final String localPath;

    public LocalStorageService(@Value("${voice-service.storage.local-path}") final String localPath) {
        String homeDirectory = System.getProperty("user.home");
        this.localPath = Paths.get(homeDirectory, localPath).toString();

        log.info("Creating local storage service with path: {}", this.localPath);
    }

    @Override
    public String store(final byte[] audioData) {
        try {
            createDirectoryIfNotExists(localPath);
            String fileName = UUID.randomUUID() + ".wav";
            Path path = Paths.get(localPath, fileName);
            try {
                Files.write(path, audioData);
            } catch (Exception e) {
                log.error("Error while storing audio file", e);
                throw new RuntimeException(e);
            }
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDirectoryIfNotExists(final String path) throws IOException {
        Path directoryPath = Paths.get(path);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }
}
