package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;

/** can string be converted to {@link Long} ? */
public class LongIntegerAnalyser extends AnalyserBase<Long, String> implements AnalyserApi<String>, FormatterFactoryApi<Long> {
    public LongIntegerAnalyser(int maxErrors) {
        super(maxErrors);
        min = Long.MAX_VALUE;
        max = Long.MIN_VALUE;
    }

    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean result = false;
        if (isEnabled()) {
            try {
                long longValue = Long.parseLong(stringValue);
                if (longValue > max) max = longValue;
                if (longValue < min) min = longValue;
                result = true;
            } catch (NumberFormatException ex) {
                addError(rowId);
            }
        }
        return result;
    }

    @Override
    public @Nullable LongFormatter createFormatter() {
        if (!isEnabled()) return null;
        return new LongFormatter();
    }
}
