package jmri.server.json.signalmast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import jmri.util.JUnitUtil;

import org.junit.jupiter.api.*;

/**
 *
 * @author Randall Wood Copyright 2018
 */
public class JsonSignalMastTest {

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    @Test
    public void testConstructor() throws Exception {
        try {
            Constructor<JsonSignalMast> constructor;
            constructor = JsonSignalMast.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            Assertions.fail("Instance of JsonSignalMast created");
        } catch (InvocationTargetException ex) {
            // because the constructor throws UnsupportedOperationException, and
            // that is thrown by newInstance() into an InvocationTargetException
            // we pass an InvocationTargetException that is caused by an
            // UnsupportedOperationException and fail everything else by
            // rethrowing the unexepected exception to get a stack trace
            var cause = ex.getCause();
            Assertions.assertNotNull(cause);
            if (!cause.getClass().equals(UnsupportedOperationException.class)) {
                throw ex;
            }
        }
    }

}
