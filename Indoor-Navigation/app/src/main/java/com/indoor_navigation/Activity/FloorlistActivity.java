package com.indoor_navigation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.indoor_navigation.R;
import com.indoor_navigation.model.Point;


/**
 * Created by 志豪 on 2015/5/28.
 * 用于选择楼层的ListView
 */
public class FloorlistActivity extends ActionBarActivity {
    private Point point = new Point();
    private String[] floorList = {"B1,地铁、地下停车场入站口",
            "F1,到达大厅","F2,出发大厅"};
    protected void onCreate(Bundle savedInstanceState){
         super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointlist);

        //ListView显示不同的楼层
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                FloorlistActivity.this, android.R.layout.simple_list_item_1,floorList);


        ListView listView = (ListView) findViewById(R.id.point_list);
        listView.setAdapter(adapter);
        //点击不同的楼层后，得到当前楼层的坐标信息列表
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(FloorlistActivity.this,
                        PointlistActivity.class);
                intent.putExtra("floor",position);
                startActivityForResult(intent   ,1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                //将选择的点返回给上一个activity
                if(resultCode == RESULT_OK){
                    point = (Point)data.getSerializableExtra("point");
                    Intent intent = new Intent();
                    intent.putExtra("point",point);
                    setResult(RESULT_OK, intent);
                    finish();
                }
        }
    }
}
