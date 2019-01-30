package com.lzp.luckymoney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lzp.luckymoney.xposed.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
                    Observable.just(isChecked)
                            .doOnSubscribe(v -> SettingUtil.updateSetting(MainActivity.this, LuckyMoneySettingProvider.GRAB, isChecked)).
                            subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();

                }
        );

        mTxtGrag = findViewById(R.id.grab_text);

        Observable.just(false)
                .map(v -> SettingUtil.getSetting(MainActivity.this, LuckyMoneySettingProvider.GRAB))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(checked -> mSwtGrag.setChecked(checked));
    }
}
