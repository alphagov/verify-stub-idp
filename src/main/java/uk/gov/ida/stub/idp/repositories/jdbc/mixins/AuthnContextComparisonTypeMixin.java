package uk.gov.ida.stub.idp.repositories.jdbc.mixins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AuthnContextComparisonTypeMixin {
	@JsonCreator
	AuthnContextComparisonTypeMixin(@JsonProperty("comparisonType") String comparisonType) { }
}
