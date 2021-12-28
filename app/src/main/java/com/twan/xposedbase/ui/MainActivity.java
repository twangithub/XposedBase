package com.twan.xposedbase.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twan.xposedbase.R;
import com.twan.xposedbase.util.LogUtil;
import com.twan.xposedbase.util.ShellUtils;
import com.twan.xposedbase.util.XposedUtil;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XposedUtil.enableXposedModule(this);
        TextView tv_text = findViewById(R.id.tv_1);
        if (!isModuleActive()) {
            tv_text.setText("模块未启动");
            Toast.makeText(this, "模块未启动", LENGTH_LONG).show();
        } else {
            tv_text.setText("模块已启动");
            Toast.makeText(this, "模块已启动", LENGTH_LONG).show();
        }


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    boolean isRoot = ShellUtils.checkRootPermission();
                    if (isRoot) {
                        Toast.makeText(MainActivity.this, "已获取到root权限", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "获取不到root权限", Toast.LENGTH_SHORT).show();
                    }

                }
        });
    }

    private boolean isModuleActive() {
        return false;
    }
}
