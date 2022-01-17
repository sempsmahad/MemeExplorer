package com.example.memeexplorer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.memeexplorer.R;
import com.example.memeexplorer.adapter.AdapterFullScreenImage;
import com.example.memeexplorer.utilities.AdController;
import com.example.memeexplorer.utilities.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ActivityFullScreenImage extends AppCompatActivity {

    public static final String EXTRA_POS = "key.EXTRA_POS";
    public static final String EXTRA_IMGS = "key.EXTRA_IMGS";

    private AdapterFullScreenImage adapter;
    private ViewPager viewPager;
    private TextView text_page;

    private int position;
    private int activePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        viewPager = findViewById(R.id.pager);
        text_page = findViewById(R.id.text_page);

        ArrayList<String> items;
        Intent i = getIntent();

        position = i.getIntExtra(EXTRA_POS, 0);
        items = i.getStringArrayListExtra(EXTRA_IMGS);

        activePosition = position;
        adapter = new AdapterFullScreenImage(ActivityFullScreenImage.this, items);
        final int total = adapter.getCount();
        viewPager.setAdapter(adapter);

        text_page.setText(String.format(getString(R.string.image_of), (position + 1), total));

        // displaying selected image first
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                activePosition = pos;
                text_page.setText(String.format(getString(R.string.image_of), (pos + 1), total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        findViewById(R.id.btnClose).setOnClickListener(view -> finish());
        findViewById(R.id.btnSave).setOnClickListener(view -> {
            AdController.adCounter++;
            AdController.showInterAd(ActivityFullScreenImage.this, null, 0);
            try {
                Tools.exportFile(ActivityFullScreenImage.this,new File(items.get(activePosition)));
                Tools.toastIconInfo(ActivityFullScreenImage.this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        findViewById(R.id.btnShare).setOnClickListener(view -> {
            AdController.adCounter++;
            AdController.showInterAd(ActivityFullScreenImage.this, null, 0);
            Tools.share(ActivityFullScreenImage.this,new File(items.get(activePosition)));


        });

        // for system bar in lollipop
        Tools.systemBarLolipop(this);

        Tools.RTLMode(getWindow());
    }


}

