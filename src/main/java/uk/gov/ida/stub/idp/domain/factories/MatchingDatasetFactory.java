package uk.gov.ida.stub.idp.domain.factories;

import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.ArrayList;
import java.util.List;



public final class MatchingDatasetFactory {

    private MatchingDatasetFactory() {}

    public static MatchingDataset create(final IdpUser user) {
         return new MatchingDataset(user.getFirstnames(), user.getMiddleNames(), user.getSurnames(), user.getGender(), user.getDateOfBirths(), getCurrentAddresses(user.getAddresses()), getPreviousAddresses(user.getAddresses()));
    }

    private static List<Address> getPreviousAddresses(List<Address> addresses) {
        List<Address> previousAddresses = new ArrayList<>();
        for(Address address: addresses) {
            if(address.getTo().isPresent()) {
                previousAddresses.add(address);
            }
        }
        return previousAddresses;
    }

    private static List<Address> getCurrentAddresses(List<Address> addresses) {
        List<Address> currentAddresses = new ArrayList<>();
        for(Address address: addresses) {
            if(!address.getTo().isPresent()) {
                currentAddresses.add(address);
            }
        }
        return currentAddresses;
    }
}
