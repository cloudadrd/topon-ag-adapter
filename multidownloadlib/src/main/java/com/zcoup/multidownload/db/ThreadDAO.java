package com.zcoup.multidownload.db;


import com.zcoup.multidownload.entitis.ThreadInfo;
import com.zcoup.multidownload.entitis.ThreadInfo;

import java.util.List;

public interface ThreadDAO {
	public void insertThread(ThreadInfo info);
	public void deleteThread(String url);
	public void updateThread(String url, int thread_id, long finished);
	public List<ThreadInfo> queryThreads(String url);
	public boolean isExists(String url, int threadId);
	public void destroy();
}
