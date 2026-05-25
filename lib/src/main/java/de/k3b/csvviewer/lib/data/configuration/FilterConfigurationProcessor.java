package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@link ConfigurationModel} interpreter for filters based on {@link TableModelRowFilterBase}  */
public class FilterConfigurationProcessor extends ConfigurationProcessorBase<@Nullable List<TableModelRowFilterBase>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public FilterConfigurationProcessor(@NonNull ConfigurationModel target, @NonNull String configurationType) {
        super(target, configurationType);
    }

    /**
     * convert from T configValue in {@link #configurationModel} to Object[] configRow-s for {@link ConfigurationModel}
     *
     * @param configValue
     */
    @Override
    public void addConfig(@Nullable List<TableModelRowFilterBase> configValue) {

    }

    /**
     * apply configuration of one column {columnNumber} from configRow to targetModel
     *
     * @param targetModel
     * @param columnNumber
     * @param configRow
     */
    @Override
    protected void applyConfiguration(@NonNull InMemoryTableModel targetModel, int columnNumber, @Nullable Object @NonNull [] configRow) {

    }
}
