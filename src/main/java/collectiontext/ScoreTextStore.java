package collectiontext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** 한 줄을 이름:점수로 저장합니다. 범용 CSV가 아닌 학습용으로 정의한 형식입니다. */
public final class ScoreTextStore {
    private ScoreTextStore() { }

    public static void write(Path path, Map<String, Integer> scores) throws IOException {
        // 파일을 비우기 전에 모든 값을 검사하여 잘못된 입력으로 인한 덮어쓰기를 막습니다.
        // 이 검사는 I/O 도중 장애까지 막지는 않습니다. 원자적 저장은 별도 설계가 필요합니다.
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String name = entry.getKey();
            Integer score = entry.getValue();
            if (name == null || name.isBlank() || name.contains(":")
                    || name.contains("\n") || name.contains("\r")
                    || score == null || score < 0 || score > 100) {
                throw new IllegalArgumentException("이름은 비어 있거나 콜론/줄바꿈을 포함할 수 없고 점수는 0~100이어야 합니다.");
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        }
    }

    public static Map<String, Integer> read(Path path) throws IOException {
        Map<String, Integer> scores = new LinkedHashMap<>(); // 파일의 줄 순서를 유지합니다.
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int number = 0;
            while ((line = reader.readLine()) != null) {
                number++;
                // -1은 마지막 빈 항목도 보존합니다. "러키:"도 잘못된 점수로 검사합니다.
                String[] parts = line.split(":", -1);
                if (parts.length != 2 || parts[0].isBlank()) {
                    throw invalid(number, "이름:점수 형식이 아닙니다.");
                }
                int score;
                try {
                    score = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    throw invalid(number, "점수는 정수여야 합니다.");
                }
                if (score < 0 || score > 100) {
                    throw invalid(number, "점수 범위는 0~100입니다.");
                }
                // Map.put은 같은 키를 덮어씁니다. 이 예제는 데이터 유실을 막기 위해 거부합니다.
                if (scores.containsKey(parts[0])) {
                    throw invalid(number, "중복 이름입니다: " + parts[0]);
                }
                scores.put(parts[0], score);
            }
        }
        return scores;
    }

    private static IOException invalid(int line, String reason) {
        return new IOException(line + "번째 줄: " + reason);
    }
}
