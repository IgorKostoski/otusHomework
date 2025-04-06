package org.example.hw03;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.hw03.annotations.After;
import org.example.hw03.annotations.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyTestRunnerTest {

    private static final Logger logger = Logger.getLogger(MyTestRunnerTest.class.getName());

    @BeforeEach
    void resetCounters() {
        TestClassForRunner.beforeCount = 0;
        TestClassForRunner.testCount = 0;
        TestClassForRunner.afterCount = 0;
        logger.log(Level.INFO, "Static counters reset");
    }

    @AfterEach
    void logTestCompletition() {
        logger.log(Level.INFO, "Finished Junit test method");
    }

    @Test
    void testRunnerExecutesLifecycleAndTestsCorrectly() {
        logger.info("Starting testRunnerExecutesLifecycleAndTestsCorrectly");

        TestRunner.run(TestClassForRunner.class.getName());

        assertEquals(2, TestClassForRunner.beforeCount, "@Before methods count mismatch");
        assertEquals(2, TestClassForRunner.testCount, "@Test methods count mismatch");
        assertEquals(2, TestClassForRunner.afterCount, "@After methods count mismatch");

        logger.info("testRunnerExecutesLifeCycleAndTestsCorrectly completed");
    }

    @Test
    void testRunnerHandlesTestClassExceptionGracefully() {
        logger.info("Starting testRunnerHandlesTestClassExceptionGracefully");
        String testClassName = TestClassWithException.class.getName();

        Assertions.assertDoesNotThrow(
                () -> TestRunner.run(testClassName),
                "TestRunner.run should handle exceptions from tests methods gracefully");

        logger.info("testRunnerHandlesTestsClassExceptionsGracefully completed");
    }

    @Test
    void testRunnerHandlesClassNotFoundExceptionGracefully() {
        logger.info("Starting testRunnerHandlesClassNotFoundExceptionGracefully");
        String invalidClassName = "hw03-annotations/src/main/java";

        Assertions.assertDoesNotThrow(
                () -> TestRunner.run(invalidClassName),
                "TestRunner.run should handle ClassNotFoundException gracefully");

        logger.info("testRunnerHandlesClassNotFoundExceptionGracefully completed");
    }

    public static class TestClassForRunner {
        static int beforeCount = 0;
        static int testCount = 0;
        static int afterCount = 0;

        @Before
        public void before() {
            beforeCount++;
            logger.info("TestClassForRunner @Before method called");
        }

        @org.example.hw03.annotations.Test
        public void test1() {
            testCount++;
            logger.info("TestClassForRunner @Test 1 method called");
        }

        @org.example.hw03.annotations.Test
        public void test2() {
            testCount++;
            logger.info("TestClassForRunner @Test 2 method called");
        }

        @After
        public void after() {
            afterCount++;
            logger.info("ATestClassForRunner @After method called");
        }
    }

    public static class TestClassWithException {
        @org.example.hw03.annotations.Test
        public void failingTest() {
            logger.info("TestClassWithException failingTest called - throwing exception...");
            throw new RuntimeException("Test failed intentionally");
        }

        @After
        void cleanupAfterFailure() {
            logger.info("TestClassWithException @After running even after test failure.");
        }
    }
}
