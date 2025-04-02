import annotations.After;
import annotations.Before;
import annotations.Test;
import java.util.logging.Logger;

public class MyTest {
    private static final Logger logger = Logger.getLogger(MyTest.class.getName());

    private String message;

    @Before
    void setup() {
        message = "Test setup";
        logger.info("Before method called: " + message);
    }

    @Test
    public void test1() {
        System.out.println("Running testCase 1 with message: " + message);
        if (!message.equals("Test setup")) {
            logger.info("Message was not set correctly");
            throw new RuntimeException("Message was not set correctly");
        }
        logger.info("testCase 1 passed");
    }

    @Test
    public void test2() {
        System.out.println("Running testCase2 with message: " + message);
        if (!message.equals("Test setup")) {
            logger.severe("Message was not set correctly");
            throw new RuntimeException("Message was not setup correctly");
        }
        logger.info("testCase 2 passed");
    }

    @Test
    public void testCase3ThatFails() {
        logger.info("Running testCase3 that fails");
        logger.severe("This test case is designed to fail");
        throw new RuntimeException("This test is supposed to fail");
    }

    @After
    public void tearDown() {
        logger.info("After test method called");
    }
}
