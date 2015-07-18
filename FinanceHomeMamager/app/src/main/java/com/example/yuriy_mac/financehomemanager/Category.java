package com.example.yuriy_mac.financehomemanager;

import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

public class Category {
    private ImageButton imgButton;
    private TextView lblCategory;
    private CategoryType type;
    private long id;

    public static enum CategoryType{
        DEBIT, CREDIT;
    }

    public Category(Context context,String lblCategoryText, CategoryType categoryType){
        lblCategory = new TextView(context);
        lblCategory.setText(lblCategoryText);
        imgButton = new ImageButton(context);
        type = categoryType;
    }

    public Category(Context context,String lblCategoryText, CategoryType categoryType, long categoryID){
        this(context,lblCategoryText,categoryType);
        id = categoryID;
    }

    public String getCategoryText(){
        return lblCategory.getText().toString();
    }

    public CategoryType getCategoryType(){
        return type;
    }

    public long getCategoryID(){ return id; }

    public void setCategoryID(long categoryID) { id = categoryID; }
}
