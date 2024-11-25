

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomFormatter extends Formatter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(dateFormat.format(new Date(record.getMillis())))
          .append(" ")
          .append(record.getLevel().getLocalizedName())
          .append(": ")
          .append(formatMessage(record))
          .append(System.lineSeparator());
        return sb.toString();
    }
}
