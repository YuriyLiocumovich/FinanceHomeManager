package com.example.yuriy_mac.financehomemanager;


import java.text.ParseException;
import java.util.Calendar;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

public class ChangeDateFragment extends DialogFragment {

    String title;
    ChangeDateFragmentListener listener;

    public ChangeDateFragment(Context context, int title, ChangeDateFragmentListener listener) {
        this.title =  context.getResources().getString(title);
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        final View view = inflater.inflate(R.layout.fragment_change_date, container);
        Button btn = (Button)view.findViewById(R.id.btnChangeDateSave);
        btn.setOnClickListener(new OnClickListener() {
            DatePicker datePicker = (DatePicker)view.findViewById(R.id.dpChangeDate);
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DATE, datePicker.getDayOfMonth());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.YEAR, datePicker.getYear());
                try {
                    listener.onFinishChangeDateDialog(Tools.getDate(calendar.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(title);
        return view;
    }

    public interface ChangeDateFragmentListener {
        void onFinishChangeDateDialog(String inputString) throws ParseException;
    }
}

