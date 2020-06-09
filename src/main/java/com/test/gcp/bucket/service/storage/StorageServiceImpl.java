package com.test.gcp.bucket.service.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

    private final ResourceLoader resourceLoader;
    private final Storage gcpStorage;
    private final String bucketName;
    private final String env;

    public StorageServiceImpl(
        ResourceLoader resourceLoader,
        Storage gcpStorage,
        @Value("${gcp.storage.bucket-name}") String bucketName,
        @Value("${gcp.storage.env}") String env
    ) {
        this.resourceLoader = resourceLoader;
        this.gcpStorage = gcpStorage;
        this.bucketName = bucketName;
        this.env = env;
    }

    public void downloadObjects() throws IOException {
        String destRootDirPath = getStorageRootDirectory();
        File destRootDir = new File(destRootDirPath);
        if (destRootDir.exists() && destRootDir.isDirectory()) {
            FileUtils.cleanDirectory(destRootDir);
        }
        Bucket bucket = gcpStorage.get(bucketName);
        Page<Blob> blobs = bucket.list();
        for (Blob blob : blobs.iterateAll()) {
            String blobName = blob.getName();
            if (!blobName.endsWith("/") && blobName.startsWith(env)) {
                downloadFileToDir(blob, destRootDirPath);
            }
        }
    }

    @Override
    public String getStorageRootDirectory() throws IOException {
        return resourceLoader.getResource("/").getFile().getAbsolutePath();
    }

    private void downloadFileToDir(Blob blob, String destRootDirPath) throws IOException {
        Path pathToFileInBucket = Paths.get(blob.getName());
        Path pathToFileInFileSystem =
            Paths.get(destRootDirPath, pathToFileInBucket.getParent().toString());
        Path fileName = pathToFileInBucket.getFileName();
        createDirsIfNotExists(pathToFileInFileSystem);
        Path destFilePath = pathToFileInFileSystem.resolve(fileName);
        blob.downloadTo(destFilePath);
    }

    private void createDirsIfNotExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
