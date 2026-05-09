package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.formatter.BooleanFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;

/** can string be converted to {@link Boolean} ? */
public class BooleanAnalyser extends AnalyserBase<Boolean,String> implements AnalyserApi<String>, FormatterFactoryApi<Boolean> {
    //-------------
    // true: can be convertet to boolean (there are only 2 different string values ie Yes/No or true/false)
    private String value1 = null;
    private String value2 = null;

    public BooleanAnalyser() {
        super(1);
    }

    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean isBoolean = false;
        if (isEnabled()) {
            isBoolean = true;
            if (value1 == null) {
                value1 = stringValue.toLowerCase();
            } else if (value1.compareToIgnoreCase(stringValue) != 0) {
                if (value2 == null) {
                    value2 = stringValue.toLowerCase();
                } else if (value2.compareToIgnoreCase(stringValue) != 0) {
                    // third value found: cannot be boolean
                    isBoolean = false;
                    addError(rowId);
                }
            }
        }
        return isBoolean;
    }

    @Nullable
    public BooleanFormatter createFormatter() {
        if (!isEnabled()) return null;
        return BooleanFormatter.createBooleanFormatter(value1, value2);
    }


}

