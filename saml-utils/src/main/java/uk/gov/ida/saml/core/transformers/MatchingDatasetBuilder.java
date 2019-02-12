package uk.gov.ida.saml.core.transformers;

import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.saml.core.domain.TransliterableMdsValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

class MatchingDatasetBuilder {
    private List<TransliterableMdsValue> firstnames = new ArrayList<>();
    private List<SimpleMdsValue<String>> middlenames = new ArrayList<>();
    private List<TransliterableMdsValue> surnames = new ArrayList<>();
    private Optional<SimpleMdsValue<Gender>> gender = Optional.empty();
    private List<SimpleMdsValue<LocalDate>> dateOfBirths = new ArrayList<>();
    private List<Address> currentAddresses = newArrayList();
    private List<Address> previousAddresses = newArrayList();
    private String personalId;

    public void addFirstNames(List<TransliterableMdsValue> firstnames) {
        this.firstnames.addAll(firstnames);
    }

    public void addSurnames(List<TransliterableMdsValue> surnames) {
        this.surnames.addAll(surnames);
    }

    public void gender(SimpleMdsValue<Gender> gender) {
        this.gender = Optional.ofNullable(gender);
    }

    public void dateOfBirth(List<SimpleMdsValue<LocalDate>> dateOfBirths) {
        this.dateOfBirths.addAll(dateOfBirths);
    }

    public void addCurrentAddresses(List<Address> currentAddresses) {
        this.currentAddresses.addAll(currentAddresses);
    }

    public void personalId(String personalId) {
        this.personalId = personalId;
    }

    public void middlenames(List<SimpleMdsValue<String>> middlenames) {
        this.middlenames.addAll(middlenames);
    }

    public void addPreviousAddresses(List<Address> previousAddresses) {
        this.previousAddresses.addAll(previousAddresses);
    }

    public MatchingDataset build() {
        return new MatchingDataset(
                firstnames,
                middlenames,
                surnames,
                gender,
                dateOfBirths,
                currentAddresses,
                previousAddresses,
                personalId
        );
    }
}
