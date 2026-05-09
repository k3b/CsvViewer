package de.k3b.csvviewer.lib.data.formatter;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class StringFormatter implements FormatterApi<String> {
    @Nullable @Override
    public String format(@Nullable String result) {
        return result;
    }

    @Override
    public String toString() {
        return "StringFormatter";
    }

    @Nullable @Override
    public String parse(@Nullable  String result) {
        return result;
    }
}
