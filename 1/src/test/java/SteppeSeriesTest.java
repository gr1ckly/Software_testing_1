import org.example.Main;
import org.example.SteppeSeries;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты разложения степенного ряда")
public class SteppeSeriesTest {
    record TestData(double x, int iteration, double accuracy, double ans) {}

    static Stream<TestData> getTestStream() {
        return Stream.iterate(-25, x -> x <= 31.4, x -> x += 0.1)
                .map(x -> new TestData(x, 100, 0.05, Math.sin(x)));
    }

    @Test
    @DisplayName("Проверка проброса исключения при некорректном количестве итераций степенного ряда")
    void testExceptions() {
        assertThrows(Exception.class,
                () -> SteppeSeries.sin(0.0, -1 ),
                "Некорректное количество слагаемых степенного ряда");
        assertThrows(Exception.class,
                () -> SteppeSeries.sin(0.0, 101),
                "Некорректное количество слагаемых степенного ряда");
    }

    @Test
    @DisplayName("Проверка значений -Pi, -Pi/2, 0, Pi/2, Pi")
    void testBasic() throws Exception {
        List<TestData> values = List.of(
                new TestData(-Math.PI, 100, 0.05, 0.0),
                new TestData(Math.PI, 100, 0.05, 0.0),
                new TestData(-Math.PI / 2.0, 100, 0.05, -1.0),
                new TestData(Math.PI / 2.0, 100, 0.05, 1.0),
                new TestData(0.0, 100, 0.05, 0.0)
        );

        for (TestData currTest : values) {
            assertEquals(currTest.ans, SteppeSeries.sin(currTest.x, currTest.iteration), currTest.accuracy);
        }
    }

    // проверить точность при разных счкетчиках
    @ParameterizedTest
    @DisplayName("Проверка значений от -3.14 до 3.14 при помощи генератора")
    @MethodSource("getTestStream")
    void testStream(TestData currTest) throws Exception {
        assertEquals(currTest.ans, SteppeSeries.sin(currTest.x, currTest.iteration), currTest.accuracy);
    }
}
