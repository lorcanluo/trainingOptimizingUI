package com.github.lorcan;

import android.app.Activity;
import android.os.Bundle;

/**
 * normal activity not using merge
 * <p/>
 * Created by luocan on 15/4/16.
 */
public class NormalActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_noraml);
    }
}
