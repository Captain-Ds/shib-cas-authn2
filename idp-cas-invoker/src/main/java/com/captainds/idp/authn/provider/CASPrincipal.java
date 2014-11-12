package com.captainds.idp.authn.provider;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.xml.util.DatatypeHelper;

/**
 * Created by Sean Hill (sean@reax.io) on 11/11/14.
 */

/** A basic implementation of {@link Principal}. */
public class CASPrincipal implements Principal, Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 4718975451896240622L;

    /** Name of the principal. */
    private String name;


    private Map<String, String> extraAttributes;

    /**
     * Constructor.
     *
     * @param principalName name of the principal
     */
    public CASPrincipal(String principalName, String uid, String accountDirectory) {
        name = DatatypeHelper.safeTrimOrNullString(principalName);
        extraAttributes = new HashMap<>();

        extraAttributes.put("uid", uid);
        extraAttributes.put("accountDirectory", accountDirectory);

        if(name == null){
            throw new IllegalArgumentException("principal name may not be null or empty");
        }
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public String toString() {
        return "{BasicPrincipal}" + getName();
    }

    public Map<String, String> getExtraAttributes() {
        return extraAttributes;
    }

    public String getExtraAttribute(String key) {
        return extraAttributes.get(key);
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return name.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof CASPrincipal) {
            return DatatypeHelper.safeEquals(getName(), ((CASPrincipal) obj).getName());
        }

        return false;
    }
}
