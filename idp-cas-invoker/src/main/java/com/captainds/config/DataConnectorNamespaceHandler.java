package com.captainds.config;

/**
 * Created by Sean Hill (sean@reax.io) on 11/11/14.
 */
import edu.internet2.middleware.shibboleth.common.config.BaseSpringNamespaceHandler;

public class DataConnectorNamespaceHandler extends BaseSpringNamespaceHandler {
    /** Namespace URI. */
    public static final String NAMESPACE = "http://captainds.com/shib-cas/dc";

    /**
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
        registerBeanDefinitionParser(CASDataConnectorBeanDefinitionParser.TYPE_NAME, new CASDataConnectorBeanDefinitionParser());
    }
}

