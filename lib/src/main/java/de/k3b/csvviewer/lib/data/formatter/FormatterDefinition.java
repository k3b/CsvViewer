package de.k3b.csvviewer.lib.data.formatter;

public interface FormatterDefinition {
    String getElementClassName();

    String getFormatPattern();

    Boolean isNullable();

    Integer getMaxStringLength();

    static String toString(FormatterDefinition t) {
        return t.getElementClassName() +
                "(formatPattern='" + t.getFormatPattern() + '\'' +
                ", nullable=" + t.isNullable() +
                ", maxStringLength=" + t.getMaxStringLength() +
                ')';
    }
}
