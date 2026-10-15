package com.raid.lmee.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class CrestStorage {

    private final BlobContainerClient containerClient;

    public CrestStorage(BlobServiceClient client, @Value("${app.crest-container}") String containerName) {
        containerClient = client.getBlobContainerClient(containerName);
    }

    public String temporaryUpload(MultipartFile file) throws IOException {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        BlobClient blob = containerClient.getBlobClient("temp/" + UUID.randomUUID());
        blob.upload(file.getInputStream(), file.getSize(), true);
        blob.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));

        return blob.getBlobUrl();
    }

    public String resolve(String url) {
        if (url == null || url.isBlank()) {
            return null; // default crest is used (dynamic insert)
        }

        String prefix = containerClient.getBlobContainerUrl() + "/";
        if (!url.startsWith(prefix)) {
            throw new IllegalArgumentException("Incorrect crest url");
        }

        String name = url.substring(prefix.length());
        if (!name.startsWith("temp/")) {
            return url; // already final, so nothing to move
        }

        BlobClient temp = containerClient.getBlobClient(name);
        BlobClient permanent = containerClient.getBlobClient(name.substring("temp/".length()));
        permanent.beginCopy(temp.getBlobUrl(), Duration.ofSeconds(1)).waitForCompletion();
        temp.delete();

        return permanent.getBlobUrl();
    }
}
