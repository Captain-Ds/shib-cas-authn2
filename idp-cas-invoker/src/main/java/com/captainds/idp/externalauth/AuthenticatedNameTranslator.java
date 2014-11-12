package com.captainds.idp.externalauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.captainds.idp.authn.provider.CASPrincipal;
import net.unicon.idp.externalauth.CasToShibTranslator;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jasig.cas.client.validation.Assertion;

import edu.internet2.middleware.shibboleth.idp.authn.LoginHandler;

import java.util.Map;
import java.util.Objects;


/**
 * Created by Sean Hill (sean@reax.io) on 11/10/14.
 */

public class AuthenticatedNameTranslator implements CasToShibTranslator {
    /**
     * @see net.unicon.idp.externalauth.CasToShibTranslator#doTranslation(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.jasig.cas.client.validation.Assertion)
     */
    public void doTranslation(final HttpServletRequest request, final HttpServletResponse response,
                              final Assertion assertion) {
        String authenticatedPrincipalName = assertion.getPrincipal().getName(); // i.e. username from CAS
        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
        String uid = Objects.toString(attributes.get("uuid"), "");
        String accountDirectory = Objects.toString(attributes.get("account_directory"), "");
        // Pass authenticated principal back to IdP to finish its part of authentication request processing
        CASPrincipal principal = new CASPrincipal(authenticatedPrincipalName, uid, accountDirectory);
        request.setAttribute(LoginHandler.PRINCIPAL_NAME_KEY, authenticatedPrincipalName);
        request.setAttribute(LoginHandler.PRINCIPAL_KEY, principal);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }
}

