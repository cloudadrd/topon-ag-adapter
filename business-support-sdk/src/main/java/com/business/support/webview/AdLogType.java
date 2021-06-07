package com.business.support.webview;

/**
 * Created by jiantao.tu on 12/4/20.
 */
public enum AdLogType {

    //视频加载成功
    LOAD_SUCCESS(12),

    //视频no_ready
    LOAD_NO_READY(13),

    //视频曝光成功
    IMP_SUCCESS(5),
    //视频播放完成后点击关闭
    PLAY_END_CLOSE(6),
    //视频广告点击
    VIDEO_CLICK(7),
    //视频奖励
    VIDEO_REWARD(8),
    //视频播放失败
    PLAY_FAIL(11);

    private final int typeId;

    AdLogType(int typeId) {
        this.typeId = typeId;
    }
    public int getTypeId() {
        return typeId;
    }

    @Override
    public String toString() {
        return "AdType{" +
                "typeId=" + typeId +
                '}';
    }
}
