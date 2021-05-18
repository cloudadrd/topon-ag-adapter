package com.zcoup.multidownload.entitis;



import android.support.annotation.Keep;

import com.zcoup.multidownload.service.LoadListener;

import java.io.Serializable;

@Keep
public class FileInfo implements Serializable {

	private String url;
	private String fileName;
	private String saveDir;
	private int threadCount = 1;
	private int timeOut = 20;
	private boolean isAutoRetry = false;
	private LoadListener loadListener;

	private long length;
	private long finished;
	private boolean error = false;
	private boolean isEnd = false;

	public FileInfo() {
		super();
	}

	public FileInfo(String url, String fileName, String saveDir, int threadCount, int timeOut, boolean isAutoRetry, LoadListener loadListener) {
		super();
		this.url = url;
		this.fileName = fileName;
		this.saveDir = saveDir;
		this.threadCount = threadCount;
		this.timeOut = timeOut;
		this.isAutoRetry = isAutoRetry;
		this.loadListener = loadListener;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @param length
	 */
	public void setLength(long length) {
		this.length = length;
	}

	/**
	 * @return
	 */
	public long getFinished() {
		return finished;
	}

	/**
	 * @param finished
	 */
	public void setFinished(long finished) {
		this.finished = finished;
	}
	
	/**
	 * @return
	 */
	public boolean isError() {
		return error;
	}
	
	/**
	 * @param error
	 */
	public void setError(boolean error) {
		this.error = error;
	}

	/**
	 * @return
	 */
	public String getSaveDir() {
		return saveDir;
	}

	/**
	 * @param saveDir
	 */
	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	/**
	 * @return
	 */
	public int getThreadCount() {
		return threadCount;
	}

	/**
	 * @param threadCount
	 */
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	/**
	 * @return
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * @param timeOut
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	/**
	 * @return
	 */
	public boolean isAutoRetry() {
		return isAutoRetry;
	}

	/**
	 * @param isAutoRetry
	 */
	public void setAutoRetry(boolean isAutoRetry) {
		this.isAutoRetry = isAutoRetry;
	}

	/**
	 * @return
	 */
	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public LoadListener getLoadListener() {
		return loadListener;
	}

	public void setLoadListener(LoadListener loadListener) {
		this.loadListener = loadListener;
	}
}
