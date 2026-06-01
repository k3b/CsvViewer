package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;

abstract class FormatterBase<T> implements FormatterApi<T> {
    private @Nullable  final Class<?> elementClass;
    private @Nullable final String formatPattern;
    private final Boolean nullable;
    @NonNull
    private final List<@NonNull ComparatorTyp> allowedComparators;

    FormatterBase(@Nullable Class<?> elementClass, @Nullable String formatPattern,
                  boolean nullable, @NonNull List<@NonNull ComparatorTyp> allowedComparators) {
        this.elementClass = elementClass;
        this.formatPattern = formatPattern;
        this.nullable = nullable;
        this.allowedComparators = allowedComparators;
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

    public @NonNull List<@NonNull ComparatorTyp> getAllowedComparators() {
        return allowedComparators;
    }

}
