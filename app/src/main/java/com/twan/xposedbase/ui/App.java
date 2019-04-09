package com.twan.xposedbase.ui;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * @author Twan
 * @date 2019/4/9
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(Constant.TAG)
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }
}
