package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class IntegerFormatter implements FormatterApi<Integer>, TableColumnComparatorFactoryImpl<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Integer nativeValue) {
        if (nativeValue == null) return null;
        return Integer.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Integer parse(@Nullable String string) {
        Integer result = null;
        if (string != null) {
            try {
                result = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                LOGGER.error("IntegerFormatter.parse(string='{}') exception: {}", string,e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
