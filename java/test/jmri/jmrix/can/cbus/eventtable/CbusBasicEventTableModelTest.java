package jmri.jmrix.can.cbus.eventtable;

import static org.assertj.core.api.Assertions.assertThat;

import jmri.util.JUnitUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 * @author Steve Young Copyright (C) 2020
 */
public class CbusBasicEventTableModelTest {

    @Test
    public void testCTor() {
        
        CbusBasicEventTableModel t = new CbusBasicEventTableModel(null);
        assertThat(t).isNotNull();
        
    }
    
    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(CbusBasicEventTableModelTest.class);

}
