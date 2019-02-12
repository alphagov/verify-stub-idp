package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.Address;

public class AddressMarshaller extends BaseMdsSamlObjectMarshaller {
    public AddressMarshaller(){
        super(Address.TYPE_LOCAL_NAME);
    }
}
