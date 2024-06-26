package jmri.jmrix.ztc.ztc611.configurexml;

import org.jdom2.Element;

/**
 * Provides load and store functionality for configuring XNetTurnoutManagers
 * and ZTC611 in particular.
 * <p>
 * Uses the store method from the abstract base class, but provides a load
 * method here.
 *
 * @author Paul Bender Copyright: Copyright (c) 2008
 */
public class ZTC611XNetTurnoutManagerXml extends jmri.managers.configurexml.AbstractTurnoutManagerConfigXML {

    public ZTC611XNetTurnoutManagerXml() {
        super();
    }

    @Override
    public void setStoreElementClass(Element turnouts) {
        turnouts.setAttribute("class", "jmri.jmrix.lenz.configurexml.XNetTurnoutManagerXml");
    }

    @Override
    public boolean load(Element shared, Element perNode) {
        // load individual turnouts
        return loadTurnouts(shared, perNode);
    }

//    private final static Logger log = LoggerFactory.getLogger(ZTC611XNetTurnoutManagerXml.class);

}
