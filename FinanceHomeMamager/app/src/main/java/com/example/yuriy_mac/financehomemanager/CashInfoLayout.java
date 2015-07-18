package com.example.yuriy_mac.financehomemanager;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CashInfoLayout extends LinearLayout {

    private double expenditure;

    public CashInfoLayout(Context context, DBAdapter adapter, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_cash_info, this, true);
        ((TextView)findViewById(R.id.lblCurDate)).setText(Tools.getDate(null));
        calcTotal(adapter);
    }

    public void calcTotal(DBAdapter adapter){
        //
        //TODO read sums from DB and do math functions
        //

        TextView cash =  (TextView)findViewById(R.id.lblCashSum);
        Double cashSum  = DBAdapter.CashLogic.GetSumCash(adapter, Category.CategoryType.DEBIT);
        cash.setText(String.format("%.2f",cashSum));

        expenditure = DBAdapter.CashLogic.GetSumCash(adapter, Category.CategoryType.CREDIT);

        TextView lblExpenditure = (TextView)findViewById(R.id.lblExpenditureSum);
        lblExpenditure.setText(String.format("%.2f",expenditure));
        ((TextView)findViewById(R.id.lblTotalSum)).setText(
                String.format("%.2f",cashSum - expenditure));
    }

    public double getExpenditure(){
        return expenditure;
    }
}
