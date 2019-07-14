package com.indoor_navigation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.indoor_navigation.R;
import com.indoor_navigation.model.Point;

import java.util.List;


/**
 * Created by 志豪 on 2015/5/19.
 * 用于生成列表选点ListView的Adapter
 */
public class PointAdapter extends ArrayAdapter<Point> {
    private int resourceId;

    public PointAdapter(Context context, int textViewResourceId,
                        List<Point> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Point point = getItem(position);
        View view;
        if(convertView == null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        }else {
            view = convertView;
        }
        //设置当前坐标的名称
        TextView pointName = (TextView) view.findViewById(R.id.point_name);
        pointName.setText(point.getName());
        return view;
    }
}
