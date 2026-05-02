package de.k3b.android.csvviewer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;

import java.io.IOException;

import de.k3b.csvviewer.lib.csv.Csv2TableModel;
import de.k3b.csvviewer.lib.csv.TableModel2Html;
import de.k3b.csvviewer.lib.data.TableModelApi;

public class CsvHtmlViewerActivity extends AppCompatActivity {

    public static final String LOG_TAG = "CsvHtmlViewerActivity";

    /** last X click position in in web view */
    private float lastX;
    /** last Y click position in in web view */
    private float lastY;

    @NonNull
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (false) {
            /* happend with minsdk=21 and minsdk23
            Caused by: java.lang.NullPointerException: Attempt to invoke virtual method
                'void android.view.View.setTag(int, java.lang.Object)' on a null object reference
                    at androidx.core.view.ViewCompat$Api21Impl.setOnApplyWindowInsetsListener(ViewCompat.java:5201)
                    at androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(ViewCompat.java:2899)
                    at de.k3b.android.csvviewer.CsvHtmlViewerActivity.onCreate(CsvHtmlViewerActivity.java:52)
            */
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        /*
        webView = new WebView(this);
        setContentView(webView);
         */

        setContentView(R.layout.activity_csv_html_viewer);
        webView = findViewById(R.id.webView);

        initWebView(webView);

        String unencodedHtml = null;
        try {
            unencodedHtml = getHtml();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        webView.loadData( unencodedHtml, "text/html", "UTF-8");
    }

    /**
     * See <a href="https://developer.android.com/develop/ui/views/layout/webapps/webview#groovy">...</a> for details
     */
    @SuppressLint({"ClickableViewAccessibility", "ObsoleteSdkInt"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull private WebView initWebView(@NonNull WebView webView) {

        // configure WebView
        final WebSettings settings = webView.getSettings();

        // Fix for "Wrong charset in serbian translations" https://github.com/k3b/LocationMapViewer/issues/5
        // (for android 2.2) see http://stackoverflow.com/questions/4933069/android-webview-with-garbled-utf-8-characters
        settings.setDefaultTextEncodingName("utf-8");
        settings.setBuiltInZoomControls(true);

        webView.setVerticalScrollBarEnabled(true);

        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // requires api Build.VERSION_CODES.LOLLIPOP=21
                Uri uri = request.getUrl();

                if (uri != null) {

                    if ("app".equals(uri.getScheme())) {
                        String row = uri.getQueryParameter("row");
                        String col = uri.getQueryParameter("col");
                        handleEvent(row, col, lastX, lastY);
                        return true; // stop WebView from loading
                    }
                }
                return false;
            }
        });

        // init listeners
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN) {
                    lastX = motionEvent.getX();
                    lastY = motionEvent.getY();
                }
                return false;
            }
        });
        return webView;
    }

    public String getHtml() throws IOException {
        String exampleCsv = "# some comment\n" +
                "name,greeting\n" +
                "peter,hello peter\n" +
                "susi,hello susi\n" +
                "world,hello world\n" +
                "nobody,hello nobody\n" +
                ",hello \n";

        TableModelApi model = null;
        try(Csv2TableModel csv = new Csv2TableModel(Csv2TableModel.OPTION_ALL)) {
            model = csv.toTableModel(exampleCsv);
            String html = TableModel2Html.toHtmlTable(model, null, null, 0);
            Log.i(LOG_TAG, html);
            return html;
        }
    }

    // Source - https://stackoverflow.com/a/31834668
    public void showHeaderMenu(float x, float y)
    {
        WebView webView = findViewById(R.id.webView);
        final ViewGroup root = (ViewGroup) webView.getParent();
        // inspired by https://stackoverflow.com/questions/13444594/how-can-i-change-the-position-of-where-my-popup-menu-pops-up
        // final ViewGroup root = (ViewGroup) getWindow().getDecorView(); // .findViewById(android.R.id.content);

        final View view = new View(this.getBaseContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        view.setBackgroundColor(Color.TRANSPARENT);

        root.addView(view);

        view.setX(x);
        view.setY(y);

        PopupMenu popupMenu = new PopupMenu(this.getBaseContext(), view, Gravity.CENTER);

        popupMenu.getMenuInflater().inflate(R.menu.test,
                popupMenu.getMenu());
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
        {
            @Override
            public void onDismiss(PopupMenu menu)
            {
                root.removeView(view);
            }
        });

        popupMenu.show();
    }

    private void handleEvent(String row, String col, float x, float y) {
        showHeaderMenu(x,y);
            String text = String.format("Link id=%s clicked at (%s,%s)", col, lastX, lastY);
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}