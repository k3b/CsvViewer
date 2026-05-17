package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

@Deprecated
public class TableColumnDefinitionDto implements TableColumnDefinitionApi {
    private final boolean nullable;
    private final int maxStringLength;
    private final FormatterApi<?> formatter;

    public TableColumnDefinitionDto(@NonNull FormatterApi<?>  source) {
        this(source.isNullable(), source.getMaxStringLength(), source);
    }

    public TableColumnDefinitionDto(@NonNull TableColumnDefinitionApi source) {
        this(source.isNullable(), source.getMaxStringLength(), source.getFormatter());
    }

    private TableColumnDefinitionDto(boolean nullable, int maxStringLength, FormatterApi<?> formatter) {
        this.nullable = nullable;
        this.maxStringLength = maxStringLength;
        this.formatter = formatter;
    }

    @Override
    public int getMaxStringLength() {
        return maxStringLength;
    }

    @Override
    public @Nullable FormatterApi<?> getFormatter() {
        return formatter;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public String toString() {
        return "TableColumnDefinitionDto{" +
                "nullable=" + nullable +
                ", maxStringLength=" + maxStringLength +
                ", formatter=" + formatter +
                '}';
    }
}
