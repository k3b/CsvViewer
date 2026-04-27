package de.k3b.android.csvviewer;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CsvHtmlViewerActivity extends AppCompatActivity {

    @Override
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_csv_html_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WebView webView = findViewById(R.id.webView);
        String unencodedHtml =
                "<html><body><a href='app://event/header?col=42'>Click Me</a> </body></html>";
        /*
        String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(),
                Base64.NO_PADDING);
        webView.loadData(encodedHtml, "text/html", "base64");
        // "text/html; charset=utf-8", null

         */
        webView.loadData(unencodedHtml, "text/html; charset=utf-8", null);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // requires api Build.VERSION_CODES.LOLLIPOP=21
                Uri uri = request.getUrl();

                if (uri != null) {
                    String col = uri.getQueryParameter("col");

                    if ("app".equals(uri.getScheme())) {
                        handleEvent(uri);
                        return true; // stop WebView from loading
                    }
                }
                return false;
            }
        });

    }

    private void handleEvent(Uri uri) {
        if ("/header".equals(uri.getPath())) {
// your Java logic here
            Toast.makeText(this, "Link clicked", Toast.LENGTH_SHORT).show();
        }
    }

}