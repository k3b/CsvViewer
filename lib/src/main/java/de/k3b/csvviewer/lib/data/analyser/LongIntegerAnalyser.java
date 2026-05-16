package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;

/** can string be converted to {@link Long} ? */
public class LongIntegerAnalyser extends AnalyserBase<Long, String> implements AnalyserApi<String>, FormatterFactoryApi<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    public LongIntegerAnalyser(int maxErrors) {
        super(maxErrors);
    }

    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean result = false;
        if (isEnabled()) {
            try {
                long longValue = Long.parseLong(stringValue);
                if (max == null || longValue > max) max = longValue;
                if (min == null || longValue < min) min = longValue;
                result = true;
            } catch (NumberFormatException ex) {
                LOGGER.error("LongIntegerAnalyser.analyse(rowId={},stringValue={}) exception: {}", rowId, stringValue, ex.getMessage());
                addError(rowId, stringValue);
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
