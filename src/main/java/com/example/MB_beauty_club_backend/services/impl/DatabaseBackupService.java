package com.example.MB_beauty_club_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DatabaseBackupService {

    @Value("${application.backup.mysqldump-path}")
    private String mysqldumpPath;

    @Value("${application.backup.output-dir}")
    private String outputDir;

    @Value("${application.backup.database-name}")
    private String databaseName;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    /**
     * Executes the mysqldump command to export the database.
     * @return The File object pointing to the generated SQL backup file.
     * @throws IOException If an IO error occurs during directory or file handling.
     * @throws InterruptedException If the mysqldump process is interrupted.
     * @throws RuntimeException If the mysqldump process fails with an error.
     */
    public File exportDatabase() throws IOException, InterruptedException {
        // 1. Create backup directory if it doesn't exist
        File backupDir = new File(outputDir);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        // 2. Define the output file name
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fileName = databaseName + "_backup_" + date + ".sql";
        File outputFile = new File(backupDir, fileName);

        // 3. Construct the mysqldump command
        // Note: The structure might slightly vary based on your OS/Shell
        String[] command = new String[]{
                mysqldumpPath,
                "-h127.0.0.1", // <-- FORCE TCP/IP CONNECTION
                "-u" + dbUser,
                // Only include password if it's not empty, as -p without a value prompts for input
                dbPassword.isEmpty() ? "" : "-p" + dbPassword,
                databaseName,
                "-r" + outputFile.getAbsolutePath()
        };

        // Filter out the empty password argument if needed
        ProcessBuilder processBuilder = new ProcessBuilder(
                java.util.Arrays.stream(command).filter(s -> !s.isEmpty()).toArray(String[]::new)
        );

        // 4. Execute the command
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            // Log the error stream from mysqldump
            try (java.util.Scanner scanner = new java.util.Scanner(process.getErrorStream()).useDelimiter("\\A")) {
                String error = scanner.hasNext() ? scanner.next() : "Unknown mysqldump error.";
                throw new RuntimeException("Database export failed. mysqldump exit code: " + exitCode + ". Error: " + error);
            }
        }

        return outputFile;
    }
}
