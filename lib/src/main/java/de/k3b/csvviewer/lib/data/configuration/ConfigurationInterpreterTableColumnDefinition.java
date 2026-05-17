package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.analyser.DateAnalyser;
import de.k3b.csvviewer.lib.data.analyser.TableColumnDefinitionApi;
import de.k3b.csvviewer.lib.data.analyser.TableColumnDefinitionDto;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.BooleanFormatter;
import de.k3b.csvviewer.lib.data.formatter.DateFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel.ColumnDefinition;
import de.k3b.csvviewer.lib.data.formatter.IntegerFormatter;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;
import de.k3b.csvviewer.lib.data.formatter.StringFormatter;

/** {@link ConfigurationModel} interpreter for filters based on {@link TableModelRowFilterBase}  */
public class ConfigurationInterpreterTableColumnDefinition extends ConfigurationInterpreterBase<@Nullable TableColumnDefinitionApi[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public static final String CONFIGURATION_TYPE = "columnDefinition";
    public ConfigurationInterpreterTableColumnDefinition(@NonNull ConfigurationModel target) {
        super(target, CONFIGURATION_TYPE);
    }

    /** Transfer filters based on {@link TableModelRowFilterBase} from {@link #target} to {@link ConfigurationModel}. */
    @Override
    public void addConfig(TableColumnDefinitionApi[] definitions) {
        if (definitions != null) {
            int colCount = definitions.length;
            if (colCount > 0) {
                for (int columnNumber = 0; columnNumber < colCount; columnNumber++) {
                    TableColumnDefinitionApi definition = definitions[columnNumber];
                    addConfig(columnNumber, definition);
                }
            }
        }
    }

    public void addConfig(List<TableColumnDefinitionApi> definitions) {
        if (definitions != null) {
            int colCount = definitions.size();
            if (colCount > 0) {
                for (int columnNumber = 0; columnNumber < colCount; columnNumber++) {
                    Object object = definitions.get(columnNumber);
                    TableColumnDefinitionApi definition = (TableColumnDefinitionApi) object;
                    addConfig(columnNumber, definition);
                }
            }
        }
    }

    private void addConfig(int columnNumber, TableColumnDefinitionApi definition) {
        FormatterApi<?> formatter = definition == null ? null : definition.getFormatter();
        if (formatter != null) {
            String description = definition.toString();
            String subTyp = formatter.getElementClassName();
            Object formatPattern = formatter.getFormatPattern();
            Object nullAble = definition.isNullable() ? null : "0";
            Integer maxStringLen = definition.getMaxStringLength();
            if (maxStringLen == -1) maxStringLen = null;
            addConfig(columnNumber, subTyp, description, formatPattern, nullAble, maxStringLen);
        }
    }

    /**
     * execute the parse on cnfigRows that match {@link #configurationType}
     *
     * @param myConfigRows
     */
    @Override
    protected @Nullable TableColumnDefinitionApi @NonNull [] parseImpl(@NonNull List<Object[]> myConfigRows) {
        TableColumnDefinitionApi[] result = new TableColumnDefinitionApi[target.getColumnCount()];
        for (Object[] row : myConfigRows) {
            String columnName = (String) row[ColumnDefinition.col_colName];
            int columnNumber = target.getTargetColumnNumber(columnName);
            if (columnNumber >= 0) {
                result[columnNumber] = parseColumnDefinition(columnNumber, columnName, row);
            } else {
                LOGGER.warn("ConfigurationInterpreterTableColumnDefinition.parseImpl(columnName='{}'): not loading TableColumnDefinition", columnName);
            }
        }
        return result;
    }

    @SuppressWarnings("SimpleDateFormat")
    private TableColumnDefinitionApi parseColumnDefinition(int columnNumber, String columnName, Object[] row) {
        TableColumnDefinitionApi result = null;

        FormatterApi<?> formatter = null;

        String subType = (String) row[ColumnDefinition.col_subType];
        String formatPattern = TableColumnType.Boolean.parseImpl(row[ColumnDefinition.col_parameter1]);
        Boolean nullable = TableColumnType.Boolean.parseImpl(row[ColumnDefinition.col_parameter2]);
        Integer maxStringLength = -1;
        maxStringLength = TableColumnType.Integer.parseImpl(row[ColumnDefinition.col_parameter3]);

        formatter = createFormatter(subType, formatPattern, nullable, maxStringLength);
        return new TableColumnDefinitionDto(formatter);
    }

    @SuppressWarnings("SimpleDateFormat")
    private static FormatterApi<?> createFormatter(String subType, String formatPattern, Boolean nullable, Integer maxStringLength) {
        FormatterApi<?> formatter = null;

        if (nullable == null) nullable = true;

        if (subType != null) {
            if (subType.compareToIgnoreCase(Integer.class.getSimpleName()) == 0) {
                formatter = new IntegerFormatter(nullable);
            } else if (subType.compareToIgnoreCase(Long.class.getSimpleName()) == 0) {
                formatter = new LongFormatter(nullable);
            } else if (subType.compareToIgnoreCase(Boolean.class.getSimpleName()) == 0 && formatPattern != null) {
                String[] trueFalse = formatPattern.split("\\|");
                if (trueFalse.length >= 2) {
                    formatter = new BooleanFormatter(trueFalse[0], trueFalse[1], nullable);
                }
            } else if (subType.compareToIgnoreCase(Date.class.getSimpleName()) == 0 && formatPattern != null) {
                DateFormat parser;

                if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_DATE) == 0) {
                    parser = SimpleDateFormat.getDateInstance();
                } else if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_TIME) == 0) {
                    parser = SimpleDateFormat.getTimeInstance();
                } else if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_DATE_TIME) == 0) {
                    parser = SimpleDateFormat.getDateTimeInstance();
                } else {
                    parser = new SimpleDateFormat(formatPattern);
                }

                formatter = new DateFormatter(formatPattern, parser, nullable);
            }
        }

        if (formatter == null) {
            // assume String
            if (maxStringLength == null) maxStringLength = -1;
            formatter = new StringFormatter(nullable, maxStringLength);
        }
        return formatter;
    }
}
