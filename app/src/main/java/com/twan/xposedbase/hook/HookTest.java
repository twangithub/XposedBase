package com.twan.xposedbase.hook;

import android.app.Application;
import android.content.Intent;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Twan
 */
public class HookTest {

    public static void hook(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            XposedHelpers.findAndHookConstructor(loadPackageParam.classLoader.loadClass("com.tencent.mm.app.MMApplicationLike"), Application.class, int.class, boolean.class, long.class, long.class, Intent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //dosth here
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    //dosth here
                }
            });
        }
    }
}
