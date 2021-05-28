package com.business.support.compose;

import android.content.Context;

public interface ISdkMain {

    boolean init(Context context, String... params);


    void requestQuery(TaskResultListener listener);

}
