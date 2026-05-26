// how to implement an android java eventhandler that is connected to a link inside a android html view

### api

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
* 
TableRowEditorActivity
* ? started from TableActivity

ConfigurationModel (s=save, r=read)
* s FormatterConfigurationInterpreter
* > FilterConfigurationInterpreter
* ? SorterConfigurationInterpreter  

	

### Android gui

v sort via header
- todo filter
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
