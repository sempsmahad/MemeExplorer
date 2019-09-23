package com.example.memeexplorer.helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memeexplorer.R;

public abstract class ProgressActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;

    @Override
    public void setContentView(View view) {
        init().addView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID,init(),true);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        init().addView(view,params);
    }

    private ViewGroup init(){
        super.setContentView(R.layout.progress);
        mProgressBar = findViewById(R.id.activity_bar);
        return (ViewGroup) findViewById(R.id.activity_frame);
    }

    protected ProgressBar getProgressBar(){
        return mProgressBar;
    }
}
