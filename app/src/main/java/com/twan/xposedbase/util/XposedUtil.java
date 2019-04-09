package com.twan.xposedbase.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.DataOutputStream;
import java.util.List;

public final class XposedUtil {

    /**
     * 执行adb shell 命令
     *
     * @param command command
     * @return 执行是否成功
     */
    public static boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 启动服务
     *
     * @param context context
     * @param intent  intent
     */
    public static synchronized void startService(Context context, Intent intent) {
        //Android 8.0以上开启服务方式
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * 自动勾选xopose模块
     *
     * @param context context
     */
    public static void enableXposedModule(Context context) {
        //启动xposed服务
        RootCommand("am startservice -n de.robv.android.xposed.installer/de.robv.android.xposed.installer.InstallService");
        Intent intent = new Intent();
        intent.setClassName("de.robv.android.xposed.installer", "de.robv.android.xposed.installer.InstallService");
        intent.putExtra("xposed_module", context.getPackageName());
        startService(context, intent);
    }

    /**
     * 检测包名是否存在
     *
     * @param context     context
     * @param packageName 包名
     * @return boolean
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            // 循环判断是否存在指定包名
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否隐藏xposed
     * @param state 是否
     */
    public static void hiddenXposed(final boolean state) {
        if (state) {
            RootCommand("pm disable de.robv.android.xposed.installer/de.robv.android.xposed.installer.WelcomeActivity");
        } else {
            RootCommand("pm enable de.robv.android.xposed.installer/de.robv.android.xposed.installer.WelcomeActivity");
        }
    }
}
