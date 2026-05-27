package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class DoubleFormatter extends FormatterBase<Double> implements TableColumnComparatorFactoryImpl<Double> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    public DoubleFormatter(boolean nullable) {
        super(Double.class,null, nullable);
    }
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Double nativeValue) {
        if (nativeValue == null) return null;
        return Double.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Double parse(@Nullable String string) {
        Double result = null;
        if (string != null) {
            try {
                result = Double.parseDouble(string);
            } catch (NumberFormatException e) {
                LOGGER.error("DoubleFormatter.parse(string='{}') exception: {}", string,e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
