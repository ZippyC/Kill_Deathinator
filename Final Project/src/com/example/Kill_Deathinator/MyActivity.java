package com.example.Kill_Deathinator;

import android.app.Activity;
import android.os.Bundle;
import java.io.*;
import java.util.*;

public class MyActivity extends Activity{
    /**
     * Called when the activity is first created.
     */
    @Override
        public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }
}