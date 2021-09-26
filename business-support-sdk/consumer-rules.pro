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

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

#sdk
-keep class com.obs.services.** {*;}
-keep class com.obs.log.** {*;}

#java-xmlbuilder
-keep class com.jamesmurty.utils.**{*;}
-keep class net.security.device.api.** {*;}
-dontwarn net.security.device.api.**
