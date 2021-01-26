package com.firebase.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> array1List;
    private TextView studOutList;
    public StudentAdapter(Context context, ArrayList<String> array1List) {
        this.context = context;
        this.array1List = array1List;
    }
    @Override
    public int getCount() {
        return array1List.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_students, parent, false);
        studOutList = convertView.findViewById(R.id.studentList);
        studOutList.setText(" " + array1List.get(position));
        return convertView;
    }
}
