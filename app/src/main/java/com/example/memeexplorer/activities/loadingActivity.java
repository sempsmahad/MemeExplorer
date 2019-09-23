package com.example.memeexplorer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memeexplorer.helpers.OcrDetectorProcessor;
import com.example.memeexplorer.R;
import com.google.android.gms.vision.text.TextRecognizer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class loadingActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.textView)
    TextView textView;
    private TextRecognizer mTextRecognizer;
    private Context context;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        context = getApplicationContext();
        handler = new Handler();
        handler.postDelayed(runnable, 5000);
        mTextRecognizer = new TextRecognizer.Builder(context).build();
        mTextRecognizer.setProcessor(new OcrDetectorProcessor());
        if (!mTextRecognizer.isOperational()) {
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;
            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                //Log.w(TAG, getString(R.string.low_storage_error));
            }else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mTextRecognizer.isOperational()){
                            startActivity(new Intent(loadingActivity.this,MainActivity.class));
                        }else {
                            handler.postDelayed(this, 5000);
                        }
                    }
                },5000);
            }
        }else {
            startActivity(new Intent(loadingActivity.this,MainActivity.class));
        }


    }
}
