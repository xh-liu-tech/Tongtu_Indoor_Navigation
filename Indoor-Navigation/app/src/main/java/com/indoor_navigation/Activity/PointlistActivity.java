package com.indoor_navigation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.indoor_navigation.R;
import com.indoor_navigation.adapter.PointAdapter;
import com.indoor_navigation.model.Point;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 志豪 on 2015/5/20.
 * 生成选择导航地点的ListView
 */
public class PointlistActivity extends ActionBarActivity {
    private List<Point> pointList = new ArrayList<Point>();


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointlist);
        //获取楼层的号码和得到不同楼层的坐标信息
        int floor = getIntent().getIntExtra("floor",0);
        getPoints(floor);

        PointAdapter adapter = new PointAdapter(PointlistActivity.this,
                R.layout.point_item,pointList);
        ListView listView = (ListView) findViewById(R.id.point_list);
        listView.setAdapter(adapter);
        //设置监听器，点击则返回地点信息。
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Point point = pointList.get(position);
                Intent intent = new Intent();
                intent.putExtra("point",point);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void getPoints(int floor){
        String pointFilename = "";
        //根据楼层不同，读取不同的楼层坐标信息
        switch (floor)
        {
            case 0: pointFilename = "B1POI.json";break;
            case 1: pointFilename = "F1POI.json";break;
            case 2: pointFilename = "F2POI.json";break;
            default:
                pointFilename = "underground.txt";
        }
        //使用Gson读取json文件中的信息
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getAssets()
                    .open(pointFilename), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            String jsonData = stringBuilder.toString();

            Gson gson = new Gson();
            pointList = gson.fromJson(jsonData, new
                    TypeToken<List<Point>>() {
                    }.getType());


            //设置点的origin属性为0说明地点信息来源于文件
            for(int i=0;i < pointList.size();i++)
            {
                pointList.get(i).setOrigin(0);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
