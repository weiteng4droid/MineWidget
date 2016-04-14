package com.botpy.demo.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.botpy.demo.R;
import com.botpy.demo.widget.TagLayout;

public class TestViewGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view_group);

        TagLayout tagLayout = (TagLayout) findViewById(R.id.tag_layout);
        LayoutInflater layoutInflater = getLayoutInflater();
        String tag;
        for (int i = 0; i <= 23; i++) {
            tag = "tag" + i;
            View tagView = layoutInflater.inflate(R.layout.layout_tag_container, null, false);

            TextView tagTextView = (TextView) tagView.findViewById(R.id.tagTextView);
            tagTextView.setText(tag);
            tagLayout.addView(tagView);
        }
    }
}
