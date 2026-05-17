package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class DateFormatter extends FormatterBase<Date> implements TableColumnComparatorFactoryImpl<Date> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);
    private final String formatPattern;
    private final DateFormat parser;

    public DateFormatter(@NonNull String formatPattern, @NonNull DateFormat parser, Boolean nullable) {
        super(Date.class, formatPattern, nullable);
        this.formatPattern = formatPattern;
        this.parser = parser;
    }
    /**
     * format a native value to a string
     *
     * @param nativeValue - item to be formatted
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
     * @param string - item to be parsed
     */
    @Nullable
    @Override
    public Date parse(@Nullable String string) {
        Date result = null;
        if (string != null) {
            try {
                result = parser.parse(string);
            } catch (ParseException e) {
                LOGGER.error("DateFormatter.parse(string='{}', formatPattern='{}') exception: {}", string,formatPattern, e.getMessage());
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
