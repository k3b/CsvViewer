package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.filter.TableModelComparatorFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelNotNullFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelNullFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;

import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel.ColumnDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationInterpreterFilter extends ConfigurationInterpreterBase<@Nullable List<TableModelRowFilterBase>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public ConfigurationInterpreterFilter(@NonNull ConfigurationModel target, @NonNull String configurationType) {
        super(target, configurationType);
    }

    public void addConfig(@Nullable List<TableModelRowFilterBase> filterList) {
        if (filterList != null && !filterList.isEmpty()) {
            for (TableModelRowFilterBase filter : filterList) {
                if (filter != null) {
                    int columnNumber = filter.getColumnNumber();
                    String filterName = filter.toString(target.getTargetColumnNames());
                    String compareTyp = null;
                    Object compareParameter = null;
                    if (filter instanceof TableModelComparatorFilter) {
                        TableModelComparatorFilter comparatorFilter = (TableModelComparatorFilter) filter;
                        compareTyp = comparatorFilter.getComparatorModeName();
                        compareParameter = comparatorFilter.getCompareValue();
                    } else if (filter instanceof TableModelNullFilter) {
                        compareTyp = TableModelNullFilter.compareTyp;
                    } else if (filter instanceof TableModelNotNullFilter) {
                        compareTyp = TableModelNotNullFilter.compareTyp;
                    } else {
                        compareTyp = "???[" + filter.getClass().getSimpleName() + "]";
                        LOGGER.warn("ConfigurationInterpreterFilter.addConfig() : {}", compareTyp);

                    }
                    addConfig(columnNumber, compareTyp, filterName, compareParameter);
                }
            }

        }

    }

    /**
     * execute the parse on cnfigRows that match {@link #configurationType}
     *
     * @param myConfigRows
     */
    @Override
    protected @Nullable List<TableModelRowFilterBase> parseImpl(@NonNull List<Object[]> myConfigRows) {
        List<TableModelRowFilterBase> result = new ArrayList<>();
        for(int rowNo = myConfigRows.size() - 1; rowNo >= 0; rowNo--) {
            Object[] configRow = myConfigRows.get(rowNo);

            Object parameter1 = configRow[ColumnDefinition.col_parameter1];
            String description = (String) configRow[ColumnDefinition.col_description];
            String subType = (String) configRow[ColumnDefinition.col_subType];
            String columnName = (String) configRow[ColumnDefinition.col_colName];
            int columnNumber = target.getTargetColumnNumber(columnName);

            TableModelRowFilterBase filter = createFilter(subType, columnNumber, parameter1);

            if (filter != null) result.add(filter);
        }
        return result;
    }

    private static TableModelRowFilterBase createFilter(String subType, int columnNumber, Object parameter1) {
        TableModelRowFilterBase filter = null;
        if (subType != null) {
            if (TableModelNullFilter.compareTyp.compareToIgnoreCase(subType) == 0) {
                filter = new TableModelNullFilter(columnNumber);
            } else if (TableModelNotNullFilter.compareTyp.compareToIgnoreCase(subType) == 0) {
                filter = new TableModelNotNullFilter(columnNumber);
            } else if (parameter1 != null) {
                int comparatorMode = TableModelComparatorFilter.getModeByTypeString(subType);
                Comparator<Object> comparator = TableColumnType.getComparator(parameter1);
                if (comparator == null) comparator = new StringIgnoreCaseComparator();
                filter = new TableModelComparatorFilter(columnNumber, comparator,
                        parameter1, comparatorMode);
            }
        }
        return filter;
    }

}
