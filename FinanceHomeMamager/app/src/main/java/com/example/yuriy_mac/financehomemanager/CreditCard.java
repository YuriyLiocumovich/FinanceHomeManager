package com.example.yuriy_mac.financehomemanager;

/**
 * Created by yuriy_mac on 1/25/15.
 */
public class CreditCard {
    private String creditCard;
    private String name;
    private String token;
    private long id;

    public CreditCard(String ccName, String ccToken){
        creditCard = ccName + " "+ccToken;
        name = ccName;
        token = ccToken;
    }

    public CreditCard(String ccName, String ccToken, long ccId) {
        this(ccName,ccToken);
        this.id = ccId;
    }

    public String getCreditCardName(){
        return creditCard;
    }

    public String getName(){
        return name;
    }

    public String getToken(){
        return token;
    }

    public long getCreditCardId(){return id;}
}
