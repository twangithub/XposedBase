package com.twan.xposedbase.util;

import android.util.SparseArray;

import java.util.List;

public class StringUtils {
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        try {
            String str = String.valueOf(obj);
            if (str.equals("null") || str.equals("")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            String str = String.valueOf(obj).trim();
            if (str.equals("") || str.equals("null")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> boolean isListNotEmpty(List<T> list) {
        if (list == null) {
            return false;
        }
        try {
            if (list.size() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> boolean isSparseArrayNotEmpty(SparseArray<T> sparseArray) {
        if (sparseArray == null) {
            return false;
        }
        try {
            if (sparseArray.size() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String isStrNullTo(String str, String str2) {
        return str != null ? str : str2;
    }


}
