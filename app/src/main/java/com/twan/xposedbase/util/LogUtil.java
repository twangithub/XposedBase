package com.twan.xposedbase.util;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.twan.xposedbase.BuildConfig;
import com.twan.xposedbase.ui.Constant;

/**
 * @author Twan
 */
public class LogUtil {

    public static boolean isDebug = BuildConfig.DEBUG;
    private static final String TAG = Constant.TAG;

//    public static void e(String tag, Object o) {
//        if (isDebug) {
//            Logger.e(tag, o);
//        }
//    }
//
//    public static void e(Object o) {
//        LogUtil.e(TAG, o);
//    }

    public static void w(String tag, Object o) {
        if (isDebug) {
            Logger.w(tag, o);
        }
    }

    public static void w(Object o) {
        LogUtil.w(TAG, o);
    }

    public static void d(String msg) {
        if (isDebug) {
            Logger.d(msg);
        }
    }

    public static void i(String msg) {
        if (isDebug) {
            Logger.i(msg);
        }
    }

    public static void wtf(String msg) {
        if (isDebug) {
            Logger.wtf(msg);
        }
    }

    public static void e(String str) {
        Log.e(TAG, str);
    }
}
