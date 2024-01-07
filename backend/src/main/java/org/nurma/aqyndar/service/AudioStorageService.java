package org.nurma.aqyndar.service;

public interface AudioStorageService {
    /*
     * Stores the audio data and returns the URL where it can be accessed.
     */
    String store(byte[] audioData);
}

