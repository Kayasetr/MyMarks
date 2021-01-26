package com.firebase.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MarksAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Marks> arrayList;
    private TextView subjOut, markOut;
    public MarksAdapter(Context context, ArrayList<Marks> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false);
        subjOut = convertView.findViewById(R.id.subjOutList);
        markOut = convertView.findViewById(R.id.markOutList);
        subjOut.setText(arrayList.get(position).getSubject());
        markOut.setText(arrayList.get(position).getMark().toString());
        return convertView;
    }
}
