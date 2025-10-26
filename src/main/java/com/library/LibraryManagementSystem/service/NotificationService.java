package com.library.LibraryManagementSystem.service;

import com.library.LibraryManagementSystem.model.BorrowingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    // We use a logger to print messages to the console
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private LibraryService libraryService; // We reuse the logic we already wrote!

    @Autowired
    private EmailService emailService; // <-- INJECT OUR NEW SERVICE

    /**
     * This method will run automatically at a fixed time.
     * The 'cron' expression "0 0 8 * * *" means:
     * "At 0 minutes, 0 hours, on the 8th hour of the day (8:00 AM),
     * every day (*), every month (*), every day of the week (*)."
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendOverdueNotifications() {

        log.info("--- ⏰ Running Daily Overdue Book Check ---");

        List<BorrowingRecord> overdueBooks = libraryService.findOverdueBooks();

        if (overdueBooks.isEmpty()) {
            log.info("✅ No overdue books found. All clear!");
        } else {
            log.warn("🔔 FOUND {} OVERDUE BOOKS. 'Sending' notifications...", overdueBooks.size());

            // Loop through each overdue book and print a warning
            for (BorrowingRecord record : overdueBooks) {
                log.warn("  -> NOTIFICATION: Member '{}' ({}) has book '{}' (ISBN: {}) which was due on {}.",
                        record.getMember().getName(),
                        record.getMember().getEmail(),
                        record.getBook().getTitle(),
                        record.getBook().getIsbn(),
                        record.getDueDate()
                );

                emailService.sendOverdueEmail(
                        record.getMember().getEmail(),
                        record.getMember().getName(),
                        record.getBook().getTitle(),
                        record.getDueDate().toString()
                );

                // In a "real" project, you would add an email-sending service here.
                // emailService.sendEmail(record.getMember().getEmail(), "Book Overdue!", "...");
            }
        }
        log.info("--- Daily Overdue Check Finished ---");
    }
}