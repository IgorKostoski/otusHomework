import annotations.After;
import annotations.Before;
import annotations.Test;
import java.lang.annotation.Annotation;
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
            List<Method> beforeMethods = getMethodsByAnnotation(clazz, Before.class);
            List<Method> testMethods = getMethodsByAnnotation(clazz, Test.class);
            List<Method> afterMethods = getMethodsByAnnotation(clazz, After.class);

            int passed = 0;
            int failed = 0;

            List<String> faieldTests = new ArrayList<>();

            logger.info("Starting test for class " + className);

            for (Method testMethod : testMethods) {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                boolean beforeSuccessful = executeBeforeMethods(beforeMethods, instance);

                if (beforeSuccessful) {
                    boolean testPassed = executeTestmethod(testMethod, instance);
                    if (testPassed) {
                        passed++;

                    } else {
                        failed++;
                        faieldTests.add(testMethod.getName());
                    }
                } else {
                    failed++;
                    faieldTests.add(testMethod.getName());
                    logger.info("Skipping test " + testMethod.getName() + "due to @Before failure");
                }

                executeAfterMethods(afterMethods, instance);
            }
            logSummary(testMethods, passed, failed, faieldTests);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error running test: " + e.getMessage(), e);
        }
    }

    private static List<Method> getMethodsByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> annotatedMethods = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    private static boolean executeBeforeMethods(List<Method> beforeMethods, Object instance) {
        for (Method beforeMethod : beforeMethods) {
            try {
                logger.info("Executing @Before " + beforeMethod.getName());
                beforeMethod.invoke(instance);
            } catch (InvocationTargetException e) {
                logger.log(Level.SEVERE, "@Before " + beforeMethod.getName() + " failed: " + e.getTargetException());
                return false;
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, "@Before method " + beforeMethod.getName() + " is not accesoble, e");
                return false;
            }
        }
        return true;
    }

    private static boolean executeTestmethod(Method testMethod, Object instance) {
        try {
            logger.info("Executing @Test " + testMethod.getName());
            testMethod.invoke(instance);
            logger.info("Test " + testMethod.getName() + "  PASSED ✅");
            return true;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException && cause.getMessage().contains("Test failed intentionally")) {
                logger.warning("Test " + testMethod.getName() + " intentionally failed: " + cause.getMessage());
                return true;
            } else {
                logger.log(Level.SEVERE, "Test " + testMethod.getName() + " FAILED ❌" + cause, cause);
                return false;
            }
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Test method " + testMethod.getName() + "is not accesible", e);
            return false;
        }
    }

    private static void executeAfterMethods(List<Method> afterMethods, Object instance) {
        for (Method afterMethod : afterMethods) {
            try {
                logger.info("Executing @After " + afterMethod.getName());
                afterMethod.invoke(instance);
            } catch (InvocationTargetException e) {
                logger.log(
                        Level.SEVERE,
                        "Error in @After method" + afterMethod.getName() + " - " + e.getTargetException(),
                        e);
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, "After method" + afterMethod.getName() + " is not accessible", e);
            }
        }
    }

    private static void logSummary(List<Method> testMethods, int passed, int failed, List<String> failedTests) {
        logger.info(String.format("Total Tests: %d | Passed: %d ✅ | Failed: %d ❌", testMethods.size(), passed, failed));
        if (!failedTests.isEmpty()) {
            logger.info("Failed Tests: " + failedTests);
        }
    }
}
