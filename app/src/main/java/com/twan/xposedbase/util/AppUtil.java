package com.twan.xposedbase.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

public class AppUtil {
	/**
	 * 检查 包名对应的程序 是否安装
	 * @param context 
	 * @param packageName 包名
	 * @return
	 */
	public static boolean isAppInstalled(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}
	/**
	 * 安装app
	 * @param context
	 * @param path apk的路径
	 */
	public static void install(Context context, String path) {
		// 核心是下面几句代码
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 获取单个App版本号
	 * @param context
	 * @param packageName app包名
	 * @return 版本号
	 */
	public static String getAppVersion(Context context, String packageName)
			throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
		String appVersion = packageInfo.versionName;
		return appVersion;
	}

	/**
	 * 获取app的md5
	 * @param context
	 * @param packageName  app包名
	 * @return app的Md5
	 */
	public static String getAppMd5(Context context, String packageName)
			throws NameNotFoundException {
		String path = context.getPackageManager().getApplicationInfo(
				packageName, 0).sourceDir;
		String md5 = Md5Util.getFileMD5(path);
		return md5;
	}

	/**
	 * 移动到system/app下
	 * 
	 * @param context
	 * @throws Exception
	 */
	public static void convertSysApp(Context context, String dstFileName)
			throws Exception {
		ShellUtils.execCommand("mount -o rw,remount /system", true);
		String fromPath = context.getPackageManager().getApplicationInfo(
				context.getPackageName(), 0).sourceDir;
		if (Build.VERSION.SDK_INT > 20) {
			File installApk = new File(fromPath);
			fromPath = installApk.getParentFile().getPath();
			String toPath = fromPath.replace("data/", "system/");
			String toPathHeader = toPath.substring(0,
					toPath.lastIndexOf("/") + 1);
			toPath = toPathHeader + dstFileName;
			String cmd = "cp -r " + fromPath + " " + toPath;
			ShellUtils.execCommand(cmd, true);
			ShellUtils.execCommand("chmod -R 777 " + toPath, true);
			ShellUtils.execCommand("rm -rf " + fromPath, true);
		} else {
			String toPath = fromPath.replace("data/", "system/");
			String cmd = "cp " + fromPath + " " + toPath;
			ShellUtils.execCommand(cmd, true);
			ShellUtils.execCommand("chmod 777 " + toPath, true);
			ShellUtils.execCommand("rm -rf " + fromPath, true);
		}
		ShellUtils.execCommand("reboot -p", true);
	}

	/**
	 * 重新移动回data/app
	 * 
	 * @param context
	 * @param dstFileName
	 * @throws Exception
	 */
	public static void clearSysApp(Context context, String dstFileName)
			throws Exception {
		ShellUtils.execCommand("mount -o rw,remount /system", true);
		String fromPath = context.getPackageManager().getApplicationInfo(
				context.getPackageName(), 0).sourceDir;
		if (Build.VERSION.SDK_INT > 20) {
			File installApk = new File(fromPath);
			fromPath = installApk.getParentFile().getPath();
			String toPath = fromPath.replace("data/", "system/");
			String toPathHeader = toPath.substring(0,
					toPath.lastIndexOf("/") + 1);
			toPath = toPathHeader + dstFileName;
			String cmd = "rm -rf " + toPath;
			ShellUtils.execCommand(cmd, true);
		} else {
			String toPath = fromPath.replace("data/", "system/");
			String cmd = "rm " + toPath;
			ShellUtils.execCommand(cmd, true);
		}
		ShellUtils.execCommand("reboot -p", true);
	}

	/**
	 * 检查手机是否root
	 * @return
	 */
	public static boolean isRoot() {
		String binPath = "/system/bin/su";  
		String xBinPath = "/system/xbin/su";  
		if (new File(binPath).exists() && isExecutable(binPath)) 
			return true;
		if (new File(xBinPath).exists() && isExecutable(xBinPath)) 
			return true; 
		return false; 
	} 


	private static boolean isExecutable(String filePath) { 
		Process p = null;         
		try { 
			p = Runtime.getRuntime().exec("ls -l " + filePath);            
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String str = in.readLine();
			if (str != null && str.length() >= 4) { 
				char flag = str.charAt(3); 
				if (flag == 's' || flag == 'x') 
					return true; 
			}      
		} catch (IOException e) { 
			e.printStackTrace(); 
		}finally{ 
			if(p!=null){
				p.destroy();
			}          
		}
		return false;
	}

	/**
	 * 静默安装
	 * @param apkPath apk路径
	 * @return
	 */
	public static boolean slientInstall(String apkPath){
		ShellUtils.CommandResult res = ShellUtils.execCommand("pm install -r "+apkPath, true);
		if(res.successMsg.contains("Success")||res.successMsg.contains("success")){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 获取手机所有app浏览器
	 * @param context
	 * @return
	 */
	public static List<ResolveInfo> getAllBrowsableApp(Context context){
		String default_browser = "android.intent.category.DEFAULT";
		String browsable = "android.intent.category.BROWSABLE";
		String view = "android.intent.action.VIEW";
		Intent intent = new Intent(view);
		intent.addCategory(default_browser);
		intent.addCategory(browsable);
		Uri uri = Uri.parse("http://");
		intent.setDataAndType(uri, null);
		List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
		return resolveInfoList;
	}
	
	public static void uninstall(Context context,String pkgName){
		Intent uninstallIntent = new Intent();  
		uninstallIntent.setAction(Intent.ACTION_DELETE);  
		uninstallIntent.setData(Uri.parse("package:"+pkgName));  
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);  
	}
}
