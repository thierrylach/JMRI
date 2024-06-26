package jmri.jmrit.beantable.signalmast;

import jmri.util.JUnitUtil;

import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.Assert;

/**
 * Tests the overall operation of {@link SignalMastAddPane} services.
 * <p>
 * See {@link AbstractSignalMastAddPaneTestBase} for the base-class of the 
 * tests of individual implementations of {@link SignalMastAddPane} subclasses.
 * of 
 * @author Bob Jacobsen Copyright 2018
 */
public class SignalMastAddPaneTest {

    @Test
    public void testLoad() {
        // group these in a single test, as the services can only be loaded once.
        Assert.assertNotNull(SignalMastAddPane.SignalMastAddPaneProvider.getInstancesCollection());
        Assert.assertNotNull(SignalMastAddPane.SignalMastAddPaneProvider.getInstancesMap());
        Assert.assertFalse(SignalMastAddPane.SignalMastAddPaneProvider.getInstancesMap().isEmpty()); // found at least one service
        Assert.assertEquals(SignalMastAddPane.SignalMastAddPaneProvider.getInstancesMap().size(), SignalMastAddPane.SignalMastAddPaneProvider.getInstancesCollection().size()); // same size
        
        // check map is in sorted order; also check lookup works
        Map<String, SignalMastAddPane.SignalMastAddPaneProvider> map = SignalMastAddPane.SignalMastAddPaneProvider.getInstancesMap();
        Collection<SignalMastAddPane.SignalMastAddPaneProvider> collection = SignalMastAddPane.SignalMastAddPaneProvider.getInstancesCollection();
        String last = "";
        
        for ( Map.Entry<String,?> entry : map.entrySet() ) {
            String name = entry.getKey();
            Assert.assertTrue(name.compareTo(last) > 0);  // no identical ones
            Assert.assertTrue(collection.contains(map.get(name)));
            last = name;
        }

        // check collection in in sorted order
        last = "";
        for (SignalMastAddPane.SignalMastAddPaneProvider pane : collection) {
            Assert.assertTrue(pane.getPaneName().compareTo(last) > 0);  // no identical ones
            last = pane.getPaneName();
        }
        
        // partial check that results are unmodifiable
        try {
            SignalMastAddPane.SignalMastAddPaneProvider.getInstancesMap().put("Foo", null);
            Assert.fail("Should have thrown");
        } catch (java.lang.UnsupportedOperationException e) {
            // this is a pass
        }
        try {
            SignalMastAddPane.SignalMastAddPaneProvider.getInstancesCollection().add(null);
            Assert.fail("Should have thrown");
        } catch (java.lang.UnsupportedOperationException e) {
            // this is a pass
        }
        
        // check all the classes
        for (SignalMastAddPane.SignalMastAddPaneProvider pane : collection) {
        
                Assert.assertFalse( pane.getPaneName().isBlank());

               if (pane.isAvailable()) {
                   Assert.assertFalse(pane.getPaneName().isBlank());
                }
        }

    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.initDefaultUserMessagePreferences();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }
}
