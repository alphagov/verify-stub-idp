package uk.gov.ida.stub.idp.domain;

import java.util.Optional;

/**
 * This can't be used to read path params directly as we are using params with dashes
 */
public enum EidasScheme {
    stub_country("stub-country", true),
    stub_country_unsigned_assertions("stub-country-unsigned-assertions", false),
    stub_cef_reference("stub-cef-reference", true),

    austria("austria", true),
    belgium("belgium", true),
    bulgaria("bulgaria", true),
    croatia("croatia", true),
    cyprus("cyprus", true),
    czech_republic("czech-republic", true),
    denmark("denmark", true),
    estonia("estonia", true),
    finland("finland", true),
    france("france", true),
    germany("germany", true),
    greece("greece", true),
    hungary("hungary", true),
    ireland("ireland", true),
    italy("italy", true),
    latvia("latvia", true),
    lithuania("lithuania", true),
    luxembourg("luxembourg", true),
    malta("malta", true),
    netherlands("netherlands", true),
    poland("poland", true),
    portugal("portugal", true),
    romania("romania", true),
    slovakia("slovakia", true),
    slovenia("slovenia", true),
    spain("spain", false), // unsigned assertions
    sweden("sweden", true),
    united_kingdom("united-kingdom", true),
    iceland("iceland", true),
    liechtestein("liechtestein", true),
    norway("norway", true);

    private String eidasSchemeName;
    private boolean shouldSignAssertions;

    EidasScheme(String eidasSchemeName, boolean shouldSignAssertions) {
        this.eidasSchemeName = eidasSchemeName;
        this.shouldSignAssertions = shouldSignAssertions;
    }

    public String getEidasSchemeName() {
        return eidasSchemeName;
    }
    public boolean getSignsAssertions() { return shouldSignAssertions; }

    public static Optional<EidasScheme> fromString(String eidasSchemeName) {
        for(EidasScheme eidasScheme : values()) {
            if(eidasScheme.getEidasSchemeName().equals(eidasSchemeName)) {
                return Optional.ofNullable(eidasScheme);
            }
        }
        return Optional.empty();
    }

}
