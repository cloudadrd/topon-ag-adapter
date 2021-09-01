package com.business.support.captcha;

import java.io.Serializable;

public interface CaptchaListener extends Serializable {

    String onAccess(long time);

    String onFailed(int failCount);

}
