package de.k3b.csvviewer.lib.data.comparator;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/** Implements a factory to create a TableColumnComparator that can be used to sort a table  */
public interface TableColumnComparatorFactoryImpl<T extends Comparable<T>> extends Serializable, TableColumnComparatorFactory {

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    default Comparator<Object> getComparator() {
        return new GenericComparator<T>();
    }
}
