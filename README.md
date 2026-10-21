# 자바 파일 입출력 학습 가이드

파일 입출력은 **프로그램의 데이터를 파일에 저장하고, 다시 읽는 과정**입니다. 이 프로젝트에서는 경로를 이해한 뒤 바이트와 문자를 읽고 쓰고, 마지막으로 컬렉션을 텍스트로 저장하고 복원합니다.

각 예제는 `main()`을 따로 실행할 수 있습니다. 입력 파일을 직접 준비하므로 앞 예제를 먼저 실행하지 않아도 됩니다. 코드를 읽고, 실행 결과를 예상하고, 값을 바꾸어 확인하는 순서로 학습하세요.

## 시작하기 전에

변수, 배열, 반복문, 클래스, List·Set·Map의 기본 사용법을 알고 있으면 좋습니다.

- **경로**: 파일이나 폴더의 위치입니다. 상대 경로는 실행 작업 디렉터리를 기준으로 해석합니다.
- **바이트**: 파일에 실제로 저장되는 데이터 단위입니다. 문자 수와 파일의 바이트 수는 다를 수 있습니다.
- **인코딩**: 문자열을 바이트로 바꾸는 규칙입니다. 읽을 때도 같은 규칙으로 해석해야 합니다.
- **스트림**: 데이터를 순서대로 읽거나 쓰는 통로입니다.
- **버퍼**: 작은 읽기·쓰기 요청을 모아 처리하는 메모리 공간입니다.
- **EOF**: 파일의 끝입니다. `read()`는 -1, `readLine()`은 null로 알립니다.
- **IOException**: 파일 없음, 접근 권한, 읽기·쓰기 실패 등 입출력 과정에서 발생할 수 있는 예외입니다.

## 실행 방법

JDK 21을 사용합니다. IntelliJ IDEA에서 프로젝트를 Gradle로 열고 Project SDK와 Gradle JVM을 JDK 21로 설정하세요. 아래 표에서 원하는 클래스를 열고 `main()` 옆 실행 버튼을 누릅니다. 실행 설정의 Working directory는 프로젝트 루트로 지정하세요.

터미널에서는 프로젝트 루트에서 실행합니다. Windows에서는 `./gradlew` 대신 `gradlew.bat`를 사용하세요.

```sh
# 컴파일과 저장·복원 검증
./gradlew clean test

# 하나만 실행 (example 생략 시 paths.FilePathExample)
./gradlew runExample -Pexample=textstreams.Utf8TextExample

# 학습 순서대로 전체 실행
./gradlew runAllExamples

# Gradle로 컴파일한 클래스를 직접 실행하는 방법
java -cp build/classes/java/main collectiontext.CollectionRoundTripExample
```

첫 Gradle 실행은 배포 파일·테스트 의존성을 내려받기 위한 네트워크 연결이 필요할 수 있습니다.

생성 파일은 모두 `build/learning-data/` 아래에 있습니다. `./gradlew clean`은 이 실습 결과도 지웁니다. 각 예제는 정해진 실습 파일을 덮어쓰므로 여러 번 실행해도 결과가 누적되지 않습니다. 단, 경로 예제의 **쓰기 전 존재 여부**는 첫 실행과 재실행에서 달라질 수 있습니다.

## 폴더 구조와 권장 학습 순서

패키지 이름은 학습 주제를 나타냅니다. `support`는 공통 출력 폴더를 만드는 보조 코드이며 가장 나중에 읽어도 됩니다.

| 순서 | 학습 파일 | 핵심 개념 | 생성 폴더 |
| --- | --- | --- | --- |
| 1 | [FilePathExample](src/main/java/paths/FilePathExample.java) | File, Path, 상대·절대 경로 | paths |
| 2 | [ByteReadExample](src/main/java/bytestreams/ByteReadExample.java) | 바이트 읽기, EOF, 자원 해제 | bytes |
| 3 | [ByteWriteExample](src/main/java/bytestreams/ByteWriteExample.java) | 덮어쓰기·이어쓰기, UTF-8, 출력 버퍼 | bytes |
| 4 | [BufferedCopyExample](src/main/java/bytestreams/BufferedCopyExample.java) | 버퍼와 실제 읽은 길이, 이진 복사 | copy |
| 5 | [Utf8TextExample](src/main/java/textstreams/Utf8TextExample.java) | 문자 입출력, 빈 줄, 줄바꿈 | text |
| 6 | [CollectionRoundTripExample](src/main/java/collectiontext/CollectionRoundTripExample.java), [ScoreTextStore](src/main/java/collectiontext/ScoreTextStore.java) | List·Set·Map 저장과 복원, 형식 검증 | collections |
| 7 | [FilesOperationsExample](src/main/java/fileoperations/FilesOperationsExample.java) | 복사·이동·목록·삭제 | operations |
| 8 | [ExpectedFailuresExample](src/main/java/ioerrors/ExpectedFailuresExample.java) | 파일 없음과 데이터 형식 오류 | errors |

## 1. File과 Path: 경로와 파일은 다릅니다

`new File(...)`이나 `Path.of(...)`는 경로를 나타내는 객체를 만듭니다. 실제 파일 생성은 쓰기 등의 작업에서 일어납니다. `File`은 기존 API, `Path`와 `Files`는 경로 표현과 파일 작업을 분리한 API입니다. `toPath()`와 `toFile()`로 연결할 수 있습니다.

**실행하며 확인하기:** 작업 디렉터리와 절대 경로를 비교하세요. 쓰기 후 `isFile()`과 디렉터리의 `isDirectory()`는 true입니다. 파일 크기는 글자 수가 아닌 바이트 수입니다. `File.length()`의 0만으로 빈 파일과 없는 파일을 구분할 수는 없습니다.

**직접 해보기:** 저장할 문자열을 영어에서 한글로 바꾸고 파일 크기를 비교하세요. IDE의 작업 디렉터리를 바꾸면 같은 상대 경로가 어디를 가리킬지 예상해 보세요.

## 2. 바이트 읽기: 왜 int로 받나요?

`FileInputStream.read()`는 한 바이트를 0~255 범위의 int로 반환하며 파일 끝에서 -1을 반환합니다. byte는 -128~127 범위이므로 EOF와 모든 바이트를 구분할 용도로는 부족합니다.

**실행하며 확인하기:** `Java!`와 줄바꿈의 바이트 값인 74, 97, 118, 97, 33, 10이 차례로 출력됩니다. 파일 끝에서는 반복문이 종료됩니다. `try (...)` 안에서 연 스트림은 자동으로 닫히므로 `close()`를 또 부르지 않습니다.

**직접 해보기:** 입력 문자열을 빈 문자열로 바꾸세요. 아무 바이트도 출력되지 않는 이유를 설명해 보세요. 한글을 읽는 데 `(char) value`를 사용할 수 없는 이유는 5단계와 연결해서 생각하세요.

## 3. 바이트 쓰기: 덮어쓰기와 이어쓰기

`new FileOutputStream(file)`은 기존 내용을 비우고, 두 번째 인자가 true이면 뒤에 추가합니다. `getBytes(UTF_8)`로 문자열을 바이트로 바꾸고, 읽을 때도 UTF-8을 지정합니다.

**실행하며 확인하기:** 고양이 소개 두 줄이 출력됩니다. 재실행해도 두 줄입니다. 첫 쓰기가 파일을 초기화하기 때문입니다. BufferedOutputStream은 데이터를 모아 전달합니다. 바깥 스트림의 close는 버퍼를 비우고 안쪽 스트림도 닫습니다.

`flush()`는 스트림을 계속 열어 둔 채 버퍼의 내용을 전달할 때 사용합니다. 모든 쓰기 직후 호출하면 버퍼의 이점을 줄일 수 있으며, 디스크에 영구 보존되었다는 보장도 아닙니다.

**직접 해보기:** 두 번째 FileOutputStream의 true를 false로 바꾸세요. 첫 줄이 사라지는 이유를 확인하고 복구하세요.

## 4. 버퍼로 이진 데이터 복사하기

입력의 `read(buffer)`는 이번에 읽은 바이트 수를 반환합니다. `write(buffer, 0, count)`로 그 길이만 써야 합니다. 배열 크기만큼 항상 읽힌다고 가정하면 안 됩니다. BufferedInputStream의 내부 버퍼와 코드에서 만든 byte 배열은 별개입니다.

**실행하며 확인하기:** 예제는 7바이트를 4바이트 배열로 옮깁니다. 마지막 읽기는 배열보다 짧고 원본 비교는 true입니다. 파일 스트림에서는 보통 4, 3이 출력되지만 일반적인 InputStream 구현은 더 적은 양을 반환할 수도 있습니다.

**직접 해보기:** 배열 크기를 1, 3, 8로 바꾸어도 비교가 true인지 확인하세요. `output.write(buffer)`로 잘못 바꾸면 어떤 크기에서 불필요한 바이트가 생기는지 관찰하고 복구하세요. 이미지·음성·압축 파일도 문자열 변환 없이 바이트 그대로 복사해야 합니다.

## 5. 문자 읽기와 쓰기: 한글과 빈 줄

문자 데이터에는 Reader/Writer를 사용합니다. `Files.newBufferedReader/Writer`는 지정한 인코딩으로 바이트와 문자를 변환하고 버퍼링합니다. 한글 한 글자는 UTF-8에서 여러 바이트이므로 한 바이트를 char로 바꾸어서는 복원할 수 없습니다.

**실행하며 확인하기:** `1: [안녕하세요, 러키!]`, `2: []`, `3: [Java 파일 입출력]`이 출력됩니다. 빈 줄은 빈 문자열이며 EOF의 null과 다릅니다. `readLine()`은 줄바꿈 문자를 반환하지 않습니다. 마지막 줄에 줄바꿈이 없어도 읽을 수 있습니다.

**직접 해보기:** 빈 줄을 두 개로 늘리고 행 번호를 예상하세요. `newLine()`이 운영체제에 따라 다른 줄바꿈을 쓰므로 파일 바이트 수가 달라질 수 있다는 점도 확인하세요.

## 6. 컬렉션을 텍스트로 저장하고 복원하기

텍스트 파일은 자바 객체의 타입을 기억하지 않습니다. 저장 형식과 복원 규칙을 직접 정해야 합니다. 이 예제는 자바 객체 직렬화가 아닌 **텍스트 저장**입니다.

- List: 한 원소를 한 줄로 저장하고 순서와 중복을 유지합니다. 원소에 줄바꿈이 없어야 합니다.
- Set: LinkedHashSet으로 중복을 제거하고 처음 등장한 순서를 유지합니다. Set.of나 HashSet의 순서를 전제로 하지 않습니다.
- Map: 한 줄에 `이름:점수`를 기록합니다. LinkedHashMap으로 입력 순서를 유지합니다.

**실행하며 확인하기:** List는 `[러키, 라떼, 러키]`, Set은 `[러키, 라떼]`, Map은 `{러키=90, 라떼=80}`입니다. 각각의 왕복 성공은 true입니다. Map.equals는 키·값을 비교하며 순서까지 비교하는 것은 아닙니다.

[ScoreTextStore](src/main/java/collectiontext/ScoreTextStore.java)의 규칙을 읽어 보세요. 이름은 공백만으로 이루어질 수 없고 콜론과 줄바꿈을 포함할 수 없습니다. 점수는 0~100의 정수입니다. 빈 줄과 중복 이름은 오류로 처리하고 줄 번호를 알려 줍니다. 빈 파일은 빈 Map으로 읽습니다. 이름 앞뒤 공백을 자동 제거하지 않으므로 `러키`와 ` 러키`는 다른 키입니다.

저장 전에 전체 입력을 검증하므로 잘못된 값 때문에 기존 파일이 비워지는 것을 방지합니다. 다만 저장 도중 디스크 오류까지 복구하는 코드는 아닙니다. 이 형식은 콜론을 포함한 이름이나 여러 줄 값을 위한 이스케이프를 지원하지 않습니다. 그런 요구가 생기면 CSV·JSON 등의 형식과 전용 라이브러리를 검토하세요.

**직접 해보기:** 이름과 점수를 추가하고 복원 결과를 확인하세요. Set을 List로 바꿨을 때 중복 처리가 어떻게 달라지는지 설명하세요. Map에 같은 키를 두 번 put하면 저장 전에 이미 덮어써진다는 점도 확인하세요.

## 7. Files로 파일 관리하기

`createDirectories`는 필요한 부모 폴더를 함께 만듭니다. `copy`는 원본을 유지하고 `move`는 경로를 옮깁니다. 대상이 있으면 기본적으로 실패하므로 예제에서는 재실행을 위해 REPLACE_EXISTING을 사용합니다.

**실행하며 확인하기:** 이동 후 copied는 false, moved는 true입니다. 목록에는 original.txt와 moved.txt가 보입니다. 첫 삭제는 true, 두 번째 삭제는 false입니다. `Files.list()`가 반환한 Stream도 닫아야 합니다.

**직접 해보기:** REPLACE_EXISTING을 제거하고 같은 대상으로 두 번 복사하는 코드를 추가해 오류를 관찰하세요. 실습은 `build/learning-data/operations` 안의 파일로 진행하세요.

## 8. 예상한 실패와 실제 오류 구분하기

파일이 없는 상황은 NoSuchFileException으로 설명합니다. 잘못된 점수 파일은 ScoreTextStore가 IOException으로 줄 번호와 원인을 전달합니다. 정상 결과처럼 진행하지 않고 실패를 드러내는 것이 중요합니다.

**실행하며 확인하기:** 파일이 없다는 메시지와 `2번째 줄: 점수는 정수여야 합니다.`라는 설명이 출력됩니다. 이는 학습용으로 의도한 실패입니다. 나머지 예제의 main은 IOException을 호출자에게 전달하므로 예상하지 못한 파일 접근 오류가 숨겨지지 않습니다.

**직접 해보기:** 잘못된 점수를 101로 바꾸거나 두 줄의 이름을 같게 하여 범위·중복 검증을 확인하세요. 예제가 실행할 때 입력을 다시 쓰므로 생성된 파일 대신 예제의 `Files.writeString` 내용을 수정해야 합니다.

## 자주 헷갈리는 점

| 질문 | 확인할 내용 |
| --- | --- |
| 파일을 찾을 수 없어요 | 소스 위치가 아닌 작업 디렉터리와 절대 경로를 확인하세요. |
| 한글이 깨져요 | 쓰기와 읽기에 같은 UTF-8을 지정하고 바이트를 char로 직접 바꾸지 마세요. 터미널 인코딩도 확인하세요. |
| 쓴 내용이 아직 안 보여요 | 출력 버퍼가 남아 있는지 확인하세요. 예제는 스트림을 닫은 뒤 다시 읽습니다. |
| 빈 줄에서 읽기가 끝나요 | 빈 문자열이 아니라 null과 비교해야 합니다. |
| 파일이 클 때도 readAllLines를 써도 되나요 | 전체 내용을 메모리에 올리므로 큰 파일은 BufferedReader로 한 줄씩 처리하세요. |
| exists가 true였는데 읽기가 실패해요 | 확인 이후 파일이 바뀔 수 있어 실제 읽기 실패도 처리해야 합니다. |

## 기존 예제에서 달라진 위치

| 기존 파일 | 새 학습 위치 |
| --- | --- |
| Main.java | paths/FilePathExample.java |
| FileInputStreamExample.java | bytestreams/ByteReadExample.java |
| FileOutputStreamExample.java | bytestreams/ByteWriteExample.java |
| FileInputStreamBufferedExample.java | bytestreams/BufferedCopyExample.java |
| collectionPersistence/WriterExample.java, ReaderExample.java | collectiontext/CollectionRoundTripExample.java, ScoreTextStore.java |
| src/main/java/test.txt | src/main/resources/samples/hello.txt |

루트의 기존 `cat.txt`, `java.txt`, `names.txt`, `scores.txt`, `uniqueWords.txt`와 기존 이미지 리소스는 보존했습니다. 새 예제는 이 파일들에 의존하거나 덮어쓰지 않습니다. 기존 점수 파일의 `[이름:점수]` 형식과 새 학습 형식 `이름:점수`는 다르므로 그대로 혼용하지 마세요.

## 다음 학습과 검증

8단계까지 마쳤다면 [실습 문제와 힌트](PRACTICE.md)에서 작은 메모장과 점수 파일 검증을 연습하세요.

`./gradlew test`는 한글·순서·경계 점수의 왕복, 잘못된 행과 중복 이름, 빈 파일, 없는 파일, 잘못된 입력이 기존 파일을 비우지 않는지 검증합니다. `./gradlew runAllExamples`는 모든 main을 실행하는 확인용 명령입니다. 실행 성공만으로 출력 내용까지 자동 검증하는 것은 아닙니다.
