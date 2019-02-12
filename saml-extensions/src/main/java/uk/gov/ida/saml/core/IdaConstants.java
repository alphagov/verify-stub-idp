package uk.gov.ida.saml.core;

public interface IdaConstants {

    String EIDAS_NS = "http://eidas.europa.eu/saml-extensions";
    String EIDAS_NATURAL_PERSON_NS = "http://eidas.europa.eu/attributes/naturalperson";
    String EIDAS_PREFIX = "eidas";
    String EIDAS_NATURUAL_PREFIX = EIDAS_PREFIX + "-natural";
    String IDA_NS = "http://www.cabinetoffice.gov.uk/resource-library/ida/attributes";
    String IDA_PREFIX = "ida";
    String IDA_MD_NS = "urn:uk:gov:cabinet-office:tc:saml:metadata:extensions";
    String IDA_MD_PREFIX = "ext";
    String SAML_VERSION_NUMBER = "2.0";
    String DATETIME_FORMAT = "yyyy-MM-dd";
    String IDA_LANGUAGE = "en-GB";

    interface Eidas_Attributes {

        interface FirstName {
            String FRIENDLY_NAME = "FirstName";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName";
        }

        interface FamilyName {
            String FRIENDLY_NAME = "FamilyName";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName";
        }

        interface DateOfBirth {
            String FRIENDLY_NAME = "DateOfBirth";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/DateOfBirth";
        }

        interface PersonIdentifier {
            String FRIENDLY_NAME = "PersonIdentifier";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier";
        }

        interface CurrentAddress {
            String FRIENDLY_NAME = "CurrentAddress";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/CurrentAddress";
        }

        interface Gender {
            String FRIENDLY_NAME = "Gender";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/Gender";
        }

        interface BirthName {
            String FRIENDLY_NAME = "BirthName";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/BirthName";
        }

        interface PlaceOfBirth {
            String FRIENDLY_NAME = "PlaceOfBirth";
            String NAME = "http://eidas.europa.eu/attributes/naturalperson/PlaceOfBirth";
        }
    }

    interface Attributes_1_1 {

        interface IdpFraudEventId {
            String FRIENDLY_NAME = "IDPFraudEventID";
            String NAME = "FECI_IDPFraudEventID";
        }

        interface GPG45Status {
            String FRIENDLY_NAME = "GPG45Status";
            String NAME = "FECI_GPG45Status";
        }

        interface Firstname {
            String FRIENDLY_NAME = "Firstname";
            String NAME = "MDS_firstname";
        }

        interface Middlename {
            String FRIENDLY_NAME = "Middlename(s)";
            String NAME = "MDS_middlename";
        }

        interface Surname {
            String FRIENDLY_NAME = "Surname";
            String NAME = "MDS_surname";
        }

        interface Gender {
            String FRIENDLY_NAME = "Gender";
            String NAME = "MDS_gender";
        }

        interface DateOfBirth {
            String FRIENDLY_NAME = "Date of Birth";
            String NAME = "MDS_dateofbirth";
        }

        interface CurrentAddress {
            String FRIENDLY_NAME = "Current Address";
            String NAME = "MDS_currentaddress";
        }

        interface PreviousAddress {
            String FRIENDLY_NAME = "Previous Address";
            String NAME = "MDS_previousaddress";
        }

        interface IPAddress {
            String FRIENDLY_NAME = "IPAddress";
            String NAME = "TXN_IPaddress";
        }
    }
}
