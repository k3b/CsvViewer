package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class LongFormatter extends FormatterBase<Long> implements TableColumnComparatorFactoryImpl<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    public LongFormatter(boolean nullable) {
        super(Long.class,null, nullable);
    }
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Long nativeValue) {
        if (nativeValue == null) return null;
        return Long.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Long parse(@Nullable String string) {
        Long result = null;
        if (string != null) {
            try {
                result = Long.parseLong(string);
            } catch (NumberFormatException e) {
                LOGGER.error("LongFormatter.parse(string='{}') exception: {}", string,e.getMessage());
            }
        }
        return result;
    }
}
