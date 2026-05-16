package de.k3b.csvviewer.lib.data.comparator;

import java.util.Comparator;

public class GenericComparator<T extends Comparable<T>> implements Comparator<Object> {
    @Override
    public int compare(Object var1, Object var2) {
        if (var1 == null) {
            return var2 == null ? 0 : 1;
        } else if (var2 == null) {
            return -1;
        } else {
            return ((T)var1).compareTo((T) var2);
        }
    }
}
