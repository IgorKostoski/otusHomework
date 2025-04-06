package org.example.hw03; // Correct package declaration

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.hw03.annotations.After;
import org.example.hw03.annotations.Before;
import org.example.hw03.annotations.Test;

/**
 * Runs test methods annotated with @Before, @Test, @After from a specified class.
 */
class TestRunner {
    private static final Logger logger = Logger.getLogger(TestRunner.class.getName());

    private static final String CONTEXT_BEFORE = "@Before";
    private static final String CONTEXT_TEST = "@Test";
    private static final String CONTEXT_AFTER = "@After";
    private static final String LOG_TEST_PREFIX = "Test ";

    private static final String MSG_ABORTING_RUN = "Aborting test run for class {0}";

    private TestRunner() {
        throw new UnsupportedOperationException("Utility class. Cannot be instantiated.");
    }

    /**
     * Entry point to run tests for a given class name. Assumes the class is in the same package
     * or provide the fully qualified name.
     *
     * @param className The simple or fully qualified name of the class containing test methods.
     */
    public static void run(String className) {
        Objects.requireNonNull(className, "Class name cannot be null");
        logger.log(Level.INFO, "Attempting to run tests for class: {0}", className);

        Class<?> clazz = null;
        try {

            clazz = Class.forName(className);

            List<Method> beforeMethods = findMethodsWithAnnotation(clazz, Before.class);
            List<Method> testMethods = findMethodsWithAnnotation(clazz, Test.class);
            List<Method> afterMethods = findMethodsWithAnnotation(clazz, After.class);

            if (testMethods.isEmpty()) {
                logger.log(Level.WARNING, "No @Test methods found in class {0}. Aborting.", className);
                return;
            }

            TestRunResult result = executeTestMethods(clazz, testMethods, beforeMethods, afterMethods);
            logSummary(testMethods.size(), result.passedCount, result.failedCount, result.failedTestDetails);

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, String.format("Test class not found: %s", className), e);
            logger.log(Level.SEVERE, MSG_ABORTING_RUN, className); // Use constant
        } catch (LinkageError e) {
            logger.log(
                    Level.SEVERE,
                    String.format("Failed to link or initialize test class %s or its dependencies", className),
                    e);
            logger.log(Level.SEVERE, MSG_ABORTING_RUN, className); // Use constant
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Unexpected error during setup for class %s", className), e);
            logger.log(Level.SEVERE, MSG_ABORTING_RUN, className); // Use constant
        }
    }

    /**
     * Executes all test methods found in the class and aggregates results.
     */
    private static TestRunResult executeTestMethods(
            Class<?> clazz, List<Method> testMethods, List<Method> beforeMethods, List<Method> afterMethods) {

        TestRunResult result = new TestRunResult();
        logger.log(Level.INFO, "Found {0} @Test methods. Starting execution...", testMethods.size());

        for (Method testMethod : testMethods) {
            Object instance = instantiateTestClass(clazz, testMethod.getName());
            if (instance == null) {
                result.failedCount++;
                result.failedTestDetails.add(testMethod.getName() + " (FAILED - Instantiation Error)");
                continue;
            }

            boolean testPassed = executeSingleTestLifecycle(instance, testMethod, beforeMethods, afterMethods);

            if (testPassed) {
                result.passedCount++;
            } else {
                result.failedCount++;
                result.failedTestDetails.add(testMethod.getName() + " (FAILED)");
            }
        }
        return result;
    }

    /**
     * Executes the @Before, @Test, and @After methods for a single test instance.
     * @return true if the @Before methods succeeded AND the @Test method succeeded, false otherwise.
     */
    private static boolean executeSingleTestLifecycle(
            Object instance, Method testMethod, List<Method> beforeMethods, List<Method> afterMethods) {
        boolean setupSuccessful = true;
        boolean testSuccessful = false; // Initial state
        logger.log(Level.INFO, "=== Starting Test Cycle: {0} ===", testMethod.getName());

        try {
            for (Method beforeMethod : beforeMethods) {
                if (!invokeMethod(beforeMethod, instance, CONTEXT_BEFORE)) {
                    setupSuccessful = false;
                    break; // Stop @Before execution on first failure
                }
            }

            if (setupSuccessful) {
                testSuccessful = invokeMethod(testMethod, instance, CONTEXT_TEST);
            } else {
                logger.log(Level.WARNING, "Skipping test {0} due to @Before failure ⚠️", testMethod.getName());
            }

        } catch (Exception e) {

            logger.log(
                    Level.SEVERE,
                    String.format("Unexpected internal error during execution phase of test %s", testMethod.getName()),
                    e);
            setupSuccessful = false;

        } finally {

            for (Method afterMethod : afterMethods) {

                invokeMethod(afterMethod, instance, CONTEXT_AFTER);
            }
        }

        boolean overallSuccess = setupSuccessful && testSuccessful;
        logTestFinish(testMethod.getName(), overallSuccess);
        return overallSuccess;
    }

    /**
     * Invokes a single method using reflection. Logs details and specific exceptions.
     * @return true if successful, false if an exception occurred.
     */
    private static boolean invokeMethod(Method method, Object instance, String context) {
        logger.log(Level.INFO, "Executing {0} method: {1}", new Object[] {context, method.getName()});
        try {

            method.invoke(instance);
            return true;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            logger.log(
                    Level.SEVERE, String.format("Exception thrown by %s method %s", context, method.getName()), cause);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, String.format("%s method %s is not accessible", context, method.getName()), e);
        } catch (IllegalArgumentException e) {
            logger.log(
                    Level.SEVERE,
                    String.format(
                            "Illegal argument invoking %s method %s (should have no args)", context, method.getName()),
                    e);
        } catch (Exception e) {
            logger.log(
                    Level.SEVERE,
                    String.format("Unexpected reflection error invoking %s method %s", context, method.getName()),
                    e);
        }
        return false;
    }

    private static Object instantiateTestClass(Class<?> testClass, String testMethodName) {
        try {

            var constructor = testClass.getDeclaredConstructor();

            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            logger.log(
                    Level.SEVERE,
                    String.format("No default constructor found for test class %s", testClass.getName()),
                    e);
        } catch (InstantiationException e) {
            logger.log(
                    Level.SEVERE, String.format("Cannot instantiate abstract test class %s", testClass.getName()), e);
        } catch (IllegalAccessException e) {
            logger.log(
                    Level.SEVERE,
                    String.format("Default constructor not accessible for test class %s", testClass.getName()),
                    e);
        } catch (InvocationTargetException e) {
            logger.log(
                    Level.SEVERE,
                    String.format("Constructor threw an exception for test class %s", testClass.getName()),
                    e.getTargetException());
        } catch (Exception e) {
            logger.log(
                    Level.SEVERE,
                    String.format(
                            "Unexpected error instantiating test class %s for %s%s",
                            testClass.getName(), LOG_TEST_PREFIX, testMethodName),
                    e);
        }
        return null;
    }

    private static List<Method> findMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Method> annotatedMethods = new ArrayList<>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    private static void logTestFinish(String testMethodName, boolean success) {
        logger.log(Level.INFO, "=== Finished {0}{1} | Status: {2} ===", new Object[] {
            LOG_TEST_PREFIX, testMethodName, success ? "PASSED ✅" : "FAILED ❌"
        });
    }

    private static void logSummary(int total, int passed, int failed, List<String> failedTestDetails) {
        logger.log(Level.INFO, "==================== Test Summary ====================");
        logger.log(Level.INFO, "Total Tests Run: {0}", total);
        logger.log(Level.INFO, "Passed: {0} ✅", passed);
        logger.log(Level.INFO, "Failed: {0} ❌", failed);
        if (!failedTestDetails.isEmpty()) {
            logger.log(Level.INFO, "Failed Test Details: {0}", failedTestDetails);
        }
        logger.log(Level.INFO, "====================================================");
    }

    /** Helper class to hold results from executeTestMethods */
    private static class TestRunResult {
        int passedCount = 0;
        int failedCount = 0;
        List<String> failedTestDetails = new ArrayList<>();
    }
}
