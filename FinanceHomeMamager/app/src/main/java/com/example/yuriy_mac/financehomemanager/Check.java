package com.example.yuriy_mac.financehomemanager;

/**
 * Created by yuriy_mac on 6/27/15.
 */
public class Check {
    private long _categoryID;
    private String _payTo;
    private String _payFor;;
    private String _sum;
    private String _date;
    private String _number;
    private double _thisMonthSum;
    private double _totalSum;

    public Check(long categoryID, String payTo, String payFor,String sum, String date, String number){
        this._categoryID = categoryID;
        this._payTo = payTo;
        this._payFor = payFor;
        this._sum = sum;
        this._date = date;
        this._number = number;
    }

    public Check(double thisMonthSum, double totalSum){
        this(0,"","","","","");
        this._thisMonthSum = thisMonthSum;
        this._totalSum = totalSum;
    }

    public long getCategoryID(){return _categoryID;}

    public String getPayTo(){return _payTo;}

    public String getPayFor(){return _payFor;}

    public String getSum(){return _sum;}

    public String getDate(){return  _date;}

    public String getNumber(){return _number;}

    public double getThisMonthSum(){return  _thisMonthSum;}

    public double getTotalSum(){return _totalSum;}
}
