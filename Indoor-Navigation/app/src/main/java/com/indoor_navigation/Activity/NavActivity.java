package com.indoor_navigation.Activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.indoor_navigation.R;
import com.indoor_navigation.adapter.PointAdapter;
import com.indoor_navigation.model.Point;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by zh.wen on 2015/7/21.
 *    若起点是我的位置，即通过定位接口得到的地点信息，则进行导航，有导航纠正功能
 *    否则进行路径规划,导航时路径纠偏功能关闭。
 */
public class NavActivity extends ActionBarActivity{
    private Button mBackBtn;
    private Button mNavigateBtn;
    private Button mCamaraBtn;
    private Button mMicBtn;
    private Button mLocateBtn;
    private Button mAddPointBtn;
    private ListView mListView;
    private WebView  mPointSetWebView;
    private PointAdapter pointadapter;
    private ArrayList<Point>  chosePointList = new ArrayList<Point>();
    private int item_position = 0;
    public static final int DATA_CHANGE = 1;
    private String  GeolocationUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_nav);

        //地点列表
        pointadapter = new PointAdapter(this,
                R.layout.point_item,chosePointList);
        mListView = (ListView) findViewById(R.id.chosepoint_list);
        mListView.setAdapter(pointadapter);

        //设置listview单击修改地点
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NavActivity.this, FloorlistActivity.class);
                item_position = position;
                startActivityForResult(intent, 2);
            }
        });

        //设置listview长按删除
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(NavActivity.this).setTitle("是否删除该地点")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(2 == chosePointList.get(position).getOrigin()){
                                            String index = chosePointList.get(position).getId();
                                            String floor = chosePointList.get(position).getZ();
                                            mPointSetWebView.loadUrl("javascript: deleteMarker('"+floor+"','" + index + "')");
                                        }
                                        chosePointList.remove(position);
                                        pointadapter.notifyDataSetChanged();

                                    }
                                }
                        )
                        .
                                setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }

                                ).

                        show();
                return true;
            }
        });

        //导航地点添加按钮
        mAddPointBtn = (Button) findViewById(R.id.addpoint_btn);
        mAddPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavActivity.this, FloorlistActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        //自动定位按钮
        mLocateBtn = (Button) findViewById(R.id.locate_btn);
        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetLocationWithHttpURLConnection();
            }
        });

        //设置返回按钮
        mBackBtn = (Button)findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //设置导航按钮
        mNavigateBtn = (Button)findViewById(R.id.go_btn);
        mNavigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavActivity.this, MapActivity.class);
                intent.putExtra("chosepointlist", chosePointList);

                //若起点是我的位置，即通过定位接口得到的地点信息，则进行导航，有导航纠正功能
                //否则进行路径规划,导航时路径纠偏功能关闭。
                Boolean IsNavigation;
                if(chosePointList.get(0).getOrigin()==1){
                    IsNavigation = true;
                }else {
                    IsNavigation = false;
                }
                intent.putExtra("isnavigation",IsNavigation);
                startActivity(intent);
            }
        });



        GeolocationUrl = getURL();
        //PointSetWebView，用于地图选点
        mPointSetWebView = (WebView)findViewById(R.id.pointSetWebView);
        WebSettings webSettings = mPointSetWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //在js中调用本地java方法
        mPointSetWebView.addJavascriptInterface(new JsInterface(this), "AndroidWebView");

        //添加客户端支持
        mPointSetWebView.setWebChromeClient(new WebChromeClient());
        mPointSetWebView.loadUrl("file:///android_asset/WebMap/select-point-map/select-point-map.html");
        mPointSetWebView.setWebViewClient(new WebViewClient() {

                                         @Override
                                         public void onPageFinished(WebView view, String url) {
                                             super.onPageFinished(view, url);
                                             mPointSetWebView.loadUrl("javascript: setGeolocationUrl('" + GeolocationUrl + "')");
                                         }
                                     }
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                //将选择的点返回给上一个activity
                if(resultCode == RESULT_OK){
                    Point point = (Point)data.getSerializableExtra("point");
                    chosePointList.add(point);
                    pointadapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    Point point = (Point)data.getSerializableExtra("point");
                    chosePointList.set(item_position,point);
                    pointadapter.notifyDataSetChanged();
                }
                break;
        }
    }

    //Handler用于list更改后，更改ListView
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DATA_CHANGE:
                    pointadapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private void GetLocationWithHttpURLConnection(){
        //读取从定位接口得到的json格式数据，并修改起点信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                String UrlString;
                //读取URL地址
                try {
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

                    UrlString = content.toString();
                    //使用HttpClient方式得到接口数据
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpGet httpGet = new HttpGet(UrlString);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if(httpResponse.getStatusLine().getStatusCode() == 200){
                        //请求和相应都成功了
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        //处理json格式数据
                        GetLocationInfo(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }

    private void GetLocationInfo(String jsonData){
        //读取从定位接口得到的json格式数据，并修改起点信息
        try{
            JSONArray jsonArray = new JSONArray(jsonData);
            double x =  (520.0 * jsonArray.getDouble(0));
            double y =  (520.0 * jsonArray.getDouble(1));
            int z = jsonArray.getInt(2);
            Point point = new Point();
            point.setName("我的位置");
            point.setX(Double.toString(x));
            point.setY(Double.toString(y));
            point.setZ(Integer.toString(z));
            point.setOrigin(1);       //设置origin属性为1，说明地点信息来源于定位接口
            chosePointList.add(point);
            Message message = new Message();
            message.what = DATA_CHANGE;
            handler.sendMessage(message);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private class JsInterface {
        private Context mContext;

        public JsInterface(Context context) {
            this.mContext = context;
        }

        //在js中调用window.AndroidWebView.addPointByTouchMap(name)，便会触发此方法。
        @JavascriptInterface
        public void addPointByTouchMap(String index,String x,String y, String z)
        {
            Point point = new Point();
            point.setName("地图选点" + index);
            point.setId(index);
            point.setOrigin(2);  //设置点的origin为2,说明地点信息来源于地图取点
            point.setX(x);
            point.setY(y);
            point.setZ(z);
            chosePointList.add(point);
            Message message = new Message();
            message.what = DATA_CHANGE;
            handler.sendMessage(message);
        }
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
