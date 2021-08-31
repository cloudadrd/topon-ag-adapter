package com.business.support.calendar;
public class CalendarPara {
    public long eventId;
    public String title;
    public String description;
    public long startTime;
    public long endTime ;
    public boolean alarm;
    public int repeatInterval;//重复间隔,只在批量添加时使用
    public int repeatCount; //重复次数,只在批量添加时使用

}
