package com.zcoup.multidownload.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zcoup.multidownload.entitis.ThreadInfo;
import com.zcoup.multidownload.util.Logger;
import com.zcoup.multidownload.entitis.ThreadInfo;
import com.zcoup.multidownload.util.Logger;

public class ThreadDAOImple implements ThreadDAO {
	
	private DBHelper dbHelper = null;
	
	private SQLiteDatabase db;
	
	public ThreadDAOImple(Context context) {
		super();
		dbHelper = DBHelper.getInstance(context);
		if(db == null || !db.isOpen()){
			db = dbHelper.getReadableDatabase();
		}
	}

	@Override
	public synchronized void insertThread(ThreadInfo info) {
		ContentValues values = new ContentValues();
		values.put("thread_id", info.getId());
		values.put("url", info.getUrl());
		values.put("start", info.getStart());
		values.put("end", info.getEnd());
		values.put("finished", info.getFinished());
		db.insert("thread_info", null, values);
	}

	@Override
	public synchronized void deleteThread(String url) {
		db.delete("thread_info", "url = ?", new String[] { url});
	}

	@Override
	public synchronized void updateThread(String url, int thread_id, long finished) {
		if(!db.isOpen()){
			return;
		}
		db.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?",
				new Object[]{finished, url, thread_id});
	}

	@Override
	public List<ThreadInfo> queryThreads(String url) {
		if(!db.isOpen()){
			return null;
		}
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		Cursor cursor = db.query("thread_info", null, "url = ?", new String[] { url }, 
				null, null, null);
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			list.add(thread);
		}
		cursor.close();
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id) {
		if(!db.isOpen()){
			return false;
		}
		Cursor cursor = db.query("thread_info", null, "url = ? and thread_id = ?", 
				new String[] { url, thread_id + "" },
				null, null, null);
		boolean exists = cursor.moveToNext();
		cursor.close();
		return exists;
	}

	@Override
	public void destroy() {
		Logger.log("close db, isopen:"+db.isOpen());
		if(db != null && db.isOpen()){
			db.close();
		}
	}

}
