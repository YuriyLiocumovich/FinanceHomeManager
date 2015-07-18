package com.example.yuriy_mac.financehomemanager;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {

    private static final int CASH_REQUEST_CODE = 100;
    private static final int CREDIT_INFO_REQUEST_CODE = 101;
    private static final int CHECK_REQUEST_CODE = 102;

    double thisMonthTotal = 0;
    double totalSum = 0;
    double totalCurrentMonthCC = 0;
    double totalCC = 0;
    double totalCurrentMonthCheck = 0;
    double totalCheck = 0;

    CashInfoLayout cashInfoLayout;
    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbAdapter = new DBAdapter(this);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),1);
        Global.setStartDate(calendar.getTime());
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Global.setEndDate(calendar.getTime());
        LinearLayout llmain = (LinearLayout) findViewById(R.id.llMain);
        cashInfoLayout = new CashInfoLayout(this, dbAdapter, null);
        llmain.addView(cashInfoLayout, 0);
        thisMonthTotal = cashInfoLayout.getExpenditure();
        totalSum = thisMonthTotal;

        getCreditCardInfo();
        thisMonthTotal += totalCurrentMonthCC;
        totalSum += totalCC;

        getCheckInfo();
        thisMonthTotal += totalCurrentMonthCheck;
        totalSum += totalCheck;

        ((TextView)findViewById(R.id.lblMainCurMonthTotal)).setText(String.format("%.2f",thisMonthTotal));
        ((TextView)findViewById(R.id.lblMainTotal)).setText(String.format("%.2f",totalSum));
    }

    private void getCreditCardInfo(){
        ArrayList<CreditCardInfo> creditCardInfoList = DBAdapter.CreditCardLogic.getCreditCardInfoList(this,dbAdapter);
        totalCurrentMonthCC = 0;
        totalCC = 0;
        for(CreditCardInfo creditCardInfo : creditCardInfoList) {
            totalCurrentMonthCC += creditCardInfo.getCurSum();
            totalCC += creditCardInfo.getSum();
        }

        TextView totalCCCurrentSum = (TextView)((CCButtomLine)findViewById(R.id.ccblMain)).findViewById(R.id.lblCCThisMonthTotal);
        totalCCCurrentSum.setText(String.format("%.2f",totalCurrentMonthCC));

        TextView totalCCSum = (TextView)((CCButtomLine)findViewById(R.id.ccblMain)).findViewById(R.id.lblCCTotalSum);
        totalCCSum.setText(String.format("%.2f",totalCC));
    }

    private void getCheckInfo(){
        ArrayList<Check> checkList = DBAdapter.CheckLogic.getCheckList(this,dbAdapter);
        totalCurrentMonthCheck = 0;
        totalCheck = 0;
        for(Check check : checkList) {
            totalCurrentMonthCheck+= check.getThisMonthSum();
            totalCheck += check.getTotalSum();
        }

        thisMonthTotal+=totalCurrentMonthCheck;
        totalSum+=totalCheck;

        TextView lblCheckThisMonth = (TextView)findViewById(R.id.lblCheckThisMonth);
        lblCheckThisMonth.setText(String.format("%.2f",totalCurrentMonthCheck));

        TextView lblCheckTotalMonth = (TextView)findViewById(R.id.lblCheckTotalMonth);
        lblCheckTotalMonth.setText(String.format("%.2f",totalCheck));
    }

    public void bntCashClick(View view) {
        Intent intent = new Intent(this, CashActivity.class);
        startActivityForResult(intent, CASH_REQUEST_CODE);
    }

    public void btnCredCardClick(View view) {
        Intent intent = new Intent(this, CreditCardActivity.class);
        startActivityForResult(intent, CREDIT_INFO_REQUEST_CODE);
    }

    public void btnCheckClick(View view){
        Intent intent = new Intent(this, CheckActivity.class);
        startActivityForResult(intent, CHECK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CASH_REQUEST_CODE && resultCode == RESULT_OK)
            cashInfoLayout.calcTotal(dbAdapter);
        if (requestCode == CREDIT_INFO_REQUEST_CODE && resultCode == RESULT_OK)
            getCreditCardInfo();
        if (requestCode == CHECK_REQUEST_CODE && resultCode == RESULT_OK)
            getCheckInfo();

        thisMonthTotal = cashInfoLayout.getExpenditure();
        totalSum = thisMonthTotal;
        thisMonthTotal += totalCurrentMonthCC;
        totalSum += totalCC;
        thisMonthTotal += totalCurrentMonthCheck;
        totalSum += totalCheck;

        ((TextView)findViewById(R.id.lblMainCurMonthTotal)).setText(String.format("%.2f",thisMonthTotal));
        ((TextView)findViewById(R.id.lblMainTotal)).setText(String.format("%.2f",totalSum));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
