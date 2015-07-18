package com.example.yuriy_mac.financehomemanager;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddCredCardFragment extends DialogFragment {

    AddCredCardFragmentListener listener;
    String title;
    EditText ccType;
    EditText ccToken;

    public AddCredCardFragment(Context context,String title,AddCredCardFragmentListener listener){
        this.listener = listener;
        this.title = title;
    }

    public AddCredCardFragment(Context context, int title,AddCredCardFragmentListener listener){
        this(context, context.getResources().getString(title), listener);
    }

    public interface AddCredCardFragmentListener{
        void OnFinishedAddCredCardFragment(CreditCard creditCard);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_credcard, container);
        ccType = (EditText)view.findViewById(R.id.txtAddCCType);
        ccToken = (EditText)view.findViewById(R.id.txtAddCCToken);

        Button btnSave = (Button)view.findViewById(R.id.btnAddCategorySave);
        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CreditCard creditCard = new CreditCard(ccType.getText().toString(),ccToken.getText().toString());
                listener.OnFinishedAddCredCardFragment(creditCard);
                dismiss();
            }
        });
        ccType.requestLayout();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(title);
        return view;
    }
}

