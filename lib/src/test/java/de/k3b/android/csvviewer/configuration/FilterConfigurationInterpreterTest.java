package de.k3b.android.csvviewer.configuration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import de.k3b.android.csvviewer.data.analyser.TableModelUtilsTest;
import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel;
import de.k3b.csvviewer.lib.data.configuration.FormatterConfigurationProcessor;
import de.k3b.csvviewer.lib.data.formatter.FormatterDefinition;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;

public class FilterConfigurationInterpreterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableModelUtilsTest.class);

    /** integration test to parse csv, analyse and sort */
    @Test
    public void csv2html_isCorrect() throws IOException {
        TableModelApi model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(DemoData.demoCsvName+"-beeingAnalysed", DemoData.demoCsv);
            TableModelUtils.analyse(model, 0);
            TableModelUtils.convertColumns(model,true);

            TableModelUtils.printDebug2Console("converted model", model);

            ConfigurationModel config = TableModelUtils.toConfigurationModel(model);

            TableModelUtils.printDebug2Console("config model", config);

            InMemoryTableModel modelRaw = null;
            try(Csv2TableModel csvRaw = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
                modelRaw = csvRaw.toTableModel(DemoData.demoCsvName+"-fromConfig", DemoData.demoCsv);

                FormatterConfigurationProcessor processor = new FormatterConfigurationProcessor(config);

                FormatterDefinition[] formatters = processor.applyConfiguration(modelRaw);
                TableModelUtils.convertColumns(modelRaw, true);
                TableModelUtils.printDebug2Console("model from config", modelRaw);
            }

        } catch (Exception e) {
            LOGGER.error("csv2html_isCorrect exception", e);
            throw new RuntimeException(e);
        }
    }
}
