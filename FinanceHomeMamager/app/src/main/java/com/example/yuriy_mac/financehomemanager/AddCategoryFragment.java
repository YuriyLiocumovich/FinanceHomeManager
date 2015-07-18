package com.example.yuriy_mac.financehomemanager;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddCategoryFragment extends DialogFragment {

    AddCategoryFragmentListener listener;
    String title;
    int imgButtonRef;
    EditText txtNewCategory;

    public AddCategoryFragment(Context context, String title, AddCategoryFragmentListener listener){
        this.listener = listener;
        this.title = title;
    }

    public AddCategoryFragment(Context context, int title, AddCategoryFragmentListener listener){
        this(context, context.getResources().getString(title), listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container);
        txtNewCategory = (EditText)view.findViewById(R.id.txtAddCategoryNewCategory);

        ((ImageButton)view.findViewById(R.id.imgMinus)).setOnClickListener(clickListener);
        ((ImageButton)view.findViewById(R.id.imgPlus)).setOnClickListener(clickListener);

        Button btnSave = (Button)view.findViewById(R.id.btnAddCategorySave);
        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                listener.OnFinishedAddCategoryFragment(txtNewCategory.getText() + "," + imgButtonRef);
                dismiss();
            }
        });
        txtNewCategory.requestLayout();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(title);
        return view;
    }

    private OnClickListener clickListener = new OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgPlus:
                    imgButtonRef = R.id.imgPlus;
                    ((ImageButton)v).setImageResource(R.drawable.plus_v);
                    break;
                case R.id.imgMinus:
                    imgButtonRef = R.id.imgMinus;
                    ((ImageButton)v).setImageResource(R.drawable.minus_v);
                    break;
            }
        }
    };

    public interface AddCategoryFragmentListener {
        void OnFinishedAddCategoryFragment(String title);
    }
}

