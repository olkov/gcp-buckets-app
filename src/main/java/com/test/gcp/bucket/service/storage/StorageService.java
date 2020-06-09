package com.test.gcp.bucket.service.storage;

import java.io.IOException;

public interface StorageService {

    void downloadObjects() throws IOException;

    String getStorageRootDirectory() throws IOException;
}
