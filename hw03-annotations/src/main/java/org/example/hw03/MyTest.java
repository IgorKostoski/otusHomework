package org.example.hw03;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.hw03.annotations.After;
import org.example.hw03.annotations.Before;
import org.example.hw03.annotations.Test;

class MyTest {
    private static final Logger logger = Logger.getLogger(MyTest.class.getName());

    private static final String MSG_SETUP = "Test setup";
    private static final String ERR_MSG_NOT_SET = "Message was not set correctly";

    private String message = "";

    @Before
    void setup() {
        this.message = MSG_SETUP;

        logger.log(Level.INFO, MSG_SETUP);
    }

    @Test
    void test1() {

        logger.log(Level.INFO, "Test 1 message: {0}", this.message);
    }

    @Test
    void test2() {
        if (!MSG_SETUP.equals(this.message)) {
            throw new IllegalStateException(ERR_MSG_NOT_SET);
        }
        logger.log(Level.INFO, "Test 2 message: {0}", this.message);
    }

    @Test
    void test3() {
        logger.log(Level.INFO, "Always pass test");
    }

    @After
    void tearDown() {
        if (!MSG_SETUP.equals(this.message)) {
            throw new AssertionError("State after test is incorrect: " + this.message);
        }
        logger.log(Level.INFO, "Teardown check OK.");
    }

    @After
    void tearDown2() {
        throw new IllegalStateException("Tear down failed intentionally");
    }
}
