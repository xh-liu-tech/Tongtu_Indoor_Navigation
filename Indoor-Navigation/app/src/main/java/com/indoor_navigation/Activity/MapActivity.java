package com.indoor_navigation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indoor_navigation.R;
import com.indoor_navigation.model.JsonPoint;
import com.indoor_navigation.model.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 志             豪 on 2015/7/23.
 */
public class MapActivity extends ActionBarActivity {
    private ArrayList<Point> chosePointList = null;
    private ArrayList<JsonPoint> jsonPointsList = null;
    private String  jsonString = null;
    private String  GeolocationUrl = null;
    private WebView mMapWebView ;
    private Boolean IsNavigation;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //获取地点列表
        Intent intent = getIntent();
        chosePointList = (ArrayList<Point>) intent.getSerializableExtra("chosepointlist");
        IsNavigation = intent.getBooleanExtra("isnavigation",false);
        Log.e("IsNavigation",IsNavigation.toString());
        //转换地点数据类型
        jsonPointsList = toJsonPoint(chosePointList);
        //获得jsonString
        jsonString = toJsonString(jsonPointsList);
        Log.e("JSON", jsonString);

        GeolocationUrl = getURL();
        //WebView 用于显示地图
        mMapWebView = (WebView)findViewById(R.id.MapWebView);
        WebSettings webSettings = mMapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mMapWebView.loadUrl("file:///android_asset/WebMap/navigation-map/navigation-map.html");
        mMapWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mMapWebView.loadUrl("javascript: setGeolocationUrl('" + GeolocationUrl + "')");
                        mMapWebView.loadUrl("javascript: setEnableRerouting("+ IsNavigation.toString() +")");
                        mMapWebView.loadUrl("javascript: setDestJSON('"+ jsonString +"')");
                        mMapWebView.loadUrl("javascript: startRouting()");
                    }
                }
        );
    }


    public ArrayList<JsonPoint> toJsonPoint(ArrayList<Point> chosePointList){
        ArrayList<JsonPoint> jsonPointslist = new ArrayList<JsonPoint>();
        for (Iterator iterator = chosePointList.iterator(); iterator.hasNext();) {
            Point point = (Point) iterator.next();
            JsonPoint jsonPoint =  new JsonPoint();
            jsonPoint.setX(point.getX());
            jsonPoint.setY(point.getY());
            jsonPoint.setZ(point.getZ());
            jsonPointslist.add(jsonPoint);
        }
        return jsonPointslist;
    }

    public String toJsonString(ArrayList<JsonPoint> jsonPointsList){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<JsonPoint>>(){}.getType();
        String jsonString = gson.toJson(jsonPointsList, listType);
        return jsonString;
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
