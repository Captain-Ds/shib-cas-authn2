package com.captainds.config;

/**
 * Created by Sean Hill (sean@reax.io) on 11/11/14.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import edu.internet2.middleware.shibboleth.common.config.attribute.resolver.dataConnector.BaseDataConnectorBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
/**
 * Spring Bean Definition Parser for CAS data connector.
 */
public class CASDataConnectorBeanDefinitionParser extends BaseDataConnectorBeanDefinitionParser {

    /** Schema type name. */
    public static final QName TYPE_NAME = new QName(DataConnectorNamespaceHandler.NAMESPACE, "CASExtraAttributes");

    /** {@inheritDoc} */
    protected Class getBeanClass(Element element) {
        return CASDataConnectorFactoryBean.class;
    }

    /** {@inheritDoc} */
    protected void doParse(String pluginId, Element pluginConfig, Map<QName, List<Element>> pluginConfigChildren,
                           BeanDefinitionBuilder pluginBuilder, ParserContext parserContext) {
        super.doParse(pluginId, pluginConfig, pluginConfigChildren, pluginBuilder, parserContext);
    }
}

