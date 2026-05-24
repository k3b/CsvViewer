package de.k3b.android.csvviewer.configuration;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import de.k3b.android.csvviewer.data.analyser.TableModelUtilsTest;
import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;

public class FilterConfigurationInterpreterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableModelUtilsTest.class);

    /** integration test to parse csv, analyse and sort */
    @Test
    public void csv2html_isCorrect() throws IOException {
        TableModelApi model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(DemoData.demoCsv);
            TableModelUtils.analyse(model, 0);
            TableModelUtils.convertColumns(model,true);

            TableModelUtils.printDebug2Console("converted model", model);
/*
            AnalyserReport reportOfReport = TableModelUtils.analyse(report, 0);
            reportOfReport.sortBy(List.of(AnalyserReport.DomainColumnModel.col_subParser));
            TableModelUtils.printDebug2Console("Analyse report of Analyse report  sorted by subParser", (TableModelApi) reportOfReport);

            ConfigurationModel config = TableModelUtils.toConfigurationModel(model);
            TableModelUtils.printDebug2Console("model config", config);

 */
        } catch (Exception e) {
            LOGGER.error("csv2html_isCorrect exception", e);
            throw new RuntimeException(e);
        }
    }
}
