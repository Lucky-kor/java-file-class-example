package fileoperations;

import support.ExamplePaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

/** Files로 디렉터리 생성, 복사, 이동, 목록 조회, 삭제를 연습합니다. */
public class FilesOperationsExample {
    public static void main(String[] args) throws IOException {
        Path directory = ExamplePaths.directory("operations");
        Path source = directory.resolve("original.txt");
        Path copied = directory.resolve("copied.txt");
        Path moved = directory.resolve("moved.txt");
        Files.writeString(source, "파일 작업 연습\n");
        // 기본 copy는 대상이 이미 있으면 실패합니다. 여기서는 재실행을 위해 교체합니다.
        Files.copy(source, copied, StandardCopyOption.REPLACE_EXISTING);
        Files.move(copied, moved, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("이동 후 copied 존재: " + Files.exists(copied));
        System.out.println("이동 후 moved 존재: " + Files.exists(moved));
        // list가 반환하는 Stream도 디렉터리 자원을 사용하므로 닫아야 합니다.
        try (Stream<Path> entries = Files.list(directory)) {
            entries.sorted().forEach(path -> System.out.println(path.getFileName()));
        }
        // 이 예제에서 만든 moved 파일만 지웁니다. 디렉터리 전체를 재귀 삭제하지 않습니다.
        System.out.println("첫 삭제: " + Files.deleteIfExists(moved));
        System.out.println("두 번째 삭제: " + Files.deleteIfExists(moved));
    }
}
