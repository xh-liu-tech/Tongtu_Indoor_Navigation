package com.indoor_navigation.Activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.indoor_navigation.R;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends ActionBarActivity {

    private Button mNavigationBtn;
    private Button mSettingBtn;
    private Button mBrowserMapBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //导航按钮
        mNavigationBtn = (Button)findViewById(R.id.navigation_btn);
        mNavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, NavActivity.class);
                startActivity(intent);
            }
        });

        //浏览地图按钮
        mBrowserMapBtn = (Button)findViewById(R.id.map_btn);
        mBrowserMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BrowserActivity.class);
                startActivity(intent);
            }
        });
        //设置按钮
        mSettingBtn = (Button)findViewById(R.id.location_interface_btn);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this).setTitle("请输入定位接口地址")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        FileOutputStream out = null;
                                        BufferedWriter writer = null;
                                        //储存得到的URL地址到data中
                                        try {
                                            out = openFileOutput("LocationServerURL", Context.MODE_PRIVATE);
                                            writer = new BufferedWriter(new OutputStreamWriter(out));
                                            writer.write(editText.getText().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            try{
                                                if(writer != null){
                                                    writer.close();
                                                }
                                            }catch (IOException e){
                                                e.printStackTrace();
                                            }
                                        }
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
            }
        });

    }


}
