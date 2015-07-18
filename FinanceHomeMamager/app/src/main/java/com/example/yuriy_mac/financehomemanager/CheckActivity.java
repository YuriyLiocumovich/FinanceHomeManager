package com.example.yuriy_mac.financehomemanager;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.yuriy_mac.financehomemanager.Category.CategoryType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CheckActivity extends Activity implements AddCategoryFragment.AddCategoryFragmentListener {

    Category curCategory;
    DBAdapter dbAdapter;

    CategoryAdapter spinnerAdapter;
    ArrayList<Category> categoriesList = new ArrayList<Category>();
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        dbAdapter = new DBAdapter(this);
        categoriesList = DBAdapter.CategoriesLogic.getCategoryList(this, dbAdapter, false);
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

        TextView lblCheckCurRecord = (TextView)findViewById(R.id.lblCheckCurRecord);
        lblCheckCurRecord.setText(lblCheckCurRecord.getText() + " " + Tools.getDate(null));
    }

    public void btnSaveClick(View v){
        // TODO Save data into DB.
        EditText txtCheckNumber = (EditText)findViewById(R.id.txtCheckNumber);
        DatePicker dpCheckDate = (DatePicker)findViewById(R.id.dpCheckDate);
        EditText txtTo = (EditText)findViewById(R.id.txtTo);
        EditText txtFor = (EditText)findViewById(R.id.txtFor);
        EditText txtCheckSum = (EditText)findViewById(R.id.txtCheckSum);

        Calendar calendar = Calendar.getInstance();
        calendar.set(dpCheckDate.getYear(), dpCheckDate.getMonth(), dpCheckDate.getDayOfMonth());
        DBAdapter.CheckLogic.insert(dbAdapter,new Check(curCategory.getCategoryID(), txtTo.getText().toString(),
                txtFor.getText().toString(),txtCheckSum.getText().toString(),Tools.getDateForDB(calendar.getTime()),
                txtCheckNumber.getText().toString()));

        txtCheckNumber.setText("");
        txtTo.setText("");
        txtFor.setText("");
        txtCheckSum.setText("");
        String curDate = Tools.getDateForDB(null);
        dpCheckDate.updateDate(Integer.parseInt(curDate.substring(0,4)),Integer.parseInt(curDate.substring(5,7))-1,Integer.parseInt(curDate.substring(8,10)));
    }

    public void btnCloseClick(View view){
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
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

    public void btnAddCategoryClick(View view){
        AddCategoryFragment catedoryFragment = new AddCategoryFragment(this, R.string.lblFragNewCategory, this);
        catedoryFragment.setCancelable(true);
        catedoryFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void OnFinishedAddCategoryFragment(String title) {
        // TODO Auto-generated method stub

        Category category = new Category(this, title.split(",")[0],
                Integer.valueOf(title.split(",")[1]) == R.id.imgMinus ? CategoryType.CREDIT : CategoryType.DEBIT);
        curCategory = category;
        //insert into DB
        category.setCategoryID(DBAdapter.CategoriesLogic.insert(dbAdapter, category));
        //insert into snippet
        spinnerAdapter = new CategoryAdapter(this, R.layout.layout_category
                ,categoriesList);
        spinnerAdapter.setDropDownViewResource(R.layout.layout_category);
        categoriesList.add(category);
        spinnerAdapter.notifyDataSetChanged();
        spinner = (Spinner)findViewById(R.id.spinnerCategory);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(categoriesList.indexOf(category));
    }
}

