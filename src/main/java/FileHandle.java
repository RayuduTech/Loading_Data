
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Stream;


public class FileHandle {
    private static final Logger logger = LoggerFactory.getLogger(FileHandle.class);
   


    public static void filecheck() throws IOException {
        final String bkpFiles = "D:\\Backup";
        final String ErrFiles = "D:\\Error_Files";
        FileReader reader = null;
        Properties p = null;
        try {
            reader = new FileReader("src/main/resources/mail.properties");
            p = new Properties();
            p.load(reader);
        } catch (FileNotFoundException e) {
            logger.error("File not found: src/PropertiesFiles/mail.properties", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        Path logDir = Paths.get("D:\\logs");
        if (!Files.exists(logDir)) {
            Files.createDirectories(logDir);
            System.out.println("Path D:\\logs created");
        }

        // Specify the path to the directory
        Path directoryPath = Paths.get("D:\\Client");

        // Check if the path exists and is a directory
        if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
            try (Stream<Path> files = Files.list(directoryPath)) {
                long fileCount = files.count();

                if (fileCount == 0) {
                    logger.info("Can't process, the directory is empty.");
                    // Sending mail
                   // EmailSender.sentmail(fileCount);
                } else {
                    // Re-open the stream as count() closes it
                    try (Stream<Path> filesToProcess = Files.list(directoryPath)) {
                        filesToProcess.forEach(file -> {
                            if (Files.isRegularFile(file)) {
                                try {
                                    if (Files.size(file) == 0) {
                                        logger.info("Empty file found: " + file);
                                        // Move the empty file to the backup directory
                                        boolean moved = filemove(file.toString(), ErrFiles);
                                        if (moved) {
                                            logger.info("Empty file moved to Error Files Dir: " + file);
                                        } else {
                                            logger.warn("Failed to move empty file: " + file);
                                        }
                                    } else {
                                        String clientfile = directoryPath + "\\" + file.getFileName();
                                        logger.info("Processing file: " + clientfile);
                                        
                                        // Sending file to load data
                                        DataLoadTSV.dataload(new String[]{clientfile}, bkpFiles);
                                    }
                                } catch (IOException e) {
                                    logger.error("An error occurred while processing the file: " + file, e);
                                } catch (SQLException e) {
								
									e.printStackTrace();
								}
                            }
                        });
                    }
                }
            } catch (IOException e) {
                logger.error("An error occurred while listing files in the directory", e);
            }
        } else {
            logger.warn("The specified path does not exist or is not a directory.");
        }
    }

    // File move function
    public static boolean filemove(String tsvFilePath, String TrgDir) {
        boolean res = false;
        BufferedReader br = null;

        try {
            Path sourcePath = Paths.get(tsvFilePath);
            Path targetPath = Paths.get(TrgDir);

            // Check if the target directory exists, if not, create it
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            logger.info("Moving file from " + sourcePath + " to " + targetPath);
            Path destination = targetPath.resolve(sourcePath.getFileName());

            // Attempt to move the file, retrying after a delay if it's locked
            int retries = 0;
            while (retries < 3) { // Retry up to 3 times
                try {
                    Files.move(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("File moved successfully!");
                    res = true;
                    break; // Exit the loop if move is successful
                } catch (IOException e) {
                    // File is locked, wait for a short delay before retrying
                    Thread.sleep(1000); // Wait for 1 second before retrying
                    retries++;
                }
            }

            if (retries == 3) {
                logger.warn("Failed to move the file after 3 attempts.");
            }
        } catch (IOException | InterruptedException e) {
            logger.error("An error occurred while moving the file", e);
        } finally {
            // Close the file stream in the finally block to ensure it's always closed
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error("An error occurred while closing the file reader", e);
            }
        }

        return res;
    }
}
