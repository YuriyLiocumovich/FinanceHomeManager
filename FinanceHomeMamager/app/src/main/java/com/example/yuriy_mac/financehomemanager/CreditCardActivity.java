package com.example.yuriy_mac.financehomemanager;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.yuriy_mac.financehomemanager.AddCredCardFragment.AddCredCardFragmentListener;
import com.example.yuriy_mac.financehomemanager.Category.CategoryType;
import com.example.yuriy_mac.financehomemanager.ChangeDateFragment.ChangeDateFragmentListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CreditCardActivity extends Activity implements AddCredCardFragmentListener,
        ChangeDateFragmentListener, AddCategoryFragment.AddCategoryFragmentListener,
        PaidByCCFragment.PaidByCCFragmentListener {

    private TextView lblCCCurRecord;
    CategoryAdapter spinnerAdapter;
    ArrayList<Category> categoriesList = new ArrayList<Category>();
    Spinner spinner;
    Category curCategory;
    DBAdapter dbAdapter;
    ArrayList<CreditCardInfo> creditCardInfoList;
    Boolean isFirstTime = false;
    String dateForRecord;
    Date date = null;
    boolean haveToClean = false;
    Date curDate;

    LinearLayout llCCCategory_ToRemove;
    TextView lblCredit_ToRemove;
    TextView lblCCNames_To_Remove;

    LinearLayout llAction;
    RadioGroup rbGrpCredit_ToMove;
    Button btnChangeDate_ToRemove;

    LinearLayout llButtons;
    Button btnNewData_ToMove;

    HorizontalScrollView horScrView;
    RadioGroup rbGrpCC_ToMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creditcard);

        dbAdapter = new DBAdapter(this);

        rbGrpCredit_ToMove = (RadioGroup)findViewById(R.id.rbGrpCredit);
        rbGrpCC_ToMove = (RadioGroup)findViewById(R.id.rbGrpCC);

        LinearLayout llMain = (LinearLayout) findViewById(R.id.llCreditCardInfo);
        getCreditCardInfo(llMain, true);
        rbGrpCC_ToMove.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                openPaidByCCDialog();
            }
        });


        lblCCCurRecord = (TextView)findViewById(R.id.lblCCCurRecord);
        lblCCCurRecord.setText(lblCCCurRecord.getText() + " " + Tools.getDate(null));


        if(creditCardInfoList.size()==0) {
            removeViews();
            isFirstTime = true;
        }else {
            setCategory();
            rbGrpCredit_ToMove.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    openPaidByCCDialog();
                }
            });
        }
    }

    private void setCategory(){
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
    }

    private void getCreditCardInfo(LinearLayout llMain, boolean isBuildRadioGroup){
        double totalCurrentMonthCC = 0;
        double totalCC = 0;
        CreditCardInfoLayout cardInfoLayout;

        llMain.removeAllViews();

        creditCardInfoList = DBAdapter.CreditCardLogic.getCreditCardInfoList(this,dbAdapter);
        for(CreditCardInfo creditCardInfo : creditCardInfoList) {

            totalCurrentMonthCC += creditCardInfo.getCurSum();
            totalCC += creditCardInfo.getSum();
            cardInfoLayout = new CreditCardInfoLayout(this,
                    null, creditCardInfo);
            llMain.addView(cardInfoLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 1f));

            Space space = new Space(this);
            space.setLayoutParams(new LayoutParams(10, 10));

            llMain.addView(space);
        }
        llMain.removeView(llMain.getChildAt(llMain.getChildCount()-1));
        if(isBuildRadioGroup){
            RadioButton radioButton;
            rbGrpCC_ToMove.removeAllViews();
            for (int i = 0, row = 0, column = 0; i < creditCardInfoList.size(); i++, column++) {
                radioButton = new RadioButton(this);
                radioButton.setText(creditCardInfoList.get(i).getCreditCard().getCreditCardName());
                radioButton.setId(i+1);
                radioButton.setTag(creditCardInfoList.get(i).getCreditCard().getCreditCardId());
                rbGrpCC_ToMove.addView(radioButton);
            }
        }
        else {
            haveToClean = true;
            rbGrpCredit_ToMove.clearCheck();
            rbGrpCC_ToMove.clearCheck();
            haveToClean = false;
        }
        TextView totalCCCurrentSum = (TextView)((CCButtomLine)findViewById(R.id.ccbCC)).findViewById(R.id.lblCCThisMonthTotal);
        totalCCCurrentSum.setText(String.valueOf(totalCurrentMonthCC));

        TextView totalCCSum = (TextView)((CCButtomLine)findViewById(R.id.ccbCC)).findViewById(R.id.lblCCTotalSum);
        totalCCSum.setText(String.valueOf(totalCC));
    }

    private void removeViews(){
        llCCCategory_ToRemove = (LinearLayout)findViewById(R.id.llCCCategory);
        ViewGroup parent = (ViewGroup)llCCCategory_ToRemove.getParent();
        parent.removeView(llCCCategory_ToRemove);
        lblCredit_ToRemove = (TextView)findViewById(R.id.lblCredit);
        parent.removeView(lblCredit_ToRemove);
        lblCCNames_To_Remove = (TextView)findViewById(R.id.lblCreditCardName);

        parent.removeView(lblCCNames_To_Remove);
        parent = (ViewGroup)rbGrpCC_ToMove.getParent();
        horScrView = (HorizontalScrollView)parent;
        parent.removeView(rbGrpCC_ToMove);

        parent = (ViewGroup)rbGrpCredit_ToMove.getParent();
        llAction = (LinearLayout)parent;
        parent.removeView(rbGrpCredit_ToMove);
        btnChangeDate_ToRemove = (Button)findViewById(R.id.btnChangeDate);
        parent.removeView(btnChangeDate_ToRemove);
    }

    private void addViews(){
        LinearLayout llMain = (LinearLayout)findViewById(R.id.llCC);
        llMain.addView(lblCCNames_To_Remove,2);
        llMain.addView(lblCredit_ToRemove,1);
        llMain.addView(llCCCategory_ToRemove,1);

        horScrView.addView(rbGrpCC_ToMove);

        llAction.addView(btnChangeDate_ToRemove,1);
        llAction.addView(rbGrpCredit_ToMove,0);

        isFirstTime = false;
        setCategory();
    }

    public void btnNewDataClick(View view) {
        // TODO Save data into DB.

        openPaidByCCDialog();
    }

    public void btnAddCategoryClick(View view){
        AddCategoryFragment catedoryFragment = new AddCategoryFragment(this, R.string.lblFragNewCategory, this);
        catedoryFragment.setCancelable(true);
        catedoryFragment.show(getFragmentManager(), "dialog");
    }

    public void btnAddCredCardClick(View view){
        AddCredCardFragment addCredCardFragment = new AddCredCardFragment(this, R.string.lblFragNewCC, this);
        addCredCardFragment.setCancelable(true);
        addCredCardFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void OnFinishedAddCredCardFragment(CreditCard creditCard) {
        // TODO Auto-generated method stub
        DBAdapter.CreditCardLogic.insert(dbAdapter, creditCard);
        if(isFirstTime)
            addViews();
        LinearLayout llMain = (LinearLayout) findViewById(R.id.llCreditCardInfo);
        getCreditCardInfo(llMain,true);
    }

    public void btnChangeDateClick(View view){
        ChangeDateFragment changeDateFragment = new ChangeDateFragment(this, R.string.btnChangeDate,this);
        changeDateFragment.setCancelable(true);
        changeDateFragment.show(getFragmentManager(), "changeDate");
    }

    public void btnCloseClick(View view){
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onFinishChangeDateDialog(String inputString) {
        // TODO Auto-generated method stub
        dateForRecord = inputString;
        DateFormat df = DateFormat.getDateInstance();
        try {
            curDate = df.parse(inputString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lblCCCurRecord = (TextView)findViewById(R.id.lblCCCurRecord);
        lblCCCurRecord.setText(getResources().getString(R.string.lblCashCurRecord) + " " + inputString);
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

    protected  void openPaidByCCDialog(){
        if(!haveToClean &&
                rbGrpCredit_ToMove.getCheckedRadioButtonId() > 0 &&
                rbGrpCC_ToMove.getCheckedRadioButtonId() > 0 &&
                curCategory != null){
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = curDate == null ? Calendar.getInstance().getTime():curDate;

            RadioButton radioButton = (RadioButton)findViewById(rbGrpCC_ToMove.getCheckedRadioButtonId());
            PaidByCCFragment paidByCCFragment = new PaidByCCFragment(this, R.string.lblPaidByCC, this);
            Bundle args = new Bundle();
            args.putLong("category", curCategory.getCategoryID());
            args.putString("fromDate", Tools.getDateForDB(date));
            args.putInt("credCardID", Integer.parseInt(radioButton.getTag().toString()));
            radioButton = (RadioButton)findViewById(rbGrpCredit_ToMove.getCheckedRadioButtonId());
            args.putInt("isCredit",Integer.parseInt(radioButton.getTag().toString()));
            paidByCCFragment.setArguments(args);
            paidByCCFragment.setCancelable(true);
            paidByCCFragment.show(getFragmentManager(),"PaidByCCFragment");
        }
    }

    @Override
    public void OnFinishedPaidByCCFragment(Sum sum) {
        date = sum.getSumCredit().getNumberOfMonths()>0?Tools.addMonth(date,(sum.getSumCredit().getNumberOfMonths())):date;
        sum.getSumCredit().setToDate(Tools.getDateForDB(date));
        DBAdapter.CreditCardLogic.insertSum(dbAdapter,sum);
        LinearLayout llMain = (LinearLayout) findViewById(R.id.llCreditCardInfo);
        getCreditCardInfo(llMain,false);
    }
}
