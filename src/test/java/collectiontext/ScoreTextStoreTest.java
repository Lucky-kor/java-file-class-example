package collectiontext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ScoreTextStoreTest {
    @TempDir Path directory;

    @Test
    void preservesKoreanNamesOrderAndBoundaryScores() throws IOException {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("러키", 0);
        scores.put("라떼", 100);
        Path file = directory.resolve("scores.txt");
        ScoreTextStore.write(file, scores);
        Map<String, Integer> restored = ScoreTextStore.read(file);
        assertEquals(scores, restored);
        assertEquals(List.of("러키", "라떼"), List.copyOf(restored.keySet()));
    }

    @Test
    void rejectsMalformedRowsWithLineNumber() throws IOException {
        for (String row : List.of("라떼", ":80", "라떼:", "라떼:80:90", "라떼:팔십",
                "라떼:-1", "라떼:101", "러키:80", "")) {
            Path file = directory.resolve("invalid.txt");
            Files.writeString(file, "러키:90\n" + row + "\n");
            IOException error = assertThrows(IOException.class, () -> ScoreTextStore.read(file), row);
            assertTrue(error.getMessage().startsWith("2번째 줄:"), row);
        }
    }

    @Test
    void invalidInputDoesNotTruncateExistingFile() throws IOException {
        Path file = directory.resolve("existing.txt");
        Files.writeString(file, "러키:90\n");
        for (Map<String, Integer> invalid : List.of(Map.of("라:떼", 80), Map.of("라\n떼", 80),
                Map.of(" ", 80), Map.of("라떼", 101))) {
            assertThrows(IllegalArgumentException.class, () -> ScoreTextStore.write(file, invalid));
            assertEquals("러키:90\n", Files.readString(file));
        }
    }

    @Test
    void supportsEmptyMapAndReportsMissingFile() throws IOException {
        Path file = directory.resolve("empty.txt");
        ScoreTextStore.write(file, Map.of());
        assertTrue(ScoreTextStore.read(file).isEmpty());
        assertThrows(IOException.class, () -> ScoreTextStore.read(directory.resolve("missing.txt")));
    }
}
