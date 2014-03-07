package net.unicon.idp.authn.provider;

import edu.internet2.middleware.shibboleth.idp.config.profile.authn.AbstractLoginHandlerFactoryBean;

public class CasLoginHandlerFactoryBean extends AbstractLoginHandlerFactoryBean {
    private String propertiesFile;
    private String paramBuilderNames;

    @Override
    protected Object createInstance() throws Exception {
        CasLoginHandler handler = new CasLoginHandler(propertiesFile, paramBuilderNames);
        populateHandler(handler);
        return handler;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getObjectType() {
        return CasLoginHandler.class;
    }

    public void setPropertiesFile(final String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public void setParamBuilderNames(final String paramBuilderNames) {
        this.paramBuilderNames = paramBuilderNames;
    }
}
