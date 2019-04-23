package com.twan.xposedbase.xc;


import com.twan.xposedbase.hook.HiddenWechatIdAndPhoneNumberHook;

import de.robv.android.xposed.XC_MethodHook;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function1;

/**
 * @author Twan
 * @date 2019/4/10
 */
public class DrawTextXc_Method extends XC_MethodHook {

    public DrawTextXc_Method() {
        //this.$filter = arg1;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        if (param.args == null || param.args.length ==0){
            return;
        }
        char[] arg = (char[]) param.args[0];
        if (arg.length >= 6) {
            Object[] objArr = param.args;
            String str = HiddenWechatIdAndPhoneNumberHook.hiddenPhoneNumber(new String(arg));
            char[] toCharArray = str.toCharArray();
            objArr[0] = toCharArray;
        }

    }
}
