package com.lzp.luckymoney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Switch mSwtGrag;
    private TextView mTxtGrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mSwtGrag = findViewById(R.id.grab);
        mSwtGrag.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        mTxtGrag.setTextColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        mTxtGrag.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
            SettingUtil.updateSetting(MainActivity.this, LuckyMoneySettingProvider.GRAB, isChecked);
                }
        );

        mTxtGrag = findViewById(R.id.grab_text);

        SettingUtil.getSetting(MainActivity.this, LuckyMoneySettingProvider.GRAB);
    }
}
