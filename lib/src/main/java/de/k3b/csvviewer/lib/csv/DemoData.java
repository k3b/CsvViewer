package de.k3b.csvviewer.lib.csv;

/** simple csv test data */
public class DemoData {
    public static final String demoCsvName = "DemoData";
    public static final String demoCsv = "# some comment\n" +
            // ' is optional string delimiter
            "'name',greeting,html,birthday\n" +
            "peter,hello peter,0,1970-01-07\n" +
            "susi,hello susi,0,2001-12-25\n" +
            // is not a comment because surrounded by delimiter
            "' #world#',hello #world#,0\n" +
            // html - escaping
            "<b>nobody</b>,hello <b>nobody</b>,1\n" +
            ",hello ,0\n";
}
