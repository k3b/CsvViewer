package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;

public class LongFormatter implements FormatterApi<Long> {
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Long nativeValue) {
        if (nativeValue == null) return null;
        return Long.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Long parse(@Nullable String string) {
        Long result = null;
        if (string != null) {
            try {
                result = Long.parseLong(string);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
