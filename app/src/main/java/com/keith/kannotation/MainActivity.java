package com.keith.kannotation;

import android.os.Bundle;

import com.keith.annotations.hello.HelloTest;
import com.keith.annotations.router.ClassLink;

import androidx.appcompat.app.AppCompatActivity;

@ClassLink("MainActivity")
@HelloTest()
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}