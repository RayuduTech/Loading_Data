

import java.io.IOException;
import java.nio.file.Path;



public class MainETL {

	public static void main(String[] args) {

	
		try {
			
			FileHandle.filecheck();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		finally {
            try {
                String logDir = "D:\\logs";
                Path latestLogFile = FindFile.findLatestLogFile(logDir);
                if (latestLogFile != null) {
                	//Calling Function to read logs line by line and process
                    LogToDB.logtodb(latestLogFile.toString());
                } else {
                    System.err.println("No log file found to process.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

	}

}
