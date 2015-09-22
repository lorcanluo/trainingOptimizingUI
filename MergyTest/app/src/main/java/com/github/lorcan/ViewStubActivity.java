package com.github.lorcan;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

/**
 * Created by luocan on 15/4/16.
 */
public class ViewStubActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stub);
        final ViewStub importPanel = ((ViewStub) findViewById(R.id.stub_import));

        findViewById(R.id.btn_stub_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               importPanel.inflate();
                importPanel.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_stub_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importPanel.setVisibility(View.GONE);
            }
        });
    }
}
