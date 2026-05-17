package de.k3b.csvviewer.lib.data.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.k3b.csvviewer.lib.data.analyser.DateAnalyser;

public class FormatterFactory {
    @SuppressWarnings("SimpleDateFormat")
    public static FormatterApi<?> createFormatter(String subType, String formatPattern, Boolean nullable, Integer maxStringLength) {
        FormatterApi<?> formatter = null;

        if (nullable == null) nullable = true;

        if (subType != null) {
            if (subType.compareToIgnoreCase(Integer.class.getSimpleName()) == 0) {
                formatter = new IntegerFormatter(nullable);
            } else if (subType.compareToIgnoreCase(Long.class.getSimpleName()) == 0) {
                formatter = new LongFormatter(nullable);
            } else if (subType.compareToIgnoreCase(Boolean.class.getSimpleName()) == 0 && formatPattern != null) {
                String[] trueFalse = formatPattern.split("\\|");
                if (trueFalse.length >= 2) {
                    formatter = new BooleanFormatter(trueFalse[0], trueFalse[1], nullable);
                }
            } else if (subType.compareToIgnoreCase(Date.class.getSimpleName()) == 0 && formatPattern != null) {
                DateFormat parser;

                if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_DATE) == 0) {
                    parser = SimpleDateFormat.getDateInstance();
                } else if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_TIME) == 0) {
                    parser = SimpleDateFormat.getTimeInstance();
                } else if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_DATE_TIME) == 0) {
                    parser = SimpleDateFormat.getDateTimeInstance();
                } else {
                    parser = new SimpleDateFormat(formatPattern);
                }

                formatter = new DateFormatter(formatPattern, parser, nullable);
            }
        }

        if (formatter == null) {
            // assume String
            if (maxStringLength == null) maxStringLength = -1;
            formatter = new StringFormatter(nullable, maxStringLength);
        }
        return formatter;
    }
}
