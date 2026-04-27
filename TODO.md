// how to implement an android java eventhandler that is connected to a link inside a android html view

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
