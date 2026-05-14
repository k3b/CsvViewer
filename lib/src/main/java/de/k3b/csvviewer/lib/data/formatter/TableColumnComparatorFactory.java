package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;

import java.util.Comparator;

/** Defines a factory to create a TableColumnComparator that can be used to sort a table  */
public interface TableColumnComparatorFactory<T>  {

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    Comparator<Object> getComparator();
}
