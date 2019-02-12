package uk.gov.ida.saml.core.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Optional;

public class MatchingDataset {

    private final List<TransliterableMdsValue> firstNames;
    private final List<SimpleMdsValue<String>> middleNames;
    private final List<TransliterableMdsValue> surnames;
    private final Optional<SimpleMdsValue<Gender>> gender;
    private final List<SimpleMdsValue<LocalDate>> dateOfBirths;
    private final List<Address> currentAddresses;
    private final List<Address> previousAddresses;
    private final String personalId;

    public MatchingDataset(
            List<TransliterableMdsValue> firstNames,
            List<SimpleMdsValue<String>> middleNames,
            List<TransliterableMdsValue> surnames,
            Optional<SimpleMdsValue<Gender>> gender,
            List<SimpleMdsValue<LocalDate>> dateOfBirths,
            List<Address> currentAddresses,
            List<Address> previousAddresses,
            String personalId) {
        this.firstNames = firstNames;
        this.middleNames = middleNames;
        this.surnames = surnames;
        this.gender = gender;
        this.dateOfBirths = dateOfBirths;
        this.currentAddresses = currentAddresses;
        this.previousAddresses = previousAddresses;
        this.personalId = personalId;
    }

    public List<TransliterableMdsValue> getFirstNames() {
        return firstNames;
    }

    public List<SimpleMdsValue<String>> getMiddleNames() {
        return middleNames;
    }

    public List<TransliterableMdsValue> getSurnames() {
        return surnames;
    }

    public Optional<SimpleMdsValue<Gender>> getGender() {
        return gender;
    }

    public List<SimpleMdsValue<LocalDate>> getDateOfBirths() {
        return dateOfBirths;
    }

    public List<Address> getCurrentAddresses() {
        return currentAddresses;
    }

    public List<Address> getPreviousAddresses() {
        return previousAddresses;
    }

    public List<Address> getAddresses() {
        return ImmutableList.copyOf(Iterables.concat(currentAddresses, previousAddresses));
    }

    public String getPersonalId() {
        return personalId;
    }
}
