package com.hostelcare.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * No-op email stub — emails are disabled.
 * Replace with real JavaMailSender implementation when SMTP is configured.
 */
@Slf4j
@Service
public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        log.info("[EMAIL STUB] To: {} | Subject: {}", to, subject);
    }

    public void sendStatusChangeNotification(String toEmail, String userName,
                                              Long complaintId, String newStatus) {
        log.info("[EMAIL STUB] Status change for complaint #{} -> {}", complaintId, newStatus);
    }

    public void sendAssignmentNotification(String toEmail, String staffName,
                                            Long complaintId, String complaintTitle) {
        log.info("[EMAIL STUB] Assignment notification for complaint #{}", complaintId);
    }
}
