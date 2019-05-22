package com.example.comicapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.comicapp.R;
import com.just.agentweb.AgentWeb;

public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        AgentWeb.with(this)
                .setAgentWebParent(new LinearLayout(this), new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go("https://manhua.fzdm.com/2//937/index_2.html");
    }
}
