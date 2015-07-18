package com.example.yuriy_mac.financehomemanager;

import java.util.Date;

/**
 * Created by yuriy_mac on 5/9/15.
 */
public class Global {
    private static Date _startDate;
    private static Date _endDate;


    public static Date getStartDate() {
        return _startDate;
    }

    public static void setStartDate(Date startDate) {
        _startDate = startDate;
    }

    public static Date getEndDate() {
        return _endDate;
    }

    public static void setEndDate(Date endDate) {
        _endDate = endDate;
    }
}
