package com.example.yuriy_mac.financehomemanager;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by yuriy_mac on 5/29/15.
 */
public class PaidByCCFragment extends DialogFragment {

    PaidByCCFragmentListener listener;
    String title;
    Bundle args;

    public PaidByCCFragment(Context context, String title, PaidByCCFragmentListener listener){
        this.listener = listener;
        this.title = title;
    }

    public PaidByCCFragment(Context context, int title, PaidByCCFragmentListener listener){
        this(context, context.getResources().getString(title), listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        args = getArguments();
        View view = inflater.inflate(R.layout.fragment_paid_by_cc,container);
        final EditText txtCCTotalSum = (EditText)view.findViewById(R.id.txtCCTotalSum);
        final EditText txtCCNumOfMonth = (EditText)view.findViewById(R.id.txtCCNumOfMonth);
        final EditText txtCCFirstMonth = (EditText)view.findViewById(R.id.txtCCFirstMonth);
        final EditText txtCAdditionalPayments = (EditText)view.findViewById(R.id.txtCAdditionalPayments);

        Button btnSave = (Button)view.findViewById(R.id.btnPaidByCCSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sum sum = new Sum(args.getLong("category"),(args.getInt("isCredit")==0? txtCCTotalSum.getText().toString():txtCCFirstMonth.getText().toString()),args.getString("fromDate"),2,
                        args.getInt("credCardID"),args.getInt("isCredit"));
                sum.getSumCredit().setTotalPaid(Double.valueOf(txtCCTotalSum.getText().toString().isEmpty()?"0":txtCCTotalSum.getText().toString()));
                sum.getSumCredit().setNumberOfMonths(Integer.valueOf(txtCCNumOfMonth.getText().toString().isEmpty() ? "0" : String.valueOf(Integer.parseInt(txtCCNumOfMonth.getText().toString())-1)));
                sum.getSumCredit().setFirstMonthPaid(Double.valueOf(txtCCFirstMonth.getText().toString().isEmpty()?"0":txtCCFirstMonth.getText().toString()));
                sum.getSumCredit().setAdditionalPayments(Double.valueOf(txtCAdditionalPayments.getText().toString().isEmpty()?"0":txtCAdditionalPayments.getText().toString()));
                listener.OnFinishedPaidByCCFragment(sum);
                dismiss();
            }
        });

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(title);
        return  view;
    }

    public interface PaidByCCFragmentListener {
        void OnFinishedPaidByCCFragment(Sum sum);
    }
}
