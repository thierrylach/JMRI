package jmri.jmrit.display.configurexml;

import jmri.configurexml.JmriConfigureXmlException;
import jmri.jmrit.catalog.NamedIcon;
import jmri.jmrit.display.Editor;
import jmri.jmrit.display.LightIcon;
import jmri.jmrit.display.Positionable;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle configuration for display.LightIcon objects.
 *
 * @author Pete Cressman Copyright: Copyright (c) 2011
 */
public class LightIconXml extends PositionableLabelXml {

    public LightIconXml() {
    }

    /**
     * Default implementation for storing the contents of a LightIcon
     *
     * @param o Object to store, of type LightIcon
     * @return Element containing the complete info
     */
    @Override
    public Element store(Object o) {

        LightIcon p = (LightIcon) o;
        if (!p.isActive()) {
            return null;  // if flagged as inactive, don't store
        }
        Element element = new Element("LightIcon");
        element.setAttribute("light", p.getLight().getSystemName());
        storeCommonAttributes(p, element);

        Element elem = new Element("icons");
        elem.addContent(storeIcon("on", p.getOnIcon()));
        elem.addContent(storeIcon("off", p.getOffIcon()));
        elem.addContent(storeIcon("unknown", p.getUnknownIcon()));
        elem.addContent(storeIcon("inconsistent", p.getInconsistentIcon()));
        element.addContent(elem);

        storeLogixNG_Data(p, element);

        element.setAttribute("class", "jmri.jmrit.display.configurexml.LightIconXml");

        return element;
    }

    /**
     * Create a PositionableLabel, then add to a target JLayeredPane
     *
     * @param element Top level Element to unpack.
     * @param o       Editor as an Object
     * @throws JmriConfigureXmlException when a error prevents creating the objects as as
     *                   required by the input XML
     */
    @Override
    public void load(Element element, Object o) throws JmriConfigureXmlException {
        // create the objects
        Editor p = (Editor) o;

        LightIcon l = new LightIcon(p);

        String name;
        try {
            name = element.getAttribute("light").getValue();
        } catch (NullPointerException e) {
            log.error("incorrect information for light; must use light name");
            p.loadFailed();
            return;
        }
        l.setLight(name);

        Element icons = element.getChild("icons");
        if (icons == null) {
            if (log.isDebugEnabled()) {
                log.debug("Main element of Light {}has no icons", name);
            }
        } else {
            NamedIcon icon = loadIcon(l, "on", icons, "LightIcon \"" + name + "\": icon \"on\" ", p);
            if (icon != null) {
                l.setOnIcon(icon);
            } else {
                log.info("LightIcon \"{}\": icon \"on\" removed", name);
                return;
            }
            icon = loadIcon(l, "off", icons, "LightIcon \"" + name + "\": icon \"off\" ", p);
            if (icon != null) {
                l.setOffIcon(icon);
            } else {
                log.info("LightIcon \"{}\": icon \"off\" removed", name);
                return;
            }
            icon = loadIcon(l, "unknown", icons, "LightIcon \"" + name + "\": icon \"unknown\" ", p);
            if (icon != null) {
                l.setUnknownIcon(icon);
            } else {
                log.info("LightIcon \"{}\": icon \"unknown\" removed", name);
                return;
            }
            icon = loadIcon(l, "inconsistent", icons, "LightIcon \"" + name + "\": icon \"inconsistent\" ", p);
            if (icon != null) {
                l.setInconsistentIcon(icon);
            } else {
                log.info("LightIcon \"{}\": icon \"inconsistent\" removed", name);
                return;
            }
        }

        try {
            p.putItem(l);
        } catch (Positionable.DuplicateIdException e) {
            // This should never happen
            log.error("Editor.putItem() with null id has thrown DuplicateIdException", e);
        }

        loadLogixNG_Data(l, element);

        // load individual item's option settings after editor has set its global settings
        loadCommonAttributes(l, Editor.LIGHTS, element);
    }

    private final static Logger log = LoggerFactory.getLogger(LightIconXml.class);
}
