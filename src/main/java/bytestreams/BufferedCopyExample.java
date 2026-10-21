package bytestreams;

import support.ExamplePaths;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** 이미지도 문자열로 바꾸지 않고 바이트 그대로 복사해야 합니다. */
public class BufferedCopyExample {
    public static void main(String[] args) throws IOException {
        Path directory = ExamplePaths.directory("copy");
        Path source = directory.resolve("source.bin");
        Path target = directory.resolve("copied.bin");
        byte[] original = {0, 1, 127, (byte) 128, (byte) 255, 10, 20};
        Files.write(source, original); // 외부 이미지 없이도 재현할 수 있는 이진 데이터
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(source.toFile()));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(target.toFile()))) {
            byte[] buffer = new byte[4]; // 마지막에는 4바이트보다 적게 읽힙니다.
            int count;
            while ((count = input.read(buffer)) != -1) {
                // 배열 전체를 쓰면 마지막에 이전 데이터까지 복사될 수 있습니다.
                output.write(buffer, 0, count);
                System.out.println("이번에 읽은 바이트 수: " + count);
            }
        }
        // 출력 스트림을 닫은 다음 결과를 읽어야 버퍼에 남은 내용까지 비교할 수 있습니다.
        System.out.println("원본과 동일: " + Arrays.equals(original, Files.readAllBytes(target)));
    }
}
