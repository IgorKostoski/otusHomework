import annotations.After;
import annotations.Before;
import annotations.Test;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestRunner {
    private static final Logger logger = Logger.getLogger(TestRunner.class.getName());

    private TestRunner() {
        throw new IllegalStateException("Utility class");
    }

    public static void run(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getDeclaredMethods();

            List<Method> beforeMethods = new ArrayList<>();
            List<Method> testMethods = new ArrayList<>();
            List<Method> afterMethods = new ArrayList<>();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Before.class)) {
                    beforeMethods.add(method);
                } else if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                } else if (method.isAnnotationPresent(After.class)) {
                    afterMethods.add(method);
                }
            }

            int passed = 0;
            int failed = 0;
            List<String> failedTests = new ArrayList<>();

            logger.info("Starting test for class " + className);

            for (Method testMethod : testMethods) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                boolean beforeSuccessful = true;

                try {
                    for (Method beforeMethod : beforeMethods) {
                        try {
                            logger.info("Executing @Before " + beforeMethod.getName());
                            beforeMethod.invoke(instance);
                        } catch (InvocationTargetException e) {
                            beforeSuccessful = false;
                            logger.log(
                                    Level.SEVERE,
                                    "@Before " + beforeMethod.getName() + " failed: " + e.getTargetException());
                            break; // Skip test execution
                        }
                    }

                    if (beforeSuccessful) {
                        try {
                            logger.info("Executing @Test " + testMethod.getName());
                            testMethod.invoke(instance);
                            passed++;
                            logger.info("Test " + testMethod.getName() + " PASSED ✅");
                        } catch (InvocationTargetException e) {
                            Throwable cause = e.getCause();
                            if (cause instanceof RuntimeException
                                    && cause.getMessage().contains("Test failed intentionally")) {
                                logger.warning("Test " + testMethod.getName() + " intentionally failed: "
                                        + cause.getMessage());
                            } else {
                                failed++;
                                failedTests.add(testMethod.getName());
                                logger.log(Level.SEVERE, "Test " + testMethod.getName() + " FAILED ❌: " + cause, cause);
                            }
                        }
                    } else {
                        failed++;
                        failedTests.add(testMethod.getName());
                        logger.info("Skipping test " + testMethod.getName() + " due to @Before failure ⚠️");
                    }
                } finally {
                    for (Method afterMethod : afterMethods) {
                        try {
                            logger.info("Executing @After " + afterMethod.getName());
                            afterMethod.invoke(instance);
                        } catch (InvocationTargetException e) {
                            logger.log(
                                    Level.SEVERE,
                                    "Error in @After method: " + afterMethod.getName() + " - " + e.getTargetException(),
                                    e);
                        }
                    }
                }
            }

            logger.info(
                    String.format("Total Tests: %d | Passed: %d ✅ | Failed: %d ❌", testMethods.size(), passed, failed));
            if (!failedTests.isEmpty()) {
                logger.info("Failed Tests: " + failedTests);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error running test: " + e.getMessage(), e);
        }
    }
}
