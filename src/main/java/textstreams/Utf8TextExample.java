package textstreams;

import support.ExamplePaths;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Reader/Writer는 인코딩을 이용해 바이트와 문자를 연결합니다. */
public class Utf8TextExample {
    public static void main(String[] args) throws IOException {
        Path target = ExamplePaths.directory("text").resolve("greetings.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(target, StandardCharsets.UTF_8)) {
            writer.write("안녕하세요, 러키!");
            writer.newLine(); // 운영체제에 맞는 줄바꿈을 기록합니다.
            writer.newLine(); // 빈 줄도 읽을 수 있는 데이터입니다.
            writer.write("Java 파일 입출력");
        }
        try (BufferedReader reader = Files.newBufferedReader(target, StandardCharsets.UTF_8)) {
            String line;
            int number = 1;
            // readLine()은 줄바꿈을 제외합니다. 빈 줄은 "", 파일 끝은 null입니다.
            while ((line = reader.readLine()) != null) {
                System.out.println(number++ + ": [" + line + "]");
            }
        }
    }
}
