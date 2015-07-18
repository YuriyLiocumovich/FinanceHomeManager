package com.example.yuriy_mac.financehomemanager;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreditCardInfoLayout extends LinearLayout {

    TextView lblCCType;
    TextView lblCCTotalSumSpecCC;
    TextView lblCCCurMonthSum;

    public CreditCardInfoLayout(Context context, AttributeSet attrs){
        this(context, attrs, null);
    }

    public CreditCardInfoLayout(Context context, AttributeSet attrs, CreditCardInfo creditCardInfo) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_creditcard_info, this, true);

        lblCCType = (TextView)findViewById(R.id.lblCCType);
        lblCCType.setGravity(Gravity.CENTER);
        lblCCTotalSumSpecCC = (TextView)findViewById(R.id.lblCCTotalSumSpecCC);
        lblCCCurMonthSum = (TextView)findViewById(R.id.lblCCCurMonthSum);
        if(creditCardInfo ==  null){
            CreditCardInfo creditCardInfo1 = new CreditCardInfo(new CreditCard("Visa","1234"),123.5f,2345f);
            setCreditCardinfo(creditCardInfo1);
        }else
            setCreditCardinfo(creditCardInfo);
    }

    public void setCreditCardinfo(CreditCardInfo cardInfo){
        lblCCType.setText(cardInfo.getCreditCard().getCreditCardName());
        lblCCTotalSumSpecCC.setText(String.valueOf(cardInfo.getSum()));
        lblCCCurMonthSum.setText(String.valueOf(cardInfo.getCurSum()));
    }
}
