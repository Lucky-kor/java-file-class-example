package bytestreams;

import support.ExamplePaths;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** 덮어쓰기와 이어쓰기를 비교하고, 문자열과 바이트 사이의 인코딩을 지정합니다. */
public class ByteWriteExample {
    public static void main(String[] args) throws IOException {
        Path target = ExamplePaths.directory("bytes").resolve("cat.txt");
        // 기본 생성자는 기존 파일을 비웁니다. 매 실행의 시작 상태가 같아집니다.
        try (FileOutputStream output = new FileOutputStream(target.toFile())) {
            output.write("러키는 귀여운 고양이!\n".getBytes(StandardCharsets.UTF_8));
        }
        // true는 이어쓰기입니다. 가장 바깥 스트림을 닫으면 내부 스트림도 닫힙니다.
        try (BufferedOutputStream output = new BufferedOutputStream(
                new FileOutputStream(target.toFile(), true))) {
            output.write("라떼도 귀여워요.\n".getBytes(StandardCharsets.UTF_8));
            // close()가 버퍼를 비우므로 여기서는 flush()를 따로 호출할 필요가 없습니다.
            // flush()는 버퍼의 데이터를 전달하지만 디스크 영구 저장까지 보장하지 않습니다.
        }
        System.out.print(Files.readString(target, StandardCharsets.UTF_8));
    }
}
