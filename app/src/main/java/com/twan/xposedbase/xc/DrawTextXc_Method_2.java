package com.twan.xposedbase.xc;


import android.text.SpannableString;
import android.text.SpannableStringBuilder;

import com.twan.xposedbase.hook.HiddenWechatIdAndPhoneNumberHook;
import com.twan.xposedbase.util.LogUtil;

import de.robv.android.xposed.XC_MethodHook;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;

/**
 * @author Twan
 * @date 2019/4/10
 */
public class DrawTextXc_Method_2 extends XC_MethodHook {

    public DrawTextXc_Method_2() {
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);

        if (param.args == null || param.args.length ==0){
            return;
        }

        Object v0 = param.args[0];

        String phone="";

        if (v0 instanceof String){
            phone = (String)v0;
        }else if (v0 instanceof SpannableString){
            SpannableString param2 = (SpannableString)v0;
            phone = param2.toString();
        }else if (v0 instanceof SpannableStringBuilder){
            SpannableStringBuilder param3 = (SpannableStringBuilder)v0;
            phone = param3.toString();
        }else if (v0 instanceof CharSequence){
            CharSequence param4 = (CharSequence)v0;
            phone = param4.toString();
        }else if (v0 instanceof char[]){
            char[] param5 = (char[])v0;
            phone = new String(param5);
        }

        if(phone.length() >= 6) {
            param.args[0] = HiddenWechatIdAndPhoneNumberHook.hiddenPhoneNumber(phone);
        }


    }
}
