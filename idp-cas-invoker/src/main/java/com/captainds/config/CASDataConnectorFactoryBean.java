package com.captainds.config;

/**
 * Created by Sean Hill (sean@reax.io) on 11/11/14.
 */

import com.captainds.attributes.resolver.provider.dataConnector.CASDataConnector;
import edu.internet2.middleware.shibboleth.common.config.attribute.resolver.dataConnector.BaseDataConnectorFactoryBean;

public class CASDataConnectorFactoryBean extends BaseDataConnectorFactoryBean {

    /** {@inheritDoc} */
    public Class getObjectType() {
        return CASDataConnector.class;
    }

    /** {@inheritDoc} */
    protected Object createInstance() throws Exception {
        CASDataConnector connector = new CASDataConnector();
        populateDataConnector(connector);

        return connector;
    }
}

