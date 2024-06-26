package jmri.implementation;

import jmri.util.JUnitUtil;
import jmri.IdTag;
import jmri.InstanceManager;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.*;
/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class AbstractRailComReporterTest extends AbstractReporterTestBase {

    @Override
    protected Object generateObjectToReport(){
        return new DefaultRailCom("ID1234", "Test Tag");
    }

    // AbstractRailComReporter implements the IdTagListener interface, which
    // includes a notify(IdTag) method.
    @Test
    public void testNotify() {
        Assume.assumeTrue(r instanceof AbstractRailComReporter);
        Assume.assumeTrue(generateObjectToReport() instanceof IdTag);
        Assert.assertEquals("IdTag not Seen",IdTag.UNKNOWN,r.getState());
        ((AbstractRailComReporter)r).notify((IdTag)generateObjectToReport());
        // Check that both CurrentReport and LastReport are not null
        Assert.assertNotNull("CurrentReport Object exists", r.getCurrentReport());
        Assert.assertNotNull("LastReport Object exists", r.getLastReport());
        // Check the value of both CurrentReport and LastReport
        Assert.assertEquals("CurrentReport equals LastReport",r.getLastReport(), r.getCurrentReport());
        Assert.assertEquals("IdTag Seen",IdTag.SEEN,r.getState());

        // send a null report.

        ((AbstractRailComReporter)r).notify(null);
        // Check that both CurrentReport and LastReport are not null
        Assert.assertNull("CurrentReport Object Null", r.getCurrentReport());
        Assert.assertNotNull("LastReport Object exists", r.getLastReport());
        Assert.assertEquals("IdTag Seen",IdTag.UNSEEN,r.getState());
    }

    @BeforeEach
    @Override
    public void setUp() {
        JUnitUtil.setUp();
        r = new AbstractRailComReporter("IR1");
    }

    @AfterEach
    @Override
    public void tearDown() {
        r = null;
        if ( InstanceManager.getNullableDefault(jmri.IdTagManager.class) != null ) {
            InstanceManager.getDefault(jmri.IdTagManager.class).dispose();
        }
        // JUnitUtil.clearShutDownManager(); // would be better to check and clean up specifics in tests
        JUnitUtil.tearDown();
    }

}
