package com.findit.backend.controller;

import com.findit.backend.model.SystemStatus;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @Autowired
    private Firestore firestore;

    @GetMapping("/hello")
    public String sayHello() {
        return "FindIt Backend is Online!";
    }

    @GetMapping("/status")
    public SystemStatus getStatus() {
        String dbStatus = (firestore != null) ? "Connected to Firestore" : "Database Error";
        return new SystemStatus("Running", "1.0.2", dbStatus);
    }
}
