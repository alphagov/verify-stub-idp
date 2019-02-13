package uk.gov.ida.analytics;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.lang.String.format;

public class CustomVariable {
    private final int index;
    private final String name;
    private final String value;

    public CustomVariable(int index, String name, String value) {
        this.index = index;
        this.name = name;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    protected String getJson() {
        return format("{\"%s\":[\"%s\",\"%s\"]}", getIndex(), getName(), getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomVariable that = (CustomVariable) o;
        return new EqualsBuilder()
                .append(index, that.index)
                .append(name, that.name)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(59, 71)
                .append(index)
                .append(name)
                .append(value)
                .toHashCode();
    }
}
