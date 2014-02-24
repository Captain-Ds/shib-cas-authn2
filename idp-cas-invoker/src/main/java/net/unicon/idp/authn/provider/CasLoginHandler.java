package net.unicon.idp.authn.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.shibboleth.idp.authn.LoginContext;
import edu.internet2.middleware.shibboleth.idp.authn.provider.AbstractLoginHandler;
import edu.internet2.middleware.shibboleth.idp.authn.provider.ExternalAuthnSystemLoginHandler;
import edu.internet2.middleware.shibboleth.idp.util.HttpServletHelper;

/**
 * CasLoginHandler replaces the {@link CasInvokerServlet} AND {@link CasAuthenticatorResource} (facade) from the v1.x implementations. 
 * Allows simplification of the SHIB-CAS authenticator by removing the need to configure and deploy a separate war.
 * 
 * This LoginHandler handles taking the login request from Shib and translating and sending the request on to the CAS instance.
 * @author chasegawa@unicon.net
 */
public class CasLoginHandler extends AbstractLoginHandler {
    private String callbackUrl;
    private String casLoginUrl;
    private Logger logger = LoggerFactory.getLogger(CasLoginHandler.class);
    private String casProtocol = "https";
    private String casPrefix = "/cas";
    private String casServer;
    private String casLogin = "/login";
    private String idpProtocol = "https";
    private String idpServer;
    private String idpPrefix = "/idp";
    private String idpCallback = "/Authn/Cas";

    /**
     * Create a new instance of the login handler. Read the configuration properties from the properties file indicated as 
     * a construction argument. 
     * @param propertiesFile File and path name to the file containing the required properties: 
     * <li>cas.server
     * <li>idp.server
     */
    public CasLoginHandler(final String propertiesFile) {
        Properties props = new Properties();
        try {
            try {
                FileReader reader = new FileReader(new File(propertiesFile));
                props.load(reader);
                reader.close();
            } catch (final FileNotFoundException e) {
                logger.debug("Unable to locate properties file: {}", propertiesFile);
                throw e;
            } catch (final IOException e) {
                logger.debug("Error reading properties file: {}", propertiesFile);
                throw e;
            }
            String temp = getProperty(props, "cas.server.protocol");
            casProtocol = StringUtils.isEmpty(temp) ? casProtocol : temp;
            temp = getProperty(props, "cas.application.prefix");
            casPrefix = StringUtils.isEmpty(temp) ? casPrefix : temp;
            temp = getProperty(props, "cas.server");
            casServer = StringUtils.isEmpty(temp) ? casServer : temp;
            temp = getProperty(props, "cas.server.login");
            casLogin = StringUtils.isEmpty(temp) ? casLogin : temp;
            casLoginUrl = casProtocol + "://" + casServer + casPrefix + casLogin;

            temp = getProperty(props, "idp.server.protocol");
            idpProtocol = StringUtils.isEmpty(temp) ? idpProtocol : temp;
            temp = getProperty(props, "idp.server");
            idpServer = StringUtils.isEmpty(temp) ? idpServer : temp;
            temp = getProperty(props, "idp.application.prefix");
            idpPrefix = StringUtils.isEmpty(temp) ? idpPrefix : temp;
            temp = getProperty(props, "idp.server.callback");
            idpCallback = StringUtils.isEmpty(temp) ? idpCallback : temp;
            callbackUrl = idpProtocol + "://" + idpServer + idpPrefix + idpCallback;
        } catch (final Exception e) {
            logger.error("Unable to load parameters", e);
            throw new RuntimeException(e);
        }

        if (null == casLoginUrl || "".equals(casServer.trim())) {
            logger.error("Unable to create CasLoginHandler - missing cas.server property. Please check {}",
                    propertiesFile);
            throw new IllegalArgumentException(
                    "CasLoginHandler missing properties needed to build the cas login URL in handler configuration.");
        }
        if (null == idpServer || "".equals(idpServer.trim())) {
            logger.error("Unable to create CasLoginHandler - missing idp.server property. Please check {}",
                    propertiesFile);
            throw new IllegalArgumentException(
                    "CasLoginHandler missing properties needed to build the callback URL in handler configuration.");
        }
    }

    /**
     * @return the property value or empty string if the key/value isn't found
     */
    private String getProperty(final Properties props, final String key) {
        String result = props.getProperty(key);
        return StringUtils.isEmpty(result) ? "" : result;
    }

    /**
     * Translate the SHIB request so that cas renew and/or gateway are set properly before handing off to CAS.
     * @see edu.internet2.middleware.shibboleth.idp.authn.LoginHandler#login(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void login(final HttpServletRequest request, final HttpServletResponse response) {
        Boolean force = (Boolean) request.getAttribute(ExternalAuthnSystemLoginHandler.FORCE_AUTHN_PARAM);
        force = (null == force) ? Boolean.FALSE : force;
        setSupportsForceAuthentication(force);

        // CAS Protocol - http://www.jasig.org/cas/protocol recommends that when this param is set, to set "true"
        String authnType = (force) ? "&renew=true" : "";

        Boolean passive = (Boolean) request.getAttribute(ExternalAuthnSystemLoginHandler.PASSIVE_AUTHN_PARAM);
        if (null != passive) {
            setSupportsPassive(passive);

            // CAS Protocol - http://www.jasig.org/cas/protocol indicates not setting gateway if renew has been set.
            if (passive && "".equals(authnType)) {
                authnType += "&gateway=true";
            }
        }
        try {
            HttpSession session = request.getSession();
            ServletContext servletContext = session.getServletContext();
            session.setAttribute("authnType", authnType);
            LoginContext loginContext = HttpServletHelper.getLoginContext(
                    HttpServletHelper.getStorageService(servletContext), servletContext, request);
            String relayingPartyId = loginContext.getRelyingPartyId();

            // CAS protocol doesn't currently handle entityId, but by supplying it now, we are hoping to be able to use that
            // in the next generation of CAS
            response.sendRedirect(response.encodeRedirectURL(casLoginUrl + "?service=" + callbackUrl + authnType
                    + "&entityId=" + relayingPartyId));
        } catch (final IOException e) {
            logger.error("Unable to redirect to CAS from LoginHandler", e);
        }
    }
}
