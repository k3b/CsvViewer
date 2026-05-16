package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

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
        return new StringIgnoreCaseComparator();
    }
}
