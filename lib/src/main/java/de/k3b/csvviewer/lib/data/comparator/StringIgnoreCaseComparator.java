package de.k3b.csvviewer.lib.data.comparator;

import java.util.Comparator;

public class StringIgnoreCaseComparator implements Comparator<Object> {
    @Override
    public int compare(Object var1, Object var2) {
        if (var1 == null) {
            return var2 == null ? 0 : 1;
        } else if (var2 == null) {
            return -1;
        } else {
            return ((String)var1).compareToIgnoreCase((String) var2);
        }
    }
}
