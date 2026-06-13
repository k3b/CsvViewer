package de.k3b.csvviewer.lib.data.comparator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;

/** to define compare result as lamda expression */
@FunctionalInterface
public interface CompareResult {
    boolean apply(@NonNull Comparator<Object> comparator, @Nullable Object fieldValue, @Nullable Object compareValue);
}
