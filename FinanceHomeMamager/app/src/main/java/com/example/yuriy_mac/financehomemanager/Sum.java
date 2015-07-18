package com.example.yuriy_mac.financehomemanager;

/**
 * Created by yuriy_mac on 5/9/15.
 */
public class Sum {
    private long _categoryID;
    private String _sum;
    private int _type;
    private String _date;
    private int _creditCardId;
    private int _isCredit;
    private SumCredit _sumCredit;

    private Check _check;

    public Sum(long categoryID, String sum, String date, int type, int creditCardId, int isCredit){
        this._categoryID = categoryID;
        this._sum = sum;
        this._date = date;
        this._type = type;
        this._creditCardId = creditCardId;
        this._isCredit = isCredit;
        this._sumCredit = new SumCredit();
    }

    public Sum(long categoryID, String sum, String date, int type, Check check){
        this._categoryID = categoryID;
        this._sum = sum;
        this._date = date;
        this._type = type;
        this._check = check;
    }

    public long getCategoryID(){return _categoryID;}

    public String getSum() {return _sum;}

    public String getDate(){return  _date;}

    public int getType(){return _type;}

    public Check getCheck(){return _check;}

    public int getCreditCardId(){return _creditCardId;}

    public int getIsCredit(){return _isCredit;}

    public SumCredit getSumCredit() {
        return _sumCredit;
    }

    public void setSumCredit(SumCredit _sumCredit) {
        this._sumCredit = _sumCredit;
    }

    public class SumCredit{
        private double _totalPaid;
        private int _numberOfMonths;
        private double _firstMonthPaid;
        private double _additionalPayments;
        private String _toDate;

        public SumCredit(){

        }

        public double getTotalPaid() {
            return _totalPaid;
        }

        public void setTotalPaid(double totalPaid) {
            this._totalPaid = totalPaid;
        }

        public int getNumberOfMonths() {
            return _numberOfMonths;
        }

        public void setNumberOfMonths(int numberOfMonths) {
            this._numberOfMonths = numberOfMonths;
        }

        public double getFirstMonthPaid() {
            return _firstMonthPaid;
        }

        public void setFirstMonthPaid(double firstMonthPaid) {
            this._firstMonthPaid = firstMonthPaid;
        }

        public double getAdditionalPayments() {
            return _additionalPayments;
        }

        public void setAdditionalPayments(double additionalPayments) {
            this._additionalPayments = additionalPayments;
        }

        public String getToDate() {
            return _toDate;
        }

        public void setToDate(String _toDate) {
            this._toDate = _toDate;
        }
    }
}
