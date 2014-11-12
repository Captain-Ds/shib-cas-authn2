package com.captainds.attributes.resolver.provider.dataConnector;

/**
 * Created by Sean Hill (sean@reax.io) on 11/11/14.
 */

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.captainds.idp.authn.provider.CASPrincipal;
import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.provider.BasicAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.dataConnector.BaseDataConnector;
import edu.internet2.middleware.shibboleth.common.session.Session;
import javax.security.auth.Subject;

/** Data connector implementation that returns attributes found on the CASPrincipal object. */
public class CASDataConnector extends BaseDataConnector {
    /**
     * Constructor.
     *
     */
    public CASDataConnector() {
    }

    /** {@inheritDoc} */
    public Map<String, BaseAttribute> resolve(ShibbolethResolutionContext resolutionContext)
            throws AttributeResolutionException {
        Session userSession = resolutionContext.getAttributeRequestContext().getUserSession();
        Subject subject = userSession.getSubject();
        HashMap<String, BaseAttribute> attributes = new HashMap<>();

        Set<Principal> principals = subject.getPrincipals();

        for(Principal principal : principals) {
            if(principal instanceof CASPrincipal) {
                CASPrincipal casPrincipal = (CASPrincipal)principal;
                Map<String, String> extraAttributes = casPrincipal.getExtraAttributes();

                for(String key : extraAttributes.keySet()) {
                    BasicAttribute<String> attribute = new BasicAttribute<>(key);
                    attribute.getValues().add(extraAttributes.get(key));

                    attributes.put(key, attribute);
                }
            }
        }

        return attributes;
    }

    /** {@inheritDoc} */
    public void validate() throws AttributeResolutionException {
        // Do nothing
    }
}
