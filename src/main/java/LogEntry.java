import java.sql.Timestamp;

public class LogEntry {
		private String FileName;
	 	private Timestamp timestamp;
	    private String logLevel;
	    private String source_class;
	    private String message;
	    
	    
		public String getFileName() {
			return FileName;
		}
		public void setFileName(String fileName) {
			FileName = fileName;
		}
		public Timestamp getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		public String getLogLevel() {
			return logLevel;
		}
		public void setLogLevel(String logLevel) {
			this.logLevel = logLevel;
		}
		public String getSource_class() {
			return source_class;
		}
		public void setSource_class(String source_class) {
			this.source_class = source_class;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public LogEntry(Timestamp timestamp, String logLevel, String source_class, String message) {
			super();
			this.timestamp = timestamp;
			this.logLevel = logLevel;
			this.source_class = source_class;
			this.message = message;
		}

	    
}
