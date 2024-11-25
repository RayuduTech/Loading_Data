import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataLoadTSV {
    private static final Logger logger = LoggerFactory.getLogger(DataLoadTSV.class);

    public static void dataload(String[] strings, String trgdir) throws SQLException {
        Connection con = DBConnection.getConnection();
        for (int i = 0; i < strings.length; i++) {
            String tsvFilePath = strings[i];
            String filetimestamp=DataLoadTSV.Filetimestamp(tsvFilePath);
            
            if (con != null) {
                System.out.println("Connected");
            }

            try {
                con.setAutoCommit(false);
                boolean dataInserted = false; // Flag to track if any data was inserted
                String sql = "INSERT INTO adcb_cards_and_loan_funnel (url,Website_Language,Banking_Type,Product,Product_Name,cookie,tokenized_cid) VALUES (?,?,?,?,?,?,?)";
                int insertedCount = 0;
                int Totalrows = 0, ValidRows = 0, InvalidRows = 0;
                BufferedReader reader = null;
                PreparedStatement statement = null;
                try {
                    statement = con.prepareStatement(sql);
                    reader = new BufferedReader(new FileReader(tsvFilePath));

                    String line;
                    int lineCounter = 0;
                    while ((line = reader.readLine()) != null) {
                        lineCounter++;
                        // Skip processing the first line
                        if (lineCounter == 1) {
                            continue; // Skip the header line
                        }

                        Totalrows++;

                        String[] parts = line.split("\t");

                        if (parts.length < 7) {
                            System.err.println("Skipping record " + lineCounter + ": insufficient columns (" + parts.length + ")");
                            logger.info("Skipping line " + lineCounter + ": insufficient columns (" + parts.length + ")");
                            InvalidRows++;
                            continue; // Skip lines with insufficient columns
                        }

                        String url = parts[0];
                        String Website_Language = parts[1];
                        String Banking_Type = parts[2];
                        String Product = parts[3];
                        String Product_Name = parts[4];
                        String cookie = parts[5];
                        String tokenized_cid = parts[6];

                        statement.setString(1, url);
                        statement.setString(2, Website_Language);
                        statement.setString(3, Banking_Type);
                        statement.setString(4, Product);
                        statement.setString(5, Product_Name);
                        statement.setString(6, cookie);
                        statement.setString(7, tokenized_cid);

                        statement.addBatch();
                    }

                    int[] batchResult = statement.executeBatch();
                    for (int result : batchResult) {
                        if (result > 0) {
                            insertedCount++;
                            dataInserted = true;
                        }
                    }
                    
                    //Calculating Valid & invalid rows
                    ValidRows = insertedCount;
                    InvalidRows = Totalrows - ValidRows;

                    if (dataInserted) {
                        con.commit();
                        reader.close();

                        System.out.println("Data has been inserted successfully. " + ValidRows);
                        logger.info("Data has been inserted successfully and count is : " + ValidRows);

                        // Calling File Moving function
                        FileHandle.filemove(tsvFilePath, trgdir);
                        // Calling function to store summary data
                        LogToDB.storeSummaryEntry(tsvFilePath, Totalrows, ValidRows, InvalidRows,filetimestamp);
                        // Calling function to sent mail confirmation
                        EmailSender.sentmail(lineCounter,tsvFilePath, Totalrows, ValidRows, InvalidRows,filetimestamp);
                    } else {
                        System.out.println("Data not inserted.");

                        // sending mail
                        EmailSender.sentmail(lineCounter + 1,tsvFilePath, Totalrows, ValidRows, InvalidRows,filetimestamp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                con.close();
            }
        }
    }
    
    public static String Filetimestamp(String str) {
        String filePath = str; // Specify the path to your file
        File file = new File(filePath);
        String formattedDateTime="";
        // Check if the file exists
        if (file.exists()) {
            long timestamp = file.lastModified();
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
             formattedDateTime = dateTime.format(formatter);
            //System.out.println("File creation timestamp: " + formattedDateTime);
        } else {
            System.out.println("File does not exist.");
        }
		return formattedDateTime;
    }
}
