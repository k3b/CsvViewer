package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;

abstract class FormatterBase<T> implements FormatterApi<T> {
    private @Nullable  final Class<?> elementClass;
    private @Nullable final String formatPattern;
    private final Boolean nullable;

    FormatterBase(@Nullable Class<?> elementClass, @Nullable String formatPattern, boolean nullable) {
        this.elementClass = elementClass;
        this.formatPattern = formatPattern;
        this.nullable = nullable;
    }

    @Nullable
    public String getElementClassName() {
        return elementClass.getSimpleName();
    }

    @Nullable
    public String getFormatPattern() {
        return formatPattern;
    }

    @Nullable
    public Boolean isNullable() {
        return nullable;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " +FormatterDefinition.toString(this);
    }

}
