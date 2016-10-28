package com.scu.ntest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.scu.ntest.account.AccountHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccountHelper.addAppstoreAccount(this);//通过账号拉活初始化
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyNtestService.class);
//                intent.setAction("com.qihoo.ntest.pushmanager.PushBrowserService");
                startService(intent);
            }
        });
    }
}
