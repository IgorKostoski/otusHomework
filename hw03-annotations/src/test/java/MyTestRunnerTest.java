import static org.junit.jupiter.api.Assertions.assertEquals;

import annotations.After;
import annotations.Before;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

public class MyTestRunnerTest {

    private static final Logger logger = Logger.getLogger(MyTestRunnerTest.class.getName());

    @Test
    public void testRunnerRunsTest() throws ClassNotFoundException {
        logger.info("Starting testRunnerRunsTest");

        TestRunner.run(TestClassForRunner.class.getName());

        assertEquals(2, TestClassForRunner.testCount);
        assertEquals(2, TestClassForRunner.beforeCount);
        assertEquals(2, TestClassForRunner.afterCount);

        logger.info("testRunnerHandlesExceptions completed");
    }

    @Test
    public void testRunnerHandlesExceptions() throws ClassNotFoundException {
        logger.info("Starting testRunnerHandlesExceptions");

        TestRunner.run(TestClassWithException.class.getName());

        logger.info("testRunnerhandlesExceptions completed");
    }

    public static class TestClassForRunner {
        static int beforeCount = 0;
        static int testCount = 0;
        static int afterCount = 0;

        @Before
        public void before() {
            beforeCount++;
            logger.info("Before method called");
        }

        @annotations.Test
        public void test1() {
            testCount++;
            logger.info("Test 1 method called");
        }

        @annotations.Test
        public void test2() {
            testCount++;
            logger.info("Test 2 method called");
        }

        @After
        public void after() {
            afterCount++;
            logger.info("After method called");
        }
    }

    public static class TestClassWithException {
        @annotations.Test
        public void failingTest() {
            logger.info("failingTest called");
            throw new RuntimeException("Test failed intentionally");
        }
    }
}
