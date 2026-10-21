package bytestreams;

import support.ExamplePaths;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

/** 한 바이트씩 읽으며 read()의 반환값과 EOF를 확인합니다. */
public class ByteReadExample {
    public static void main(String[] args) throws IOException {
        Path source = ExamplePaths.directory("bytes").resolve("ascii.txt");
        Files.writeString(source, "Java!\n", StandardCharsets.US_ASCII);
        // try가 끝나면 정상 종료/예외 발생 여부와 관계없이 close()가 호출됩니다.
        try (FileInputStream input = new FileInputStream(source.toFile())) {
            int value;
            // 0~255는 실제 바이트, -1은 EOF(파일 끝). byte 변수로 받으면 구분할 수 없습니다.
            while ((value = input.read()) != -1) {
                System.out.println("바이트 값: " + value);
            }
        }
        // 한글은 UTF-8에서 여러 바이트를 사용하므로 (char)value로 복원하면 안 됩니다.
    }
}
