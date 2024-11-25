import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FindFile {
    public static Path findLatestLogFile(String logDir) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(logDir), "*.log")) {
            List<Path> logFiles = toList(directoryStream);
            Optional<Path> latestLogFile = logFiles.stream()
                    .filter(Files::isRegularFile)
                    .max(Comparator.comparingLong(path -> {
                        try {
                            return Files.readAttributes(path, BasicFileAttributes.class).creationTime().toMillis();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }));
            return latestLogFile.orElse(null);
        }
    }

    private static List<Path> toList(DirectoryStream<Path> directoryStream) {
        return StreamSupport.stream(directoryStream.spliterator(), false).collect(Collectors.toList());
    }
}
