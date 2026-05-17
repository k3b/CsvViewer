package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel.ColumnDefinition;
import de.k3b.csvviewer.lib.data.filter.TableModelComparatorFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;

public abstract class ConfigurationInterpreterBase<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);

    @NonNull protected final ConfigurationModel target;
    @NonNull protected final String configurationType;

    public ConfigurationInterpreterBase(@NonNull ConfigurationModel target, @NonNull String configurationType) {
        this.target = target;
        this.configurationType = configurationType;
    }

    /** Transfer filters based on {@link TableModelRowFilterBase} from {@link #target} to {@link ConfigurationModel}. */
    protected Object[] addConfig(int columnNumber, String subType, String description, Object parameter1, Object parameter2, Object parameter3) {
        String columnName = target.getTargetColumnName(columnNumber);

        Object[] row = target.createEmptyRow();
        row[ColumnDefinition.col_colName]=columnName;
        row[ColumnDefinition.col_configType] = configurationType;
        row[ColumnDefinition.col_subType] = subType;
        row[ColumnDefinition.col_description] = description;
        row[ColumnDefinition.col_parameter1] = parameter1;
        row[ColumnDefinition.col_parameter2] = parameter2;
        row[ColumnDefinition.col_parameter3] = parameter3;

        target.addRow(row);
        return row;
    }

    /** convert from T configValue in {@link #target} to Object[] configRow-s for {@link ConfigurationModel}*/
    public abstract void addConfig(@Nullable T configValue);

    /** convert from configRow to configValue */
    @NonNull public T parse(@NonNull List<Object[]> configRows) {
        TableModelComparatorFilter filter = new TableModelComparatorFilter(
                ColumnDefinition.col_configType, new StringIgnoreCaseComparator(),
                this.configurationType, TableModelComparatorFilter.EQUALS);

        List<Object[]> myConfigRows = new ArrayList<>();
        for (Object[] configRow : configRows) {
            if (filter.match(configRow)) {
                myConfigRows.add(configRow);
            }
        }

        return parseImpl(myConfigRows);

        /*
        for (int i = 0; i < configRows.size(); i++) {
        String columnName = (String) configRow[ColumnDefinition.col_colName];
        int columnNumber = target.getTargetColumnNumber(columnName);
        String description = (String) configRow[ColumnDefinition.col_description];
        String subType = (String) configRow[ColumnDefinition.col_subType];
        Object parameter1 = configRow[ColumnDefinition.col_parameter1];
        return parse(columnNumber, columnName, subType, description, parameter1);

    public T parse(int columnNumber, String columnName, String subType, String description, Object parameter1) {
    }

         */
    }

    /** execute the parse on cnfigRows that match {@link #configurationType}  */
    @NonNull protected abstract T parseImpl(@NonNull List<Object[]> myConfigRows);
}
