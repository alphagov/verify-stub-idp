package uk.gov.ida.stub.idp.domain;

import java.util.Optional;

/**
 * This can't be used to read path params directly as we are using params with dashes
 */
public enum EidasScheme {
    stub_country("stub-country"),
    stub_country_unsigned_assertions("stub-country-unsigned-assertions", false),
    stub_cef_reference("stub-cef-reference"),

    austria("austria"),
    belgium("belgium"),
    bulgaria("bulgaria"),
    croatia("croatia"),
    cyprus("cyprus"),
    czech_republic("czech-republic"),
    denmark("denmark"),
    estonia("estonia"),
    finland("finland"),
    france("france"),
    germany("germany"),
    greece("greece"),
    hungary("hungary"),
    ireland("ireland"),
    italy("italy"),
    latvia("latvia"),
    lithuania("lithuania"),
    luxembourg("luxembourg"),
    malta("malta"),
    netherlands("netherlands"),
    poland("poland"),
    portugal("portugal"),
    romania("romania"),
    slovakia("slovakia"),
    slovenia("slovenia"),
    spain("spain", false),
    sweden("sweden"),
    united_kingdom("united-kingdom"),
    iceland("iceland"),
    liechtestein("liechtestein"),
    norway("norway");

    private String eidasSchemeName;
    private boolean shouldSignAssertions;

    EidasScheme(String eidasSchemeName) {
        this(eidasSchemeName, true);
    }

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
