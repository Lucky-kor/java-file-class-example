package paths;

import support.ExamplePaths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** File과 Path는 파일 내용이 아니라 파일/디렉터리의 경로를 나타냅니다. */
public class FilePathExample {
    public static void main(String[] args) throws IOException {
        Path directory = ExamplePaths.directory("paths");
        File file = directory.resolve("hello.txt").toFile();
        System.out.println("작업 디렉터리: " + Path.of("").toAbsolutePath());
        System.out.println("절대 경로: " + file.getAbsolutePath());
        // new File(...)만으로 실제 파일이 만들어지지는 않습니다.
        // 두 번째 실행에서는 지난번 파일이 있으므로 true일 수 있습니다.
        System.out.println("쓰기 전 존재: " + file.exists());
        Files.writeString(file.toPath(), "안녕하세요, File과 Path!\n"); // 기본 UTF-8
        System.out.println("파일인가: " + file.isFile());
        System.out.println("폴더인가: " + directory.toFile().isDirectory());
        System.out.println("파일 크기(바이트): " + file.length()); // 글자 수와 다릅니다.
        System.out.println(Files.readString(file.toPath()));
    }
}
