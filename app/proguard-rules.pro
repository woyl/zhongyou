# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/whatisjava/Application/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep com.dou361.ijkplayer.** {*;}

-keepattributes Exceptions,InnerClasses

-keepattributes Signature

-keep class io.rong.** {*;}
-keep class cn.rongcloud.** {*;}
-keep class * implements io.rong.imlib.model.MessageContent {*;}
-dontwarn io.rong.push.**
-dontnote com.xiaomi.**
-dontnote com.google.android.gms.gcm.**
-dontnote io.rong.**

-keep class io.agora.rtc.** {*;}

-keep class com.google.gson.** { *; }
-keep class com.uuhelper.Application.** {*;}
-keep class net.sourceforge.zbar.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.alipay.** {*;}
-keep class com.jrmf360.rylib.** {*;}

-keep public class com.tencent.bugly.**{*;}

-keep  class com.tencent.bugly.**{*;}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}
-keep class com.gyf.immersionbar.* {*;}
-keep public class cn.jzvd.JZMediaSystem {*; }

-keep public class cn.jzvd.demo.CustomMedia.CustomMedia {*; }

-keep public class cn.jzvd.demo.CustomMedia.JZMediaIjk {*; }

-keep public class cn.jzvd.demo.CustomMedia.JZMediaSystemAssertFolder {*; }


-keep class tv.danmaku.ijk.media.player.** {*; }

-dontwarn tv.danmaku.ijk.media.player.*

-keep interface tv.danmaku.ijk.media.player.** { *; }

-keep class com.zhongyou.meet.mobile.IM.SealNotificationReceiver {*;}

-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}

-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

-keep   class com.amap.api.services.**{*;}

-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}
 -keep class com.tencent.mm.sdk.** {
    *;
}
-keep public class cn.jzvd.JZMediaSystem {*; }
-keep public class cn.jzvd.demo.CustomMedia.CustomMedia {*; }
-keep public class cn.jzvd.demo.CustomMedia.JZMediaIjk {*; }
-keep public class cn.jzvd.demo.CustomMedia.JZMediaSystemAssertFolder {*; }

-keep class tv.danmaku.ijk.media.player.** {*; }
-dontwarn tv.danmaku.ijk.media.player.*
-keep interface tv.danmaku.ijk.media.player.** { *; }
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.push.** { *; }
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }

-dontwarn com.coloros.mcsdk.**
-keep class com.coloros.mcsdk.** { *; }

-dontwarn com.heytap.**
-keep class com.heytap.** { *; }

-dontwarn com.mcs.**
-keep class com.mcs.** { *; }
-dontwarn com.meizu.cloud.**
-keep class com.meizu.cloud.** { *; }
-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-ignorewarnings
