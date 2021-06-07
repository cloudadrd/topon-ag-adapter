package com.business.support.webview;

/**
 * Created by jiantao.tu on 12/4/20.
 */
public enum DownloadState {

    //开始下载
    START(1),

    //开始中
    UPDATE(2),

    //下载成功
    SUCCESS(3),

    //下载失败
    FAILED(4),

    //安装成功
    INSTALL_OK(5),

    //已下载
    DOWNLOADED(6),

    //已安装
    INSTALLED(7),

    //未下载
    NO_DOWNLOAD(8);

    private final int state;

    DownloadState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return "AdType{" +
                "typeId=" + state +
                '}';
    }
}
