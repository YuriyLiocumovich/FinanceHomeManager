package com.example.yuriy_mac.financehomemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class AddButton extends Button {

    public AddButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setAddButton();
    }

    private void setAddButton(){
        setBackground(getResources().getDrawable(R.drawable.button_back));
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.plus, 0);
    }
}
