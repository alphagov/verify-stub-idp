package uk.gov.ida.saml.core.domain;

import java.io.Serializable;

public class PersistentId implements Serializable {

    private String nameId;

    public PersistentId(String nameId) {
        this.nameId = nameId;
    }

    public String getNameId() {
        return nameId;
    }
}
