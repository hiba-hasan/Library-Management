package com.library.LibraryManagementSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    /**
     * By adding @Async, this method will run in a separate thread.
     * The NotificationService won't have to wait for the email to be sent.
     */
    @Async
    public void sendOverdueEmail(String toEmail, String memberName, String bookTitle, String dueDate) {

        String subject = "Overdue Book Notice: " + bookTitle;
        String body = "Dear " + memberName + ",\n\n"
                + "This is a friendly reminder that your book, \"" + bookTitle + "\", "
                + "was due on " + dueDate + ".\n\n"
                + "Please return it to the library as soon as possible.\n\n"
                + "Thank you,\n"
                + "Your Library Management System";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("hibaalhasan.m@gmail.com"); // Must be the same email as in application.properties
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            emailSender.send(message);
        } catch (Exception e) {
            // In a real app, you'd log this failure more robustly
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }
}