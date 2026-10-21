package support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** 예제 결과를 한곳에 모읍니다. 파일 입출력 원리는 각 예제에서 직접 구현합니다. */
public final class ExamplePaths {
    private ExamplePaths() { }

    public static Path directory(String topic) throws IOException {
        // 상대 경로의 기준은 소스 파일 위치가 아니라 실행 작업 디렉터리입니다.
        Path directory = Path.of("build", "learning-data", topic);
        Files.createDirectories(directory); // 부모 폴더까지 만들며, 이미 있어도 괜찮습니다.
        return directory;
    }
}
