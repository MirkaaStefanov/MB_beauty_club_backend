package com.example.MB_beauty_club_backend.config.schedulers;

import com.example.MB_beauty_club_backend.services.impl.DatabaseBackupService;
import com.example.MB_beauty_club_backend.services.impl.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@ComponentScan
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class DailyBackupScheduler {

    private final DatabaseBackupService databaseBackupService;
    private final MailService mailService;

    @Scheduled(cron = "0 0 3 * * *")
    public void performDailyDatabaseBackup() {
        log.info("Starting daily database backup job...");
        try {
            // 1. Export the database
            File backupFile = databaseBackupService.exportDatabase();
            log.info("Database successfully exported to: {}", backupFile.getAbsolutePath());

            // 2. Email the backup file to the admin
            mailService.sendDatabaseBackup(backupFile);
            log.info("Database backup file successfully emailed to admin.");

            // 3. Optional: Delete the local file after sending
            if (backupFile.exists() && backupFile.delete()) {
                log.info("Local backup file deleted successfully.");
            }

        } catch (IOException e) {
            log.error("Failed to perform database backup due to IO error: {}", e.getMessage());
        } catch (InterruptedException e) {
            log.error("Database backup process was interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt(); // Restore the interrupted status
        } catch (MessagingException e) {
            log.error("Failed to send database backup email: {}", e.getMessage());
        } catch (RuntimeException e) {
            log.error("Database export failed: {}", e.getMessage());
        }
    }
}
