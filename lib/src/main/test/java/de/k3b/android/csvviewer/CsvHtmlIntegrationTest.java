package de.k3b.android.csvviewer;

import org.junit.Test;

import java.io.IOException;

import de.k3b.android.csvviewer.csv.Csv2TableModel;
import de.k3b.android.csvviewer.csv.TableModel2Html;
import de.k3b.android.csvviewer.data.TableModelApi;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CsvHtmlIntegrationTest {
    String exampleCsv = "# some comment\n" +
            "name,greeting\n" +
            "peter,hello peter\n" +
            "susi,hello susi\n";

    @Test
    public void csv2html_isCorrect() throws IOException {
        TableModelApi model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(exampleCsv);
            String html = TableModel2Html.toHtmlTable(model, null, null, 0);
            System.out.println(html);
            // assertEquals(4, 2 + 2);
        }
    }
}