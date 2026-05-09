package de.k3b.android.csvviewer.data.analyser;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.CsvConfig;
import de.k3b.csvviewer.lib.csv.DemoData;
import de.k3b.csvviewer.lib.csv.TableModel2Csv;
import de.k3b.csvviewer.lib.data.TableModelApi;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.analyser.TableModelAnalyzer;

public class TableModelAnalyzerTest {
    @Test
    public void csv2html_isCorrect() throws IOException {
        TableModelApi model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(DemoData.demoCsv);

            AnalyserReport report = TableModelAnalyzer.analyse(model, 0);

            StringWriter resultWriter = new StringWriter();
            TableModel2Csv.write(resultWriter, CsvConfig.DEFAULT, report);
            System.out.println(resultWriter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
