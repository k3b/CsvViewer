package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactory;

/**
 * FormatterApi can format a native value to a string or parse a string to a native value.
 * @param <T> - the native type
 */
public interface FormatterApi<T> extends FormatterDefinition, TableColumnComparatorFactory, Serializable {
    /** format a native value to a string */
    @Nullable String format(@Nullable T nativeValue);
    default @Nullable String formatObject(@Nullable Object nativeValue) {
        return format((T) nativeValue);
    }

    /** parse a string to a native value */
    @Nullable T parse(@Nullable String string);

    default Integer getMaxStringLength() {return null;}

    @NonNull List<@NonNull ComparatorTyp> getAllowedComparators();
}
