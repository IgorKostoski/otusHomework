package org.example.aop.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClassImpl implements MyClassInterface {
    private static final Logger logger = LoggerFactory.getLogger(MyClassImpl.class);

    @Log
    @Override
    public void secureAccess(String param) {
        logger.info("secureAccess (actual method execution), param:{}", param);
    }

    public void anotherMethod(int count) {
        logger.info("anotherMethod called with count: {}", count);
    }

    @Override
    public String toString() {
        return "MyClassImpl{}";
    }
}
