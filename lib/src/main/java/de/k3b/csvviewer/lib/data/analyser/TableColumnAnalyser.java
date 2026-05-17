package de.k3b.csvviewer.lib.data.analyser;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.formatter.BooleanFormatter;
import de.k3b.csvviewer.lib.data.formatter.DateFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.IntegerFormatter;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;
import de.k3b.csvviewer.lib.data.formatter.ObjectFormatter;
import de.k3b.csvviewer.lib.data.formatter.StringFormatter;

/** analyse String content of a table column */
public class TableColumnAnalyser extends AnalyserBase<Object,Object> implements AnalyserApi<Object>, TableColumnDefinitionApi {
    private final int MIN_SUCCESS_ITEMS = 1;
    private boolean nullable = false;
    private int minStringLength = Integer.MAX_VALUE;
    private int maxStringLength = 0;

    // used to calculate average lenght of non blank items
    private long nonNullTotalLength = 0;
    private int nonNullStringCount = 0;

    private final DateAnalyser dateAnalyser;
    private final BooleanAnalyser booleanAnalyser;
    private final LongIntegerAnalyser longIntegerAnalyser;

    public TableColumnAnalyser(int maxErrors) {
        super(maxErrors);
        dateAnalyser = new DateAnalyser(maxErrors);
        booleanAnalyser = new BooleanAnalyser();
        longIntegerAnalyser = new LongIntegerAnalyser(maxErrors);
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    public int getMinStringLength() {
        return minStringLength;
    }

    @Override
    public int getMaxStringLength() {
        return maxStringLength;
    }

    public long getNonNullTotalLength() {
        return nonNullTotalLength;
    }

    @Override
    public boolean analyse(Long rowId, Object columnValue) {
        super.analyse(rowId, columnValue);
        String stingValue = columnValue == null ? null : columnValue.toString();

        if (!StringUtils.isBlank(stingValue)) {
            stingValue = stingValue.trim();
            int len = stingValue.length();
            if (len > maxStringLength) {
                maxStringLength = len;
                max = len + " (" + stingValue +")" ;
            }
            if (len < minStringLength) {
                minStringLength = len;
                min = len + " (" + stingValue +")" ;
            }
            nonNullTotalLength += len;
            nonNullStringCount++;

            longIntegerAnalyser.analyse(rowId, stingValue);
            dateAnalyser.analyse(rowId, stingValue);
            booleanAnalyser.analyse(rowId, stingValue);
        } else {
            nullable = true;
        }
        return true;
    }

    @Override
    public void addInfoRowsToReport(AnalyserReport report) {
        super.addInfoRowsToReport(report);
        dateAnalyser.addInfoRowsToReport(report);
        booleanAnalyser.addInfoRowsToReport(report);
        longIntegerAnalyser.addInfoRowsToReport(report);
    }

    public String getResultColumnValueForReport() {
        String result = null;
        FormatterApi<?> formatter = getFormatter();
        if (formatter != null) result =
                formatter.toString() + "; nullable:" + nullable+
                        "; len: '" + min +
                        "' .. '" + (nonNullTotalLength / nonNullStringCount) +
                        "' .. '" + max +
                        "'" +
                        "";
        return result;
    }

    @Override
    public @Nullable FormatterApi<?> getFormatter() {
        FormatterApi<?> result = getDateFormatter();
        if (result == null) result = getBooleanFormatter();
        if (result == null) result = getIntegerFormatter();
        if (result == null) result = getLongFormatter();
        if (result == null) result = new StringFormatter(true, -1);
        result = new ObjectFormatter(result);
        return result;
    }

    @Nullable public BooleanFormatter getBooleanFormatter() {
        BooleanFormatter result = null;
        if (booleanAnalyser.isEnabled() && nonNullStringCount >= 3) {
            result = booleanAnalyser.createFormatter();
        }
        return result;
    }

    @Nullable public DateFormatter getDateFormatter() {
        DateFormatter result = null;
        if (dateAnalyser.isEnabled() && nonNullStringCount >= MIN_SUCCESS_ITEMS) {
            result = dateAnalyser.createFormatter();
        }
        return result;
    }

    @Nullable public LongFormatter getLongFormatter() {
        LongFormatter result = null;
        if (longIntegerAnalyser.isEnabled() && nonNullStringCount >= MIN_SUCCESS_ITEMS) {
            result = longIntegerAnalyser.createFormatter();
        }
        return result;
    }

    @Nullable public IntegerFormatter getIntegerFormatter() {
        IntegerFormatter result = null;
        if (longIntegerAnalyser.isEnabled() && nonNullStringCount >= MIN_SUCCESS_ITEMS
                && longIntegerAnalyser.getMin() != null
                && longIntegerAnalyser.getMin() >= (long) Integer.MIN_VALUE
                && longIntegerAnalyser.getMax() <= (long) Integer.MAX_VALUE) {
                result = new IntegerFormatter(true);
        }
        return result;
    }
}
