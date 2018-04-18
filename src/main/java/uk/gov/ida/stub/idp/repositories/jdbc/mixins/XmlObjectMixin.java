package uk.gov.ida.stub.idp.repositories.jdbc.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opensaml.core.xml.schema.XSBooleanValue;

import javax.annotation.Nullable;

public abstract class XmlObjectMixin {
	@JsonIgnore
	public abstract void setNil(@Nullable final XSBooleanValue newNil);
}
