package org.nurma.aqyndar.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "voice-service.storage.type", havingValue = "s3")
public class S3StorageService implements AudioStorageService {
    @Override
    public String store(final byte[] audioData) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
