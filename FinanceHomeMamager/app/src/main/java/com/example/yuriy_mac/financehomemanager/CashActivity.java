package com.example.yuriy_mac.financehomemanager;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.yuriy_mac.financehomemanager.Category.CategoryType;
import com.example.yuriy_mac.financehomemanager.ChangeDateFragment.ChangeDateFragmentListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class CashActivity extends Activity implements AddCategoryFragment.AddCategoryFragmentListener, ChangeDateFragmentListener {

    ListView lstView;
    TextView lblCashCurRecord;
    CategoryAdapter spinnerAdapter;
    ArrayList<Category> categoriesList = new ArrayList<Category>();
    Spinner spinner;

    DBAdapter dbAdapter;
    Category curCategory;
    String dateForRecord;
    Date curDate;
    EditText txtSum;

    CashInfoLayout cashInfoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);

        txtSum = (EditText)findViewById(R.id.txtSum);

        lblCashCurRecord = (TextView)findViewById(R.id.lblCashCurRecord);
        lblCashCurRecord.setText(lblCashCurRecord.getText() + " " + Tools.getDate(null));

        dbAdapter = new DBAdapter(this);

        LinearLayout llCashRoot = (LinearLayout) findViewById(R.id.llCashRoot);
        cashInfoLayout = new CashInfoLayout(this, dbAdapter, null);
        llCashRoot.addView(cashInfoLayout, llCashRoot.getChildCount()-2);

        categoriesList = DBAdapter.CategoriesLogic.getCategoryList(this, dbAdapter, true);

        spinnerAdapter = new CategoryAdapter(this, R.layout.layout_category
                ,categoriesList);
        spinnerAdapter.setDropDownViewResource(R.layout.layout_category);

        spinner = (Spinner)findViewById(R.id.spinnerCategory);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                curCategory = (Category)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void btnSaveClick(View view) {
        // TODO Save data into DB.

        String text = txtSum.getText().toString();

        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
            date = curDate == null? Calendar.getInstance().getTime():curDate;
        if(text != "") {
            DBAdapter.CashLogic.insert(dbAdapter, new Sum(curCategory.getCategoryID(), text, Tools.getDateForDB(date), 1, 0, 0));// type 1 -> cash
            txtSum.setText("");
            cashInfoLayout.calcTotal(dbAdapter);
        }
    }

    public void btnChangeDateClick(View view){
        ChangeDateFragment changeDateFragment = new ChangeDateFragment(this, R.string.btnChangeDate,this);
        changeDateFragment.setCancelable(true);
        changeDateFragment.show(getFragmentManager(), "changeDate");
    }

    @Override
    public void onFinishChangeDateDialog(String inputString) throws ParseException {
        // TODO Auto-generated method stub
        dateForRecord = inputString;
        DateFormat df = DateFormat.getDateInstance();
        try {
            curDate = df.parse(inputString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lblCashCurRecord = (TextView)findViewById(R.id.lblCashCurRecord);
        lblCashCurRecord.setText(getResources().getString(R.string.lblCashCurRecord) + " " + inputString);
    }

    public void btnCloseClick(View view){
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    public void btnAddCategoryClick(View view){
        AddCategoryFragment categoryFragment = new AddCategoryFragment(this, R.string.lblFragNewCategory, this);
        categoryFragment.setCancelable(true);
        categoryFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void OnFinishedAddCategoryFragment(String title) {

        Category category = new Category(this, title.split(",")[0],
                Integer.valueOf(title.split(",")[1]) == R.id.imgMinus ? CategoryType.CREDIT : CategoryType.DEBIT);
        curCategory = category;
        //insert into DB
        category.setCategoryID(DBAdapter.CategoriesLogic.insert(dbAdapter, category));
        //insert into snippet
        categoriesList.add(category);
        spinnerAdapter.notifyDataSetChanged();
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(categoriesList.indexOf(category));
    }
}

