#for sdk
-keep public class com.business.support.**{*;}
-keep class com.ishumei.** {*;}
-dontwarn com.business.support.**
-keep class cn.shuzilm.core.** {*;}

-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
    *;
}

-keep class net.security.device.api.** {*;}
-dontwarn net.security.device.api.**