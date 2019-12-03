package com.sorezel.andruino_bluetooth.invernadero;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.sorezel.andruino_bluetooth.R;

public class testActivity extends AppCompatActivity {

    Handler handler = new Handler();
    CircularProgressBar p1,p2,p3,p4;
    int v1=0,v2=0,v3=0,v4=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_progress);

    }
}
