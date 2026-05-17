package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

/** Defines properties of a table column */
@Deprecated
public interface TableColumnDefinitionApi {
    boolean isNullable();

    int getMaxStringLength();

    @Nullable FormatterApi<?> getFormatter();
}
