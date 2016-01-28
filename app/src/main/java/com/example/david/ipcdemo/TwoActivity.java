package com.example.david.ipcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author WeiDeng
 * @FileName com.example.david.ipcdemo.TwoActivity.java
 * @date 2016-01-28 22:07
 * @describe
 */
public class TwoActivity extends Activity {

    public final String TAG = getClass().getSimpleName();

    private TextView contentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        contentTv = (TextView) findViewById(R.id.content_name_tv);
        contentTv.setText(TAG);

        contentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TwoActivity.this, ThreeActivity.class);
                startActivity(intent);
            }
        });

    }
}
