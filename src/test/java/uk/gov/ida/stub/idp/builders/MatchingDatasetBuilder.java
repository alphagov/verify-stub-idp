package uk.gov.ida.stub.idp.builders;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.*;

import java.util.*;

public class MatchingDatasetBuilder {
    private List<SimpleMdsValue<String>> firstnames = new ArrayList<>();
    private List<SimpleMdsValue<String>> middleNames = new ArrayList<>();
    private List<SimpleMdsValue<String>> surnames = new ArrayList<>();
    private Optional<SimpleMdsValue<Gender>> gender = Optional.empty();
    private List<SimpleMdsValue<LocalDate>> dateOfBirths = new ArrayList<>();
    private List<Address> currentAddresses = new ArrayList<>();
    private List<Address> previousAddresses = new ArrayList<>();

    public static MatchingDatasetBuilder aMatchingDataset() {
        return new MatchingDatasetBuilder();
    }

    public static MatchingDatasetBuilder aFullyPopulatedMatchingDataset() {
        final List<Address> currentAddressList = Collections.singletonList(new AddressFactory().create(Arrays.asList("subject-address-line-1"), "subject-address-post-code", "internation-postcode", "uprn", DateTime.parse("1999-03-15"), DateTime.parse("2000-02-09"), true));
        final List<Address> previousAddressList = Collections.singletonList(new AddressFactory().create(Arrays.asList("previous-address-line-1"), "subject-address-post-code", "internation-postcode", "uprn", DateTime.parse("1999-03-15"), DateTime.parse("2000-02-09"), true));
        final SimpleMdsValue<String> currentSurname = MatchingDatasetValueBuilder.<String>aSimpleMdsValue().withValue("subject-currentSurname").withVerifiedStatus(true).build().asSimpleMdsValue();
        return aMatchingDataset()
                .addFirstname(MatchingDatasetValueBuilder.<String>aSimpleMdsValue().withValue("subject-firstname").withVerifiedStatus(true).build().asSimpleMdsValue())
                .addMiddleNames(MatchingDatasetValueBuilder.<String>aSimpleMdsValue().withValue("subject-middlename").withVerifiedStatus(true).build().asSimpleMdsValue())
                .withSurnameHistory(Collections.singletonList(currentSurname))
                .withGender(MatchingDatasetValueBuilder.<Gender>aSimpleMdsValue().withValue(Gender.FEMALE).withVerifiedStatus(true).build().asSimpleMdsValue())
                .addDateOfBirth(MatchingDatasetValueBuilder.<LocalDate>aSimpleMdsValue().withValue(LocalDate.parse("2000-02-09")).withVerifiedStatus(true).build().asSimpleMdsValue())
                .withCurrentAddresses(currentAddressList)
                .withPreviousAddresses(previousAddressList);
    }

    public MatchingDataset build() {
        return new MatchingDataset(firstnames, middleNames, surnames, gender, dateOfBirths, currentAddresses, previousAddresses);
    }

    public MatchingDatasetBuilder addFirstname(SimpleMdsValue<String> firstname) {
        this.firstnames.add(firstname);
        return this;
    }

    public MatchingDatasetBuilder addMiddleNames(SimpleMdsValue<String> middleNames) {
        this.middleNames.add(middleNames);
        return this;
    }

    public MatchingDatasetBuilder addSurname(SimpleMdsValue<String> surname) {
        this.surnames.add(surname);
        return this;
    }

    public MatchingDatasetBuilder withGender(SimpleMdsValue<Gender> gender) {
        this.gender = Optional.ofNullable(gender);
        return this;
    }

    public MatchingDatasetBuilder addDateOfBirth(SimpleMdsValue<LocalDate> dateOfBirth) {
        this.dateOfBirths.add(dateOfBirth);
        return this;
    }

    public MatchingDatasetBuilder withCurrentAddresses(List<Address> currentAddresses) {
        this.currentAddresses = currentAddresses;
        return this;
    }

    public MatchingDatasetBuilder withPreviousAddresses(List<Address> previousAddresses) {
        this.previousAddresses = previousAddresses;
        return this;
    }

    public MatchingDatasetBuilder withoutFirstName() {
        this.firstnames.clear();
        return this;
    }

    public MatchingDatasetBuilder withoutMiddleName() {
        this.middleNames.clear();
        return this;
    }

    public MatchingDatasetBuilder withoutSurname() {
        this.surnames.clear();
        return this;
    }

    public MatchingDatasetBuilder withoutDateOfBirth() {
        this.dateOfBirths.clear();
        return this;
    }

    public MatchingDatasetBuilder withSurnameHistory(
            final List<SimpleMdsValue<String>> surnameHistory) {

        this.surnames.clear();
        this.surnames.addAll(surnameHistory);
        return this;
    }
}
