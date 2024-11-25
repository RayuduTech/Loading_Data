import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogToDB {


    public static void logtodb(String f) {
    	System.out.println("logtoDB file : "+f);
        String logFile = f;
        int i=0;
        String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\s+(\\w+)\\s+(\\w+)\\s+-\\s+(.*)";
        Pattern pattern = Pattern.compile(regex);
        BufferedReader br = null;
        
        try {
        	br = new BufferedReader(new FileReader(logFile));
            String line;
            
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // Extract log details
                    Timestamp timestamp = Timestamp.valueOf(matcher.group(1));
                    String logLevel = matcher.group(2);
                    String source = matcher.group(3);
                    String message = matcher.group(4);

                    // Create a log entry object
                    LogEntry logEntry = new LogEntry(timestamp, logLevel, source, message);

                    // Store log entry in the database
                   i= storeLogEntry(logEntry,f);
                }
            }
            if(i >0)
          	   System.out.println("record(s) inserted into ERROR_TABLE  ");
     		else
     			System.out.println("record(S) NOT inserted into ERROR_TABLE  ");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
			try {
				br.close();
			} catch (IOException e) {
			
				e.printStackTrace();
			}
		}
    }
    
  //Function to store ERROR Details
    private static int storeLogEntry(LogEntry logEntry,String logf) {
    	
        String insertSQL = "INSERT INTO ERROR_Table (FileName,E_TimeStamp,E_Log_Level,E_Src_Class,E_Message,Record_inserted) VALUES (?,?,?,?,?,?)";
        Connection con = null;
        PreparedStatement ps = null;
        LocalDateTime myObj = LocalDateTime.now();
        String curdatetime =myObj.toString().substring(0,19).replace('T',' ');
        int i=0;
        try  {
        	con = DBConnection.getConnection();
        	ps= con.prepareStatement(insertSQL);
        	ps.setString(1,  logf.substring(8));
            ps.setTimestamp(2, logEntry.getTimestamp());
            ps.setString(3, logEntry.getLogLevel());
            ps.setString(4, logEntry.getSource_class());
            ps.setString(5, logEntry.getMessage());
            ps.setString(6, curdatetime);
           i = ps.executeUpdate();
           
           
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
        
		return i;
    }
   
    
  //Function to store SUMMARY Details
   public static void storeSummaryEntry(String Fname,int Tr,int Vr,int Ir,String filetimestamp) {
    	
        String insertSQL = "INSERT INTO summary_table (FILE_NAME,TOTAL_ROWS,VALID_ROWS,INVALID_ROWS,Record_Insert_TM,File_REC_TM) VALUES (?,?,?,?,?,?)";
        Connection con = null;
        PreparedStatement ps = null;
        LocalDateTime myObj = LocalDateTime.now();
        String curdatetime =myObj.toString().substring(0,19).replace('T',' ');
        int i=0;
        try  {
        	con = DBConnection.getConnection();
        	ps= con.prepareStatement(insertSQL);
        	ps.setString(1,  Fname.substring(10));
            ps.setInt(2, Tr);
            ps.setInt(3, Vr);
            ps.setInt(4, Ir);
            ps.setString(5, curdatetime);
            ps.setString(6, filetimestamp);
           i = ps.executeUpdate();
           
           
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
			try {
				ps.close();
				con.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
        if(i >0)
     	   System.out.println("record inserted into SUMMARY_TABLE  ");
		else
			System.out.println("record NOT inserted into SUMMARY_TABLE  ");
    }
}