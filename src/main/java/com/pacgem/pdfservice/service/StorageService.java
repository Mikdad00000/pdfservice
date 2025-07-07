package com.pacgem.pdfservice.service;

import com.pacgem.pdfservice.exception.StorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private S3Client s3Client;
    private final String bucketName = "pdf-processing-bucket";

    public String uploadFile(MultipartFile file, String jobId) {
        String key = generateFileKey(jobId, "original", "pdf");
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return key;
        } catch (IOException e) {
            throw new StorageException("Failed to upload file", e);
        }
    }

    public String uploadProcessedFile(byte[] fileData, String jobId, String format) {
        String key = generateFileKey(jobId, format, "pdf");
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType("application/pdf")
                            .build(),
                    RequestBody.fromBytes(fileData)
            );
            return key;
        } catch (Exception e) {
            throw new StorageException("Failed to upload processed file", e);
        }
    }

    public Resource downloadFile(String key) {
        try {
            var response = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return new InputStreamResource(response);
        } catch (Exception e) {
            throw new StorageException("Failed to download file", e);
        }
    }

    public boolean isHealthy() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateFileKey(String jobId, String format, String extension) {
        return "jobs/" + jobId + "/" + format + "." + extension;
    }
}

