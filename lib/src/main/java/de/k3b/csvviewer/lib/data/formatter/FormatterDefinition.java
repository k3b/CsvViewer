package de.k3b.csvviewer.lib.data.formatter;

import java.io.Serializable;

public interface FormatterDefinition extends Serializable {
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
