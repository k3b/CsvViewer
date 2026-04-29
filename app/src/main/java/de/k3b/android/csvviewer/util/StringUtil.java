package de.k3b.android.csvviewer.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class StringUtil {
    /**
     * Modified version of org.apache.commons.lang3.StringUtils#indexOfAny.
     * <p>
     * Same as {@link StringUtils#indexOfAny(CharSequence, char...)
     * where you can specify the search intervall}
     * License of this function is Apache2
     */
    public static int indexOfAny(final CharSequence cs, int csFirst, int csLen, final char... searchChars) {
        if (StringUtils.isEmpty(cs) || ArrayUtils.isEmpty(searchChars)) {
            return StringUtils.INDEX_NOT_FOUND;
        }
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = csFirst; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        // ch is a supplementary character
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            return i;
                        }
                    } else {
                        return i;
                    }
                }
            }
        }
        return StringUtils.INDEX_NOT_FOUND;
    }


}
