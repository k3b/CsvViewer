package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class StringFormatter extends FormatterBase<String> implements TableColumnComparatorFactoryImpl<String> {
    private final Integer maxStringLength;

    public StringFormatter(boolean nullable, int maxStringLength) {
        super(String.class,null, nullable, ComparatorTyp.VALUE_SIMPLE);
        this.maxStringLength = maxStringLength;
    }

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

    @Override
    public Integer getMaxStringLength() {
        return maxStringLength;
    }
}
