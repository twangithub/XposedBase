package com.twan.xposedbase.hook;

import android.app.Application;
import android.content.Context;

import com.twan.xposedbase.BuildConfig;
import com.twan.xposedbase.util.LogUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * @author Twan
 */
public class HookMain implements IXposedHookLoadPackage {
    public static Context applicationContext = null;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam llpm) throws Throwable {
        LogUtil.e("打印包名:" + llpm.packageName + " 当前线程：" + Thread.currentThread().getName() + " 当前类地址：" + HookMain.this.toString());
        try {
            if (llpm.packageName.equals(BuildConfig.APPLICATION_ID)) {
                XposedHelpers.findAndHookMethod("com.twan.xposedbase.ui.MainActivity", llpm.classLoader,
                        "isModuleActive", XC_MethodReplacement.returnConstant(true));
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }

        //hook application
        if (applicationContext == null) {
            LogUtil.e("开始hook applicationContext");
            applicationContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");

            if (applicationContext == null) {
                findAndHookMethod("android.content.ContextWrapper", llpm.classLoader, "getApplicationContext", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        applicationContext = (Context) param.getResult();
                    }
                });
            }
        }

        if (applicationContext != null){
            LogUtil.e("已成功hook到application");
        }else {
            LogUtil.e(" hook application 失败");
        }

        // hook微信
        if (llpm.packageName.equals("com.tencent.mm") ||
                "/data/user/0/com.tencent.mm".equals(llpm.appInfo.dataDir) ||
                "/data/data/com.tencent.mm".equals(llpm.appInfo.dataDir)) {
            PreventXposeCheck.hook((llpm));
            LogUtil.e("已进入微信进程");

            // 解决多dex出现ClassNotFound的问题
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    // todo your own business




                }
            });



        }

    }
}
