package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;

public class IntegerFormatter implements FormatterApi<Integer> {
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Integer nativeValue) {
        if (nativeValue == null) return null;
        return Integer.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Integer parse(@Nullable String string) {
        Integer result = null;
        if (string != null) {
            try {
                result = Integer.parseInt(string);
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
