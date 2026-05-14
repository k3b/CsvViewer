package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;

import java.util.Comparator;

/** Implements a factory to create a TableColumnComparator that can be used to sort a table  */
public interface TableColumnComparatorFactoryImpl<T extends Comparable<T>> extends TableColumnComparatorFactory {

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    default Comparator<Object> getComparator() {
        return new Comparator<>() {
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
        };
    }
}
