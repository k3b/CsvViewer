package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateFormatter implements FormatterApi<Date> {
    private final String formatPattern;
    private final DateFormat parser;

    public DateFormatter(@NonNull String formatPattern, @NonNull DateFormat parser) {
        this.formatPattern = formatPattern;
        this.parser = parser;
    }
    /**
     * format a native value to a string
     *
     * @param nativeValue
     */
    @Nullable
    @Override
    public String format(@Nullable Date nativeValue) {
        if (nativeValue == null) return null;
        return parser.format(nativeValue);
    }

    /**
     * parse a string to a native value
     *
     * @param string
     */
    @Nullable
    @Override
    public Date parse(@Nullable String string) {
        Date result = null;
        if (string != null) {
            try {
                result = parser.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "DateFormatter{" +
                "formatPattern='" + formatPattern + '\'' +
                '}';
    }
}
