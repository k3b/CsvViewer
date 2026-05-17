package de.k3b.android.csvviewer.data.analyser;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.CsvConfig;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.csv.TableModel2Csv;
import de.k3b.csvviewer.lib.data.TableModelApi;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.TableModelUtils;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel;

public class TableModelUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TableModelUtilsTest.class);

    /** integration test to parse csv, analyse and sort */
    @Test
    public void csv2html_isCorrect() throws IOException {
        TableModelApi model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(DemoData.demoCsv);

            print("model", model);

            AnalyserReport report = TableModelUtils.analyse(model, 0);

            print("Analyse report", report);

            AnalyserReport reportOfReport = TableModelUtils.analyse(report, 0);
            reportOfReport.sortBy(List.of(AnalyserReport.ColumnDefinition.col_subParser));
            print("Analyse report of Analyse report  sorted by subParser", reportOfReport);

            ConfigurationModel config = TableModelUtils.toConfigurationModel(model);
            print("model config", config);
        } catch (Exception e) {
            LOGGER.error("csv2html_isCorrect exception", e);
            throw new RuntimeException(e);
        }
    }

    private static void print(String header, TableModelApi model) throws Exception {
        StringWriter resultWriter = new StringWriter();
        TableModel2Csv.write(resultWriter, CsvConfig.DEFAULT, model);
        System.out.println(header);
        System.out.println(resultWriter);
        System.out.println("------------------");
    }
}
