package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;

/**
 * FormatterApi can format a native value to a string or parse a string to a native value.
 * @param <T> - the native type
 */
public interface FormatterApi<T> extends TableColumnComparatorFactory {
    /** format a native value to a string */
    @Nullable String format(@Nullable T nativeValue);
    default @Nullable String formatObject(@Nullable Object nativeValue) {
        return format((T) nativeValue);
    }

    /** parse a string to a native value */
    @Nullable T parse(@Nullable String string);

}
