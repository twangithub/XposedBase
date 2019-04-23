package com.twan.xposedbase.hook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.orhanobut.logger.Logger;
import com.twan.xposedbase.util.LogUtil;
import com.twan.xposedbase.xc.DrawTextXc_Method;
import com.twan.xposedbase.xc.DrawTextXc_Method_2;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import kotlin.text.StringsKt;

/**
 * 实现了 hook android系统中出现的手机号 , 使其变成*号
 */
public final class HiddenWechatIdAndPhoneNumberHook {
    private static final Pattern PHONE_NUMBER_REGEX = Pattern.compile("([0-9]{3}\\u202a?[-\\s]?[0-9]{3,4}\\u202a?[-\\s]?[0-9]{0,4})");
    private static final Pattern WECHAT_ID_REGEX = Pattern.compile("([a-zA-Z][0-9a-zA-Z-_\\s\\u202a]{5,20})");

    public static final void hookContacts(@NotNull LoadPackageParam param) {
        hookCanvasDrawText(param);
    }

    private static void hookCanvasDrawText(LoadPackageParam param) {
        ClassLoader classLoader = param.classLoader;
        //10,12,14,15,16,18,2,20,4,5,6,8 - DrawTextXc_Method_2
        //1,3,7,9,11,13,17,19 - DrawTextXc_Method_1

        XposedHelpers.findAndHookMethod(Canvas.class, "drawPosText", char[].class, int.class, int.class, float[].class, Paint.class, new DrawTextXc_Method());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawPosText", String.class, float[].class, Paint.class, new DrawTextXc_Method_2());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawText", char[].class, int.class, int.class, float.class, float.class, Paint.class, new DrawTextXc_Method());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawText", String.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawText", String.class, int.class, int.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawText", CharSequence.class, int.class, int.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawTextOnPath", char[].class, int.class, int.class, Path.class, float.class, float.class, Paint.class, new DrawTextXc_Method());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawTextOnPath", String.class, Path.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawTextRun", char[].class, int.class, int.class, int.class, int.class, float.class, float.class, boolean.class, Paint.class, new DrawTextXc_Method());
        XposedHelpers.findAndHookMethod(Canvas.class, "drawTextRun", CharSequence.class, int.class, int.class, int.class, int.class, float.class, float.class, boolean.class, Paint.class, new DrawTextXc_Method_2());
        try {
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawPosText", char[].class, int.class, int.class, float[].class, Paint.class, new DrawTextXc_Method());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawPosText", String.class, float[].class, Paint.class, new DrawTextXc_Method_2());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawText", char[].class, int.class, int.class, float.class, float.class, Paint.class, new DrawTextXc_Method());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawText", String.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawText", String.class, int.class, int.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawText", CharSequence.class, int.class, int.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawTextOnPath", char[].class, int.class, int.class, Path.class, float.class, float.class, Paint.class, new DrawTextXc_Method());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawTextOnPath", String.class, Path.class, float.class, float.class, Paint.class, new DrawTextXc_Method_2());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawTextRun", char[].class, int.class, int.class, int.class, int.class, float.class, float.class, Boolean.class, Paint.class, new DrawTextXc_Method());
            XposedHelpers.findAndHookMethod("android.view.RecordingCanvas", classLoader, "drawTextRun", CharSequence.class, int.class, int.class, int.class, int.class, float.class, float.class, Boolean.class, Paint.class, new DrawTextXc_Method_2());
        }
        catch(Throwable v1) {
            LogUtil.e("找不到android.view.RecordingCanvas");
        }
    }

    public static String hiddenPhoneNumber(String str) {
        return hiddenTextByRegex(str, PHONE_NUMBER_REGEX);
    }

    private static String hiddenTextByRegex(String str, Pattern pattern) {
        try {
            //String[] splits = str.split("\n");
            List<String> splits = StringsKt.split(str, new String[]{"\n"}, false, 6);
            StringBuilder re = new StringBuilder();
            for (String text : splits) {
                String telphone = text;
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    int groupCount = matcher.groupCount();
                    for (int idx = 0; idx < groupCount; idx++) {
                        String telNum = matcher.group(idx);
                        if (telNum != null) {
                            String header = telNum.substring(0, 3);
                            StringBuilder replace = new StringBuilder(header);
                            LogUtil.e("telNum= "+telNum);
                            LogUtil.e("telNum.length() = "+telNum.length());
                            replace.append(genMask(telNum.length() - 3));
                            telphone = StringsKt.replace(telphone, telNum, replace.toString(), false);
                        }
                    }
                }
                StringsKt.appendln(re.append(telphone));
                re.append(telphone);
            }
            return re.toString();
        } catch (Exception e) {
            Logger.e(e, "===== MATCHER ERROR");
            LogUtil.e("出现异常 "+e.getMessage());
            return "MATCHER ERROR: " + e.getMessage();
        }
    }

    private static String genMask(int count) {
        StringBuilder mask = new StringBuilder();
        int i = 1;
        if (1 <= count) {
            while (true) {
                mask.append("*");
                if (i == count) {
                    break;
                }
                i++;
            }
        }
        return mask.toString();
    }
}