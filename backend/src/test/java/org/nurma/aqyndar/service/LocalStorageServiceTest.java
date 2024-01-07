package org.nurma.aqyndar.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
class LocalStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void whenPathIsInvalid_ThenIOExceptionIsThrown() {
        String invalidPath = "/\\/\\/";
        assertThrows(IOException.class, () -> new LocalStorageService(invalidPath));
    }

    @Test
    void whenDirectoryIsReadOnly_ThenIOExceptionIsThrown() throws IOException {
        Path readOnlyDir = tempDir.resolve("readOnlyDir");
        Files.createDirectory(readOnlyDir);
        Files.setPosixFilePermissions(readOnlyDir, Set.of(PosixFilePermission.OWNER_READ));

        LocalStorageService service = new LocalStorageService(readOnlyDir.toString());
        byte[] dummyData = "test data".getBytes();

        assertThrows(RuntimeException.class, () -> service.store(dummyData));
    }

    @Test
    void successfulDirectoryCreation() throws IOException {
        Path newDir = tempDir.resolve("newDir");
        LocalStorageService service = new LocalStorageService(newDir.toString());
        assertTrue(Files.exists(newDir));
    }

    @Test
    void successfulFileStorage() throws IOException {
        LocalStorageService service = new LocalStorageService(tempDir.toString());
        byte[] dummyData = "test data".getBytes();
        String fileName = service.store(dummyData);
        assertTrue(Files.exists(tempDir.resolve(fileName)));
        assertArrayEquals(dummyData, Files.readAllBytes(tempDir.resolve(fileName)));
    }
}

