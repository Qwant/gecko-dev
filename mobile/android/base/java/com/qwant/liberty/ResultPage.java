package com.qwant.liberty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import org.mozilla.gecko.R;

public class ResultPage extends Activity {
    WebView webview;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwant_widget_result);

        webview = findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);

        webview.loadUrl("https://www.qwant.com/?q=" + this.getIntent().getStringExtra("search_string"));
    }
}
