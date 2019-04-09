package com.twan.xposedbase.hook;

import com.twan.xposedbase.BuildConfig;
import com.twan.xposedbase.ui.MainActivity;
import com.twan.xposedbase.util.LogUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Twan
 */
public class HookMain implements IXposedHookLoadPackage {

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

        HookTest.hook(llpm);
    }
}
