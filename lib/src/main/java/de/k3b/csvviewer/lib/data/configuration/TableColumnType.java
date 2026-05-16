package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import de.k3b.csvviewer.lib.data.analyser.DateAnalyser;
import de.k3b.csvviewer.lib.data.formatter.BooleanFormatter;
import de.k3b.csvviewer.lib.data.formatter.DateFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.IntegerFormatter;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;

/**
 * Translates between Object and String.
 */
public enum TableColumnType {
    Integer(new IntegerFormatter()),
    Long(new LongFormatter()),
    Boolean(new BooleanFormatter("1","0")),
    Date(new DateFormatter(DateAnalyser.ISO_DATE_TIME_PATTERN, new SimpleDateFormat(DateAnalyser.ISO_DATE_TIME_PATTERN))),
    ;

    @NonNull private final FormatterApi<?> formatter;

    private TableColumnType(@NonNull FormatterApi<?> formatter) {
        this.formatter = formatter;
    }

    @Nullable public String toStringInternal(@Nullable Object value) {
        return formatter.formatObject(value);
    }

    /** parse a string to a native value */
    @Nullable Object parse(@Nullable String string) {
        return formatter.parse(string);
    }

    public static String toString(@Nullable Object value) {
        String result = null;
        if (value != null) {
            String typeName = value.getClass().getSimpleName();
            TableColumnType tableColumnType = valueOf(typeName);
            if (tableColumnType != null) {
                result = tableColumnType.toStringInternal(value);
            } else {
                result = tableColumnType +"[" + value.toString() + "]";
            }
        }
        return result;
    }

    public static @Nullable Comparator<Object> getComparator(@Nullable Object value) {
        Comparator<Object> result = null;
        if (value != null) {
            String typeName = value.getClass().getSimpleName();
            TableColumnType tableColumnType = valueOf(typeName);
            if (tableColumnType != null) {
                result = tableColumnType.formatter.getComparator();
            }
        }
        return result;
    }

}
