package jmri.jmrit.vsdecoder;

import jmri.util.startup.AbstractStartupActionFactory;
import jmri.util.startup.StartupActionFactory;
import java.util.Locale;
import org.openide.util.lookup.ServiceProvider;

/**
 * {@link jmri.util.startup.StartupActionFactory} for the
 * {@link jmri.jmrit.vsdecoder.VSDecoderCreationAction}.
 *
 * @author Randall Wood Copyright (C) 2016
 */
@ServiceProvider(service = StartupActionFactory.class)
public final class VSDecoderCreationStartupActionFactory extends AbstractStartupActionFactory {

    @Override
    public String getTitle(Class<?> clazz, Locale locale) throws IllegalArgumentException {
        if (clazz.equals(VSDecoderCreationAction.class)) {
            return Bundle.getMessage(locale, "VSDStartupActionText"); // NOI18N
        }
        throw new IllegalArgumentException(clazz.getName() + " is not supported by " + this.getClass().getName());
    }

    @Override
    public Class<?>[] getActionClasses() {
        return new Class[]{VSDecoderCreationAction.class};
    }

}
