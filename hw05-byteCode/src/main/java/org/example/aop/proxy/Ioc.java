package org.example.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Ioc {

    private Ioc() {}

    static MyClassInterface createMyClass() {
        MyClassImpl original = new MyClassImpl();
        InvocationHandler handler = new DemoInvocationHandler(original);
        return (MyClassInterface)
                Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {MyClassInterface.class}, handler);
    }

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
            Method implementationMethod = null;
            try {
                implementationMethod = originalClass.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                logger.warn(
                        "Could not find implementation method for: {}, annotation check skipped", method.getName(), e);

                throw new ProxySetupException(
                        "Proxy setup error: Method not found on implementation class " + originalClass.getName() + ": "
                                + method.getName(),
                        e);
            }

            if (implementationMethod != null && implementationMethod.isAnnotationPresent(Log.class)) {
                String paramsLog;
                if (args == null || args.length == 0) {
                    paramsLog = "no params";
                } else {
                    paramsLog = "param(s): "
                            + Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(", "));
                }
                logger.info("executed method: {}, {}", method.getName(), paramsLog);
            }
            return method.invoke(originalObject, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + "originalObject=" + originalObject + '}';
        }
    }
}
