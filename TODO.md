### next
* on rotattion dataloss
* App with title, menu, quicksearch

### api

Build: 
	requires android17
	add gradle plugin https://github.com/uber/NullAway to detect JSpecify errors
	non android: 
		https://github.com/uber/NullAway#gradle example https://github.com/uber/NullAway/blob/master/sample-app/build.gradle 
		must use old version  Alternately, 2.x versions
	

---------

> ColumnModel
* Domain (or logical) ColumnModel is defined by the Domain (i.e. AnalyserReport, ConfigurationModel)
* CSV (or Physical) ColumnModel: is defined by header of the physical Csv file (there may ignored (not in Domain ColumnModel) or missing columns)
* Gui (or View) ColumnModel: is defined by the display (columns may be hidden or in different order. There may be virtual or calculated columns)

TableModelUtils
* analyse
* convertColumns
* filter+sort

TableModelBase-class 

InMemoryTableModel
* sorter

ObjectFormatter
* ReadOnlyObjectFormatter
* Add support for (non-)Nullable
* DateFormatterEx
    * parser mit suffix, wenn zu kurz
    * format ohne time wenn time==null
* > TODO !!! DoubleFormat mit verschiedenen patterns zum probieren
***** DoubleAnalyser analog zu DateAnalyser mit verschiedenen Formaten  


String text = "1.234,56"; // deutsches Format

Locale.US
NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

try {
Number number = nf.parse(text);
double value = number.doubleValue();
System.out.println(value); // 1234.56
} catch (ParseException e) {
e.printStackTrace();
}
----
double valDe = NumberFormat.getInstance(Locale.GERMANY).parse(de).doubleValue();
// java.text.NumberFormat#getInstance
-----

String de = "1.234,56";
String us = "1,234.56";

double valDe = NumberFormat.getInstance(Locale.GERMANY).parse(de).doubleValue();
double valUs = NumberFormat.getInstance(Locale.US).parse(us).doubleValue();

----

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
DecimalFormat df = new DecimalFormat("#,##0.###", symbols);

double value = df.parse("1.234,56").doubleValue();
System.out.println(value);

-----

license-report
* Display ...apk\assets\open_source_licenses.csv
* example gradlew.bat csv_viewer_android_app:copyLicenseMinApi21Debug

TableRowEditorActivity
* ? started from TableActivity

ConfigurationModel (s=save, r=read)
* s FormatterConfigurationInterpreter
* > FilterConfigurationInterpreter
* ? SorterConfigurationInterpreter  

	

### Android gui

v sort via header
v filter via cell contextmenu
- save when rotation

### html gui
app://event/header?col=42

WebView webView = findViewById(R.id.webView);

webView.setWebViewClient(new WebViewClient() {
@Override
public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
Uri uri = request.getUrl();
String col = uri.getQueryParameter("col");

        if ("app".equals(uri.getScheme())) {
            handleEvent(uri);
            return true; // stop WebView from loading
        }
        return false;
    }
});

private void handleEvent(Uri uri) {
if ("/header".equals(uri.getPath())) {
// your Java logic here
Toast.makeText(this, "Link clicked", Toast.LENGTH_SHORT).show();
}
}


