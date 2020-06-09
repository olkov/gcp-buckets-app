package com.test.gcp.bucket.controller;

import com.test.gcp.bucket.service.storage.StorageService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/")
public class MainController {

    private final StorageService storageService;

    public MainController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public HttpEntity<?> downloadObjectsFromGCS() throws IOException {
        try {
            storageService.downloadObjects();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error!", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
