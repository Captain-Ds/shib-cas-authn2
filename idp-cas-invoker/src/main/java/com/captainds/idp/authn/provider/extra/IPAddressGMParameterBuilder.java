package com.captainds.idp.authn.provider.extra;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import edu.internet2.middleware.shibboleth.idp.authn.LoginContext;
import edu.internet2.middleware.shibboleth.idp.authn.Saml2LoginContext;
import edu.internet2.middleware.shibboleth.idp.util.HttpServletHelper;

import net.unicon.idp.authn.provider.extra.IParameterBuilder;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.xml.XMLObject;

/**
 * Created by Sean Hill (sean@reax.io) on 11/10/14.
 */
public class IPAddressGMParameterBuilder implements IParameterBuilder {
    @Override
    public String getParameterString(HttpServletRequest request) {
        ServletContext servletContext = request.getSession().getServletContext();
        LoginContext loginContext = HttpServletHelper.getLoginContext(
                HttpServletHelper.getStorageService(servletContext), servletContext, request);

        String gmParameter;

        try {
            gmParameter = getGMParameter((Saml2LoginContext) loginContext);
        } catch(RuntimeException ex) {
            gmParameter = "";
        }

        return gmParameter;
    }

    private String getGMParameter(Saml2LoginContext loginContext) {
        try {
            AuthnRequest authnRequest = loginContext.getAuthenticiationRequestXmlObject();
            Extensions extensions = authnRequest.getExtensions();
            for(XMLObject element : extensions.getUnknownXMLObjects()) {
                if(element.getElementQName().getLocalPart() == "QueryString") {
                    return element.getDOM().getTextContent();
                }
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof IPAddressGMParameterBuilder;
    }
}
