package de.k3b.android.csvviewer.lib.data;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.configuration.FormatterConfigurationProcessor;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.filter.TableModelColumnFilter;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel;
import de.k3b.csvviewer.lib.data.model.TableProperties;

public class TableModelUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableModelUtilsTest.class);

    /** integration test to parse csv, analyse and sort */
    @Test
    public void analyserReport() throws IOException {
        InMemoryTableModel model = getDemoModel(DemoData.demoCsvName);
        TableModelUtils.printDebug2Console("model", model);

        AnalyserReport report = TableModelUtils.analyse(model, 0);

        TableModelUtils.printDebug2Console("Analyse report", report);

        AnalyserReport reportOfReport = TableModelUtils.analyse(report, 0);
        reportOfReport.sortBy(List.of(AnalyserReport.DomainColumnModel.col_subParser));
        TableModelUtils.printDebug2Console("Analyse report of Analyse report  sorted by subParser", reportOfReport);

    }

    /** integration test to parse csv, analyse and sort */
    @Test
    public void sorting() throws IOException {
        InMemoryTableModel model = getDemoModel(DemoData.demoCsvName);
        AnalyserReport report = TableModelUtils.analyse(model, 0);

        TableModelUtils.printDebug2Console("Analyse report", report);

        AnalyserReport reportOfReport = TableModelUtils.analyse(report, 0);
        TableProperties.setColumnFormatter(reportOfReport,AnalyserReport.DomainColumnModel.col_subParser, TableColumnType.String.getFormatter());
        reportOfReport.sortBy(List.of(AnalyserReport.DomainColumnModel.col_subParser));
        TableModelUtils.printDebug2Console("Analyse report of Analyse report  sorted by subParser", reportOfReport);

    }

    /** treat loaded demo model via analyser and apply config to reloaded demo model
     * via FormatterConfigurationProcessor. */
    @Test
    public void filtering() throws IOException {
        InMemoryTableModel model = getDemoModel(DemoData.demoCsvName);
        TableModelUtils.printDebug2Console(" before filtering", model);

        String expression = "name=peter";
        TableModelColumnFilter filter = TableModelColumnFilter.create(1, TableColumnType.String.getFormatter(),
                expression);

        InMemoryTableModel filteredModel = TableModelUtils.filter(model, filter);
        TableModelUtils.printDebug2Console(" after filtering", filteredModel);

    }
    /** treat loaded demo model via analyser and apply config to reloaded demo model
     * via FormatterConfigurationProcessor. */
    @Test
    public void formatterConfigIntegrationTest() throws IOException {
        InMemoryTableModel model = getDemoModel(DemoData.demoCsvName+"-beeingAnalysed");
        TableModelUtils.analyse(model, 0);
        TableModelUtils.convertColumns(model,true);

        TableModelUtils.printDebug2Console("converted model", model);

        ConfigurationModel config = TableModelUtils.toConfigurationModel(model);

        TableModelUtils.printDebug2Console("config model", config);

        InMemoryTableModel modelViaConfig = getDemoModel(DemoData.demoCsvName+"-fromConfig");

        FormatterConfigurationProcessor processor = new FormatterConfigurationProcessor(config);
        processor.applyConfiguration(modelViaConfig);
        TableModelUtils.convertColumns(modelViaConfig, true);
        TableModelUtils.printDebug2Console("model from config", modelViaConfig);
    }

    private InMemoryTableModel getDemoModel(String name) {
        InMemoryTableModel model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(name, DemoData.demoCsv);
        } catch (Exception e) {
            LOGGER.error("exception in getDemoModel", e);
            throw new RuntimeException(e);
        }
        return model;
    }

}
