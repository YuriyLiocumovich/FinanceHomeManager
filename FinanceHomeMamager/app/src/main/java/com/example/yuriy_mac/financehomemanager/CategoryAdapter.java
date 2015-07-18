package com.example.yuriy_mac.financehomemanager;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList itemsAarrayList;
    private int resource;

    public CategoryAdapter(Context context, int txtViewResourceId, ArrayList objects) {
        // TODO Auto-generated constructor stub
        super(context, txtViewResourceId, objects);
        this.context = context;
        this.itemsAarrayList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return viewItem(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return viewItem(position, convertView, parent);
    }

    private View viewItem(int position, View convertView, ViewGroup parent){
        View row = convertView;
        if(row == null){
            // 1. Create inflater
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            // 2. Get rowView from inflater
            row = inflater.inflate(R.layout.layout_category, parent, false);
        }
        Category category = (Category)itemsAarrayList.get(position);
        if(category != null){
            // 3. Get the objects from the rowView
            TextView lblCategory = (TextView)row.findViewById(R.id.lblCategory);
            ImageView imgBtnCategory = (ImageView)row.findViewById(R.id.imgBtnCategory);
            // 4. Set the data to the objects
            lblCategory.setText(category.getCategoryText());
            switch(category.getCategoryType()){
                case CREDIT:
                    imgBtnCategory.setImageResource(R.drawable.minus);
                    break;
                case DEBIT:
                    imgBtnCategory.setImageResource(R.drawable.plus);
                    break;
            }
        }
        return row;
    }
}

