package com.business.support.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Looper;
import android.provider.CalendarContract;

import com.business.support.utils.ContextHolder;

import java.util.TimeZone;


public class CalendarOperate {
    private static String CALENDAR_URL = "content://com.android.calendar/calendars";
    private static String CALENDAR_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDAR_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "playGame";
    private static String CALENDARS_ACCOUNT_NAME = "ymgame@game.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.ymgame";
    private static String CALENDARS_DISPLAY_NAME = "playGame";
    private static boolean isCheckedAccount = false;
    private static long calendarId = -1;

    private static final int CALENDAR_ID_INDEX = 0;
    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
    };
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events._ID,
    };

    public static void insertCalendar(final Context context, final String appName, final String appid, final CalendarPara para){
        paraRedefine(appName, appid);
        if (isMainThread()) {
           Thread insert = new Thread(new Runnable() {
               @Override
               public void run() {
                   insertCalendarOperate(context,appName, appid, para);
               }
           });
           insert.start();
       }else {
           insertCalendarOperate(context,appName, appid, para);
       }
    }


    public static void batchInsertCalendar(final Context context, final String appName, final String appid, final CalendarPara para){
        paraRedefine(appName, appid);
        if (isMainThread()) {
            Thread insert = new Thread(new Runnable() {
                @Override
                public void run() {
                    batchInsertCalendarOperate(context,appName, appid, para);
                }
            });
            insert.start();
        }else {
            batchInsertCalendarOperate(context,appName, appid, para);
        }
    }
    public static void updateCalendar(final Context context, final String appName, final String appid, final CalendarPara para) {
        paraRedefine(appName,appid);
        if (isMainThread()) {
            Thread update = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateCalendarOperate(context, appName,appid, para);
                }
            });
            update.start();
        }else {
            updateCalendarOperate(context, appName,appid, para);
        }
    }


    public static void batchUpdateCalendar(final Context context, final String appName, final String appid, final CalendarPara para) {
        paraRedefine(appName,appid);
        if (isMainThread()) {
            Thread update = new Thread(new Runnable() {
                @Override
                public void run() {
                    batchUpdateCalendarOperate(context, appName,appid, para);
                }
            });
            update.start();
        }else {
            batchUpdateCalendarOperate(context, appName,appid, para);
        }
    }

    public static  void deleteCalendar(final Context context,  final long eventId) {
        if (isMainThread()) {
            Thread delete = new Thread(new Runnable() {
                @Override
                public void run() {
                    deleteCalendarOperate(context, eventId);
                }
            });
            delete.start();

        }else {
            deleteCalendarOperate(context, eventId);
        }
    }

    public static boolean searchCalendar(final Context context,  final long eventId) {
        return isHadEvent(context, eventId);
    }

    private static synchronized boolean insertCalendarOperate(Context context, final String appName, String appid, CalendarPara para) {
        if (context == null) {
            return false;
        }
        calendarId = checkAndAddCalendarAccount(context);
        if (0 > calendarId || isHadEvent(context, para.eventId)){
            return false;
        }

        //Log.e(LOG_TAG,"addNewRemindEvent" + scheduleInformation.id + "..." +  scheduleInformation.remindEventId+ "..." +  mCourseInfo.autoRemind);
        try {
            /** 插入日程 */
            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.DTSTART, para.startTime);
            eventValues.put(CalendarContract.Events.DTEND, para.endTime);
            eventValues.put(CalendarContract.Events.TITLE, para.title);
            eventValues.put(CalendarContract.Events.DESCRIPTION, para.description);
            eventValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            eventValues.put(CalendarContract.Events._ID, para.eventId);
            eventValues.put(CalendarContract.Events.HAS_ALARM, para.alarm ? 1 : 0);
            eventValues.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
            TimeZone tz = TimeZone.getDefault(); // 获取默认时区
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            Uri eUri = context.getContentResolver().insert(Uri.parse(CALENDAR_EVENT_URL), eventValues);
            long eventId = ContentUris.parseId(eUri);
            if (eventId == 0) { // 插入失败
                return false;
            }

            /** 插入提醒 - 依赖插入日程成功 */
            ContentValues reminderValues = new ContentValues();
            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
            reminderValues.put(CalendarContract.Reminders.MINUTES, 5); // 提前提醒
            reminderValues.put(CalendarContract.Reminders.METHOD,
                    CalendarContract.Reminders.METHOD_ALERT);
            Uri rUri = context.getContentResolver().insert(Uri.parse(CALENDAR_REMINDER_URL),
                    reminderValues);
            if (rUri == null || ContentUris.parseId(rUri) == 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static synchronized boolean batchInsertCalendarOperate(Context context, final String appName, String appid, CalendarPara para) {
        if (context == null) {
            return false;
        }
        calendarId = checkAndAddCalendarAccount(context);
        if (0 > calendarId || isHadEvent(context, para.eventId)){
            return false;
        }

        //Log.e(LOG_TAG,"addNewRemindEvent" + scheduleInformation.id + "..." +  scheduleInformation.remindEventId+ "..." +  mCourseInfo.autoRemind);
        try {
            /** 插入日程 */
            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.DTSTART, para.startTime);
            eventValues.put(CalendarContract.Events.DTEND, para.endTime);
            eventValues.put(CalendarContract.Events.RRULE, "FREQ=DAILY;" + "INTERVAL="+para.repeatInterval + ";COUNT="+para.repeatCount);
            eventValues.put(CalendarContract.Events.TITLE, para.title);
            eventValues.put(CalendarContract.Events.DESCRIPTION, para.description);
            eventValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            eventValues.put(CalendarContract.Events._ID, para.eventId);
            eventValues.put(CalendarContract.Events.HAS_ALARM, para.alarm ? 1 : 0);
            eventValues.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
            TimeZone tz = TimeZone.getDefault(); // 获取默认时区
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            Uri eUri = context.getContentResolver().insert(Uri.parse(CALENDAR_EVENT_URL), eventValues);
            long eventId = ContentUris.parseId(eUri);
            if (eventId == 0) { // 插入失败
                return false;
            }

            /** 插入提醒 - 依赖插入日程成功 */
            ContentValues reminderValues = new ContentValues();
            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
            reminderValues.put(CalendarContract.Reminders.MINUTES, 5); // 提前提醒
            reminderValues.put(CalendarContract.Reminders.METHOD,
                    CalendarContract.Reminders.METHOD_ALERT);
            Uri rUri = context.getContentResolver().insert(Uri.parse(CALENDAR_REMINDER_URL),
                    reminderValues);
            if (rUri == null || ContentUris.parseId(rUri) == 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static synchronized boolean updateCalendarOperate(Context context, final String appName, String appid, CalendarPara para) {
        if (context == null) {
            return false;
        }

        calendarId =  checkAndAddCalendarAccount(context);
        if (0 > calendarId) {
            return false;
        }
        try {
            /** 更新日程 */
            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.DTSTART, para.startTime);
            eventValues.put(CalendarContract.Events.DTEND, para.endTime);
            eventValues.put(CalendarContract.Events.TITLE, para.title);
            eventValues.put(CalendarContract.Events.DESCRIPTION, para.description);
            eventValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            eventValues.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
            eventValues.put(CalendarContract.Events.HAS_ALARM, para.alarm ? 1 : 0);
            TimeZone tz = TimeZone.getDefault(); // 获取默认时区
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            Uri rUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), para.eventId);
            final int row =  context.getContentResolver().update(rUri, eventValues, null, null);
            if (row > 0)/*更新event不成功，说明用户在日历中删除了提醒事件，重新添加*/
            {
                /** 更新提醒 - 依赖更新日程成功 */
                ContentValues reminderValues = new ContentValues();
                reminderValues.put(CalendarContract.Reminders.MINUTES, 5); // 提前提醒
                Uri uri = Uri.parse(CALENDAR_REMINDER_URL);
                context.getContentResolver().update(uri, reminderValues, CalendarContract.Reminders.EVENT_ID + "= ?", new String[]{String.valueOf(para.eventId)});
                return true;
            }
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static synchronized boolean batchUpdateCalendarOperate(Context context, final String appName, String appid, CalendarPara para) {
        if (context == null) {
            return false;
        }

        calendarId =  checkAndAddCalendarAccount(context);
        if (0 > calendarId) {
            return false;
        }
        try {
            /** 更新日程 */
            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.DTSTART, para.startTime);
            eventValues.put(CalendarContract.Events.DTEND, para.endTime);
            eventValues.put(CalendarContract.Events.RRULE, "FREQ=DAILY;" + "INTERVAL="+para.repeatInterval + ";COUNT="+para.repeatCount);
            eventValues.put(CalendarContract.Events.TITLE, para.title);
            eventValues.put(CalendarContract.Events.DESCRIPTION, para.description);
            eventValues.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            eventValues.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
            eventValues.put(CalendarContract.Events.HAS_ALARM, para.alarm ? 1 : 0);
            TimeZone tz = TimeZone.getDefault(); // 获取默认时区
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
            Uri rUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), para.eventId);
            final int row =  context.getContentResolver().update(rUri, eventValues, null, null);
            if (row > 0)/*更新event不成功，说明用户在日历中删除了提醒事件，重新添加*/
            {
                /** 更新提醒 - 依赖更新日程成功 */
                ContentValues reminderValues = new ContentValues();
                reminderValues.put(CalendarContract.Reminders.MINUTES, 5); // 提前提醒
                Uri uri = Uri.parse(CALENDAR_REMINDER_URL);
                context.getContentResolver().update(uri, reminderValues, CalendarContract.Reminders.EVENT_ID + "= ?", new String[]{String.valueOf(para.eventId)});
                return true;
            }
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static synchronized boolean deleteCalendarOperate(Context context, long eventId) {
        if (context == null) {
            return false;
        }

        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), eventId);
        int rows = context.getContentResolver().delete(deleteUri, null, null);
        if (rows < 1) { //事件删除失败
            return false;
        }
        return true;
    }

    private static boolean isHadEvent(Context context ,long eventId) {
        Cursor cur = null;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
//            String selection = "((" + CalendarContract.Events.CALENDAR_ID + " = ?) AND (" + CalendarContract.Events._ID + " = ?))";
            String selection = "((" + CalendarContract.Events._ID + " = ?))";
//            String[] selectionArgs = new String[]{String.valueOf(calendarId),String.valueOf(eventId)} ;
            String[] selectionArgs = new String[]{String.valueOf(eventId)} ;


            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            if (0 == cur.getCount()) {
                return false;
            }
            return true;
        }finally {
            if (cur != null) {
                cur.close();
            }

        }
    }


    private static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDAR_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
                        CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }
    public static long checkAndAddCalendarAccount(Context context)    {
        if (isCheckedAccount){
            return calendarId;
        }
        isCheckedAccount = true;
        long id = checkCalendarAccount(context);
        if (id != -1) return id;
        else return (int)addCalendarAccount(context);

    }
    private static long checkCalendarAccount(Context context) {
        // 执行查询
        Cursor cur = null;
        long calID = -1;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CalendarContract.Calendars.CONTENT_URI;
            String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?))";
            String[] selectionArgs = new String[] {CALENDARS_ACCOUNT_NAME};
            cur = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                calID = cur.getLong(CALENDAR_ID_INDEX);
            }
            return calID;
        }finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    private static void paraRedefine(String appName,String appid){
         CALENDARS_NAME = appName;
         CALENDARS_ACCOUNT_NAME = appid+"@game.com";
         CALENDARS_ACCOUNT_TYPE = ContextHolder.getGlobalAppContext().getPackageName();
         CALENDARS_DISPLAY_NAME = appName;
    }

}
