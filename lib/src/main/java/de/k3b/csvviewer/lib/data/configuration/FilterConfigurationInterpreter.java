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

import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel.DomainColumnModel;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@link ConfigurationModel} interpreter for filters based on {@link TableModelRowFilterBase}  */
public class FilterConfigurationInterpreter extends ConfigurationInterpreterBase<@Nullable List<TableModelRowFilterBase>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public FilterConfigurationInterpreter(@NonNull ConfigurationModel target, @NonNull String configurationType) {
        super(target, configurationType);
    }

    /** Transfer filters based on {@link TableModelRowFilterBase} from {@link #target} to {@link ConfigurationModel}. */
    @Override
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
                        LOGGER.warn("FilterConfigurationInterpreter.addConfig() : {}", compareTyp);
                    }
                    addConfig(columnNumber, compareTyp, filterName, compareParameter, null, null);
                }
            }

        }

    }

    /** execute the parse on cnfigRows that match {@link #configurationType}  */
    @Override @NonNull
    protected List<TableModelRowFilterBase> parseImpl(@NonNull List<Object[]> myConfigRows) {
        List<TableModelRowFilterBase> result = new ArrayList<>();
        for(int rowNo = myConfigRows.size() - 1; rowNo >= 0; rowNo--) {
            Object[] configRow = myConfigRows.get(rowNo);

            Object parameter1 = configRow[ConfigurationModel.DomainColumnModel.col_parameter1];
            String description = (String) configRow[ConfigurationModel.DomainColumnModel.col_description];
            String subType = (String) configRow[ConfigurationModel.DomainColumnModel.col_subType];
            String columnName = (String) configRow[DomainColumnModel.col_colName];
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

    public static List<TableModelRowFilterBase> parseExpression(InMemoryTableModel model, String expression) {
        List<TableModelRowFilterBase> result = new ArrayList<>();
        for (int columnNumber = model.getColumnCount()-1;columnNumber >= 0; columnNumber--) {
            FormatterApi<?> columnFormatter = TableModelUtils.getColumnFormatter(model,columnNumber);
            // columnFormatter.getMaxStringLength();

            // TODO !!!
        }

        return result;
    }
}
