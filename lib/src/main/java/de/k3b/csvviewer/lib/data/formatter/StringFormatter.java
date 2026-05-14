package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

public class StringFormatter implements FormatterApi<String>, TableColumnComparatorFactoryImpl<String> {
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

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    public Comparator<Object> getComparator() {
        return new Comparator<>() {
            @Override
            public int compare(Object var1, Object var2) {
                if (var1 == null) {
                    return var2 == null ? 0 : 1;
                } else if (var2 == null) {
                    return -1;
                } else {
                    return ((String)var1).compareToIgnoreCase((String) var2);
                }
            }
        };
    }
}
