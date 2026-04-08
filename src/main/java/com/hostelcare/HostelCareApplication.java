package com.hostelcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main entry point for the HostelCare – Smart Complaint Management System.
 *
 * @EnableAsync enables asynchronous email notifications via @Async in EmailService.
 */
@SpringBootApplication
@EnableAsync
public class HostelCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(HostelCareApplication.class, args);
    }
}
