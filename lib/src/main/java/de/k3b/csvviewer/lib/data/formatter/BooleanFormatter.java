package de.k3b.csvviewer.lib.data.formatter;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class BooleanFormatter implements FormatterApi<Boolean>, TableColumnComparatorFactoryImpl<Boolean> {
    private final String trueValue;
    private final String falseValue;
    private final char trueChar;
    private final char falseChar;

    private static final String yesCandidates = "1tyjsxTYJSX"; // true, yes ja, si, x
    private static final String noCandidates = "0fnFN"; // false no/nein

    /** try to guess which of the two values stand for true and wich stand for false.
     * @return null if cannot guess
     * */
    @Nullable
    public static BooleanFormatter createBooleanFormatter(@Nullable String value1, @Nullable String value2) {
        BooleanFormatter result = null;

        char v1 = getChar(value1);
        char v2 = getChar(value2);
        if (v1 != 0 || v2 != 0) {

            String yes = null;
            String no = null;
            if (noCandidates.indexOf(v1) >= 0 ) {
                no = value1;
                yes = value2;
            } else if (noCandidates.indexOf(v2) >= 0 ) {
                no = value2;
                yes = value1;
            } if (yesCandidates.indexOf(v1) >= 0 ) {
                no = value2;
                yes = value1;
            } else if (yesCandidates.indexOf(v2) >= 0 ) {
                no = value1;
                yes = value2;
            } else if (v1 == 0 && v2 != 0) {
                no = value1;
                yes = value2;
            } else if (v1 != 0 && v2 == 0) {
                no = value2;
                yes = value1;
            }
            if (yes != null && no != null) {
                result = new BooleanFormatter(yes, no);
            }
        }
        return result;
    }

    public static char getChar(@Nullable String trueValue) {
        return StringUtils.isBlank(trueValue) ? 0 : trueValue.trim().toLowerCase().charAt(0);
    }


    public BooleanFormatter(String trueValue, String falseValue) {
        this.trueValue = trueValue;
        this.falseValue = falseValue;
        trueChar = getChar(trueValue);
        falseChar = getChar(falseValue);
    }

    @Nullable @Override
    public String format(@Nullable Boolean b) {
        String result = null;
        if (b != null) {
            if (b) result = trueValue;
            if (!b) result = falseValue;
        }
        return result;
    }

    @Override
    public String toString() {
        return "BooleanFormatter{" +
                "true='" + trueValue + '\'' +
                ", false='" + falseValue + '\'' +
                '}';
    }

    @Nullable @Override
    public Boolean parse(@Nullable  String string) {
        Boolean result = null;
        char c = 0;
        if (!StringUtils.isBlank(string)) {
            string = string.trim().toLowerCase();
            c = string.charAt(0);
        }

        if (falseChar != 0 && c == falseChar) {
            result = Boolean.FALSE;
        } else if (trueChar != 0 && c == trueChar) {
            result = Boolean.TRUE;
        } else {
            result = string != null && !string.isEmpty();
        }
        return result;
    }

}
