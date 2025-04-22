package org.example.aop.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Keep the outer final class definition
final class Ioc {

    // Private constructor for utility class
    private Ioc() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Custom exception for errors during proxy setup or invocation related to missing methods
    public static class ProxySetupException extends RuntimeException {
        public ProxySetupException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static MyClassInterface createMyClass() {
        MyClassImpl original = new MyClassImpl(); // Assuming MyClassImpl is defined correctly
        InvocationHandler handler = new DemoInvocationHandler(original);
        return (MyClassInterface)
                Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {MyClassInterface.class}, handler);
    }

    // Keep the static nested class for the handler
    static class DemoInvocationHandler implements InvocationHandler {
        private static final Logger logger = LoggerFactory.getLogger(DemoInvocationHandler.class);
        private final Object originalObject;
        private final Class<?> originalClass;

        DemoInvocationHandler(Object originalObject) {
            this.originalObject = originalObject;
            this.originalClass = originalObject.getClass();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Optimization: Check if the method is from Object class (like toString, hashCode)
            if (method.getDeclaringClass() == Object.class) {
                // Example: Handle toString specifically if desired
                if ("toString".equals(method.getName())) {
                    return "Proxy for " + originalObject.toString();
                }
                // Otherwise, invoke directly on the original object without logging
                return method.invoke(originalObject, args);
            }

            Method implementationMethod = null;
            try {
                // Find the corresponding method in the *implementation* class
                implementationMethod = originalClass.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new ProxySetupException(
                        "Proxy setup error: Method '" + method.getName() + "' not found on implementation class "
                                + originalClass.getName(),
                        e);
            }

            // Check annotation only if method was found and is annotated
            // Ensure implementationMethod isn't null (though catch block should prevent this path)
            if (implementationMethod != null && implementationMethod.isAnnotationPresent(Log.class)) {
                logMethodExecution(method, args);
            }

            // Invoke the original method on the actual implementation object
            return method.invoke(originalObject, args);
        }

        // FIX for S3358: Extracted nested ternary into multi-line lambda
        private void logMethodExecution(Method method, Object[] args) {
            String paramsLog;
            if (args == null || args.length == 0) {
                paramsLog = "without params";
            } else {
                paramsLog = "with param(s): "
                        + Arrays.stream(args)
                                .map(
                                        arg -> { // Start of multi-line lambda
                                            if (arg == null) {
                                                return "null";
                                            } else {
                                                return String.valueOf(arg);
                                            }
                                        }) // End of multi-line lambda
                                .collect(Collectors.joining(", "));
            }

            logger.info("Executing logged method: {}({});", method.getName(), paramsLog); // Adjusted log format
        }

        @Override
        public String toString() {
            // Delegate to original object's toString or provide custom proxy representation
            return "DemoInvocationHandler{" + "proxiedObject=" + originalObject + '}';
        }
    }

    // --- Example Placeholders (Should be properly defined elsewhere) ---

    interface MyClassInterface {
        void calculation(int param1);

        void calculation(int param1, int param2);

        void calculation(int param1, int param2, String param3);

        void secureAccess(String param);
    }

    static class MyClassImpl implements MyClassInterface {
        private static final Logger logger = LoggerFactory.getLogger(MyClassImpl.class);

        @Log
        @Override
        public void calculation(int param1) {
            logger.info("calculation(int): {}", param1);
        }

        @Log
        @Override
        public void calculation(int param1, int param2) {
            logger.info("calculation(int, int): {}, {}", param1, param2);
        }

        @Override
        public void calculation(int param1, int param2, String param3) {
            logger.info("calculation(int, int, String): {}, {}, {}", param1, param2, param3);
        }

        @Override
        public void secureAccess(String param) {
            logger.info("secureAccess called with param: {}", param);
        }
    }

    // Log annotation definition
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Log {}
}
