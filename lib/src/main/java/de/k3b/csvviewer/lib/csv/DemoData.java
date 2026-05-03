package de.k3b.csvviewer.lib.csv;

/** simple csv test data */
public class DemoData {
    public static final String demoCsv = "# some comment\n" +
            // ' is optional string delimiter
            "'name',greeting\n" +
            "peter,hello peter\n" +
            "susi,hello susi\n" +
            // is not a comment because surrounded by delimiter
            "' #world#',hello #world#\n" +
            // html - escaping
            "<b>nobody</b>,hello <b>nobody</b>\n" +
            ",hello \n";
}
