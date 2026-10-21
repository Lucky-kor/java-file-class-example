package collectiontext;

import support.ExamplePaths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** 컬렉션 → 텍스트 → 새 컬렉션의 왕복 과정을 한 번의 실행으로 확인합니다. */
public class CollectionRoundTripExample {
    public static void main(String[] args) throws IOException {
        Path directory = ExamplePaths.directory("collections");
        List<String> names = List.of("러키", "라떼", "러키");
        Path namesFile = directory.resolve("names.txt");
        // 한 원소를 한 줄로 저장합니다. 이 형식의 원소에는 줄바꿈이 없어야 합니다.
        Files.write(namesFile, names, StandardCharsets.UTF_8);
        List<String> restoredNames = Files.readAllLines(namesFile, StandardCharsets.UTF_8);
        System.out.println("List(순서·중복 유지): " + restoredNames);
        Set<String> uniqueNames = new LinkedHashSet<>(restoredNames);
        System.out.println("Set(중복 제거): " + uniqueNames);
        Path uniqueFile = directory.resolve("unique-names.txt");
        Files.write(uniqueFile, uniqueNames, StandardCharsets.UTF_8);
        Set<String> restoredUnique = new LinkedHashSet<>(Files.readAllLines(uniqueFile, StandardCharsets.UTF_8));
        System.out.println("Set 복원: " + restoredUnique);

        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("러키", 90);
        scores.put("라떼", 80);
        Path scoresFile = directory.resolve("scores.txt");
        ScoreTextStore.write(scoresFile, scores);
        Map<String, Integer> restoredScores = ScoreTextStore.read(scoresFile);
        System.out.println("Map 복원: " + restoredScores);
        System.out.println("List 왕복 성공: " + names.equals(restoredNames));
        System.out.println("Set 왕복 성공: " + uniqueNames.equals(restoredUnique));
        System.out.println("Map 왕복 성공: " + scores.equals(restoredScores));
        // readAllLines는 전체 내용을 메모리에 올립니다. 큰 파일은 BufferedReader로 순회하세요.
    }
}
