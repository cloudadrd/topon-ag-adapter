package com.business.support.calendar;
public class CalendarPara {
    long eventId;
    String title;
    String description;
    long startTime;
    long endTime ;
    boolean alarm;
    int repeatInterval;//重复间隔,只在批量添加时使用
    int repeatCount; //重复次数,只在批量添加时使用

}
