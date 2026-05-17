package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel.ColumnDefinition;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.FormatterDefinition;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactory;

/** {@link ConfigurationModel} interpreter for filters based on {@link TableModelRowFilterBase}  */
public class ConfigurationInterpreterTableColumnDefinitionNew extends ConfigurationInterpreterBase<@Nullable FormatterDefinition[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public static final String CONFIGURATION_TYPE = "columnDefinition";
    public ConfigurationInterpreterTableColumnDefinitionNew(@NonNull ConfigurationModel target) {
        super(target, CONFIGURATION_TYPE);
    }

    /** Transfer formatter info from {@link #target} to {@link ConfigurationModel}. */
    @Override
    public void addConfig(FormatterDefinition[] definitions) {
        if (definitions != null) {
            int colCount = definitions.length;
            if (colCount > 0) {
                for (int columnNumber = 0; columnNumber < colCount; columnNumber++) {
                    addConfig(columnNumber, definitions[columnNumber]);
                }
            }
        }
    }

    private void addConfig(int columnNumber, FormatterDefinition formatter) {
        if (formatter != null) {
            String description = formatter.toString();
            String subTyp = formatter.getElementClassName();
            Object formatPattern = formatter.getFormatPattern();
            Object nullAble = formatter.isNullable() ? null : "0";
            Integer maxStringLen = formatter.getMaxStringLength();
            addConfig(columnNumber, subTyp, description, formatPattern, nullAble, maxStringLen);
        }
    }

    /**
     * execute the parse on cnfigRows that match {@link #configurationType}
     *
     * @param myConfigRows
     */
    @Override
    protected @Nullable FormatterDefinition @NonNull [] parseImpl(@NonNull List<Object[]> myConfigRows) {
        FormatterDefinition[] result = new FormatterDefinition[target.getColumnCount()];
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
    private FormatterDefinition parseColumnDefinition(int columnNumber, String columnName, Object[] row) {
        FormatterDefinition result = null;

        FormatterApi<?> formatter = null;

        String subType = (String) row[ColumnDefinition.col_subType];
        String formatPattern = TableColumnType.Boolean.parseImpl(row[ColumnDefinition.col_parameter1]);
        Boolean nullable = TableColumnType.Boolean.parseImpl(row[ColumnDefinition.col_parameter2]);
        Integer maxStringLength = TableColumnType.Integer.parseImpl(row[ColumnDefinition.col_parameter3]);

        formatter = FormatterFactory.createFormatter(subType, formatPattern, nullable, maxStringLength);
        return formatter;
    }

}
