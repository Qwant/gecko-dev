package com.qwant.liberty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import org.mozilla.gecko.BrowserApp;
import org.mozilla.gecko.R;
import org.mozilla.gecko.db.BrowserContract;


public class Assist extends Activity {
    private static final String LOGTAG = "QwantAssist";

    AutoCompleteTextView search_text;
    WebView webview;
    SuggestAdapter suggest_adapter;
    Intent new_tab_intent;

    @SuppressLint("SetJavaScriptEnabled")
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qwant_widget_main);

        search_text = findViewById(R.id.search_text);

        suggest_adapter = new SuggestAdapter(this, R.layout.qwant_widget_suggestlist_item);

        new_tab_intent = new Intent(this, BrowserApp.class);
        new_tab_intent.setPackage(getPackageName());
        new_tab_intent.setAction(Intent.ACTION_VIEW);
        new_tab_intent.putExtra(BrowserContract.SKIP_TAB_QUEUE_FLAG, true);

        webview = findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String host = uri.getHost();
                if (host != null && host.contains("qwant.com")) {
                    return false;
                }
                new_tab_intent.setData(uri);
                startActivity(new_tab_intent);
                return true;
            }
        });

        ImageView cancel_cross = findViewById(R.id.widget_search_bar_cross);
        cancel_cross.setVisibility(View.INVISIBLE);
        cancel_cross.setOnClickListener((e) -> {
            search_text.setText("");
            search_text.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        });

        ImageView img_magnifier = findViewById(R.id.widget_search_bar_magnifier);
        img_magnifier.setOnClickListener((e) -> launch_search());

        search_text.setAdapter(suggest_adapter);
        search_text.setDropDownBackgroundResource(R.drawable.white_rectangle);
        search_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                launch_search();
                return true;
            }
            return false;
        });

        search_text.setOnItemClickListener((adapterView, view, i, l) -> launch_search());
        search_text.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cancel_cross.setVisibility((count > 0) ? View.VISIBLE : View.INVISIBLE);
            }
        });
        search_text.requestFocus();
    }

    void launch_search() {
        webview.loadUrl("https://www.qwant.com/?widget=1&q=" + search_text.getText().toString());
        search_text.dismissDropDown();
        // Force hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
    }
}
