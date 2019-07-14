package com.indoor_navigation.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.indoor_navigation.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by 志豪 on 2015/7/23.
 */
public class BrowserActivity extends ActionBarActivity {

    private WebView mBrowserMapWebView;
    private String GeolocationUrl =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsermap);

        //获得URL
        GeolocationUrl = getURL();
        mBrowserMapWebView = (WebView)findViewById(R.id.browsermap_webview);
        WebSettings websettings = mBrowserMapWebView.getSettings();
        websettings.setJavaScriptEnabled(true);

        mBrowserMapWebView.loadUrl("file:///android_asset/WebMap/browse-map/browse-map.html");
        mBrowserMapWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    mBrowserMapWebView.loadUrl("javascript: setGeolocationUrl('" + GeolocationUrl + "')");
                }
            }
        );

    }

    public String getURL(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = openFileInput("LocationServerURL");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) !=null){
                content.append(line);
            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                if(reader != null){
                    reader.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return content.toString();
    }
}
