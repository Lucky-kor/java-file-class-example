package ioerrors;

import collectiontext.ScoreTextStore;
import support.ExamplePaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/** 예상한 실패만 잡아 설명하고, 예기치 못한 I/O 실패는 호출자에게 전달합니다. */
public class ExpectedFailuresExample {
    public static void main(String[] args) throws IOException {
        Path directory = ExamplePaths.directory("errors");
        // 매번 새 빈 디렉터리로 존재하지 않는 경로를 보장합니다.
        Path emptyDirectory = Files.createTempDirectory(directory, "missing-");
        try {
            Files.readString(emptyDirectory.resolve("missing.txt"));
        } catch (NoSuchFileException e) {
            System.out.println("예상한 실패: 파일이 없습니다.");
        } finally {
            Files.delete(emptyDirectory);
        }
        Path invalid = directory.resolve("invalid-scores.txt");
        Files.writeString(invalid, "러키:90\n라떼:아흔\n");
        try {
            ScoreTextStore.read(invalid);
        } catch (IOException e) {
            System.out.println("읽기 실패: " + e.getMessage());
        }
        // exists() 확인 직후에도 파일이 사라질 수 있습니다. 실제 I/O의 예외 처리가 필요합니다.
    }
}
