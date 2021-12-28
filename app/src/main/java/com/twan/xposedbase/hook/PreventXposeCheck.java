package com.twan.xposedbase.hook;

import android.os.Environment;

import com.twan.xposedbase.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PreventXposeCheck {
    public static String[] skips = {"de.robv.android.xposed.installer", "kaiqi.cn.xposed003","com.alibaba.android.rimet","libxposed_art.so"};

    public static void hook(XC_LoadPackage.LoadPackageParam param) {
        try {
            XposedHelpers.findAndHookMethod(File.class, "exists", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (((File) param.thisObject).getPath().endsWith("su")) {
                        param.setResult(false);
                    }
                }
            });
            XposedBridge.hookMethod(XposedHelpers.findConstructorExact(File.class, String.class, String.class), new XC_MethodHook(10000) {
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (((String) param.args[1]).endsWith("su")) {
                        param.args[1] = "/system/xbin/FAKEJUNKFILE";
                    }
                }
            });
            XposedHelpers.findAndHookConstructor(File.class, String.class, new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam param) {
                    String path = (String) param.args[0];
                    boolean shouldDo = path.matches("/proc/[0-9]+/maps") || (path.toLowerCase().contains("xposed") && !path.startsWith(Environment.getExternalStorageDirectory().getPath()) && !path.contains("fkzhang"));
                    if (shouldDo) {
                        param.args[0] = "/system/build.prop";
                    }
                }
            });
            XposedHelpers.findAndHookMethod(File.class, "list",new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam param) {
                    String[] fs = (String[]) param.getResult();
                    if (fs != null) {
                        List<String> list = new ArrayList<>();
                        int length = fs.length;
                        for (int i = 0; i < length; i++) {
                            String f = fs[i];
                            if (!f.toLowerCase().contains("xposed") && !f.equals("su")) {
                                list.add(f);
                            }
                        }
                        param.setResult(list.toArray(new String[0]));
                    }
                }
            });

            XposedHelpers.findAndHookMethod("java.lang.Runtime", param.classLoader, "exec", String[].class, String[].class, File.class, new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) {
                    try {
                        String[] arrayOfString1 = (String[]) param.args[1];
                        String cmd1 = Arrays.toString((String[]) param.args[0]);
                        String cmd2 = Arrays.toString(arrayOfString1);
                        if (!StringUtils.isEmpty(cmd1) && cmd1.contains("su")) {
                            param.setThrowable(new IOException());
                        }
                        if (!StringUtils.isEmpty(cmd2) && cmd2.contains("su")) {
                            param.setThrowable(new IOException());
                        }
                        if (!StringUtils.isEmpty(cmd1) && cmd1.contains("adb_enabled")) {
                            param.setThrowable(new IOException());
                        }
                        if (!StringUtils.isEmpty(cmd2) && cmd2.contains("adb_enabled")) {
                            param.setThrowable(new IOException());
                        }
                    } catch (Exception e) {
                    }
                }
            });

            Class cls = null;
            try {
                cls = Runtime.getRuntime().exec("echo").getClass();
            } catch (IOException e) {
                XposedBridge.log("[W/XposedHider] Cannot hook Process#getInputStream");
            }
            if (cls != null) {
                XposedHelpers.findAndHookMethod(cls, "getInputStream",new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void afterHookedMethod(MethodHookParam param) {
                        InputStream is = (InputStream) param.getResult();
                        if (is instanceof FilterXpInputStream) {
                            param.setResult(is);
                        } else {
                            param.setResult(new FilterXpInputStream(is));
                        }
                    }
                });
            }

            XC_MethodHook hookClass = new XC_MethodHook() {
                public void beforeHookedMethod(MethodHookParam param) {
                    if(param.args != null && param.args[0] != null && param.args[0].toString().startsWith("de.robv.android.xposed.")){
                        param.args[0] = "com.tencent.mm.Test";
                    }
                }
            };

            XposedBridge.hookAllMethods(System.class, "getenv", new XC_MethodHook() {
                public void afterHookedMethod(MethodHookParam param) {
                    if (param.args.length == 0) {
                        param.setResult(filter((String) ((Map) param.getResult()).get("CLASSPATH")));
                    } else if ("CLASSPATH".equals(param.args[0])) {
                        param.setResult(filter((String) param.getResult()));
                    }
                }

                private String filter(String s) {
                    List<String> list = Arrays.asList(s.split(":"));
                    List<String> clone = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.get(i).toLowerCase().contains("xposed")) {
                            clone.add(list.get(i));
                        }
                    }
                    StringBuilder res = new StringBuilder();
                    for (int i2 = 0; i2 < clone.size(); i2++) {
                        res.append(clone);
                        if (i2 != clone.size() - 1) {
                            res.append(":");
                        }
                    }
                    return res.toString();
                }
            });

            try {
                XposedHelpers.findAndHookMethod(Runtime.class, "exec", String.class,new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        hookGetInput(param);
                    }
                });
            }catch (Exception e){

            }

            try {
                XposedHelpers.findAndHookMethod(Runtime.class, "exec", String.class, String[].class,new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        hookGetInput(param);
                    }
                });
            }catch (Exception e){

            }

        } catch (Exception e) {

        }

        try {
            XposedHelpers.findAndHookMethod(BufferedReader.class, "readLine", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String result = (String) param.getResult();
                    if(result != null) {
                        if (result.contains("Xposed")) {
                            param.setResult("");
                            new File("").lastModified();
                        }
                    }
                }
            });
        }catch (Exception e){

        }

    }


    private static void hookGetInput(XC_MethodHook.MethodHookParam param) {
        try {
            XposedHelpers.findAndHookMethod(param.getResult().getClass(), "getInputStream",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            InputStream input = (InputStream) param.getResult();
                            BufferedReader bis = new BufferedReader(new InputStreamReader(input));
                            String line = null;
                            StringBuilder sb = new StringBuilder();
                            while ((line = bis.readLine()) != null) {
                                boolean flg = false;
                                for (String key : skips) {
                                    if (line.contains(key)) {
//                                        Log.e("-------","包名命中....."+key);
                                        flg = true;
                                        break;
                                    }
                                }
                                if (flg) {
                                    continue;
                                }
                                sb.append(line + "\n");
                            }
                            InputStream result = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
                            param.setResult(result);
                        }

                    });
        } catch (Throwable e) {

        }
    }
}
