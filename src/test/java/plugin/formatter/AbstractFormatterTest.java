package plugin.formatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

public abstract class AbstractFormatterTest {

    protected abstract QueryFormatter queryFormatterForTest();

    private static final String INPUT_SQL = "input.sql";
    private static final String EXPECTED_SQL = "expected.sql";

    @TestFactory
    protected List<DynamicTest> testFormatter() {
        QueryFormatter queryFormatter = queryFormatterForTest();

        return loadTestCases()
                .map(testCase -> {

                    Executable executable = () -> {
                        String actual = queryFormatter.format(testCase.input);

                        assertEquals(testCase.expected, actual);
                    };

                    return dynamicTest(testCase.name, executable);
                }).collect(Collectors.toList());
    }

    private Stream<TestCase> loadTestCases() {
        Path testCaseDir = Path.of(getTestCaseDirectory());

        try {
            return Files.walk(testCaseDir)
                    .filter(Files::isDirectory)
                    .filter(dir -> isCaseDir(dir.getFileName().toString()))
                    .flatMap(this::loadTestCasesInDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test case", e);
        }
    }

    private Stream<TestCase> loadTestCasesInDirectory(Path directory) {
        try {
            String testCaseName = directory.toString();
            String input = Files.readString(directory.resolve(INPUT_SQL));
            String expected = Files.readString(directory.resolve(EXPECTED_SQL));
            return Stream.of(new TestCase(testCaseName, input, expected));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test case", e);
        }
    }

    private boolean isCaseDir(String dirpath) {
        return dirpath.startsWith("case");
    }

    private String getTestCaseDirectory() {
        return "src/test/resources/sql/" + getClass().getSimpleName()
                .replace("Test", "");
    }

    private record TestCase(String name, String input, String expected) {

        private TestCase(String name, String input, String expected) {
            this.name = name;
            this.input = normalizeLineSeparators(input);
            this.expected = normalizeLineSeparators(expected);
        }

        // Normalize CRLF to LF
        private String normalizeLineSeparators(String input) {
            return input.replaceAll("\\r\\n", "\n");
        }
    }
}
