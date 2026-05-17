package de.k3b.csvviewer.lib.data.formatter;

public interface FormatterDefinition {
    String getElementClassName();

    String getFormatPattern();

    Boolean isNullable();

    Integer getMaxStringLength();
}
