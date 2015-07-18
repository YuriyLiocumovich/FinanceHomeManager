package com.example.yuriy_mac.financehomemanager;

public class CreditCardInfo {

    private double curSum;
    private double totalSum;
    private CreditCard creditCard;


    public CreditCardInfo(CreditCard creditCard, double curSum, double totalSum){
        this.creditCard = creditCard;
        this.curSum = curSum;
        this.totalSum = totalSum;
    }

    public double getCurSum(){
        return curSum;
    }

    public double getSum(){
        return totalSum;
    }

    public CreditCard getCreditCard(){return creditCard;};
}
