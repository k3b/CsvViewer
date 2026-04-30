package de.k3b.android.csvviewer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;

import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.TableModel2Html;
import de.k3b.csvviewer.lib.data.TableModelApi;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    String exampleCsv = "# some comment\n" +
            "name,greeting\n" +
            "peter,hello peter\n" +
            "susi,hello susi\n";

    @Test
    public void calculate_isCorrect() throws IOException {
        assertEquals(4, 2 + 2);
   }
}