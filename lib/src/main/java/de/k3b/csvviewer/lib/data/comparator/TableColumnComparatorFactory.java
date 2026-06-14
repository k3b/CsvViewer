package de.k3b.csvviewer.lib.data.comparator;

import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/** Defines a factory to create a TableColumnComparator that can be used to sort a table  */
public interface TableColumnComparatorFactory extends Serializable {

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    Comparator<Object> getComparator();
}
