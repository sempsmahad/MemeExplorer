package com.example.memeexplorer.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memeexplorer.R;
import com.example.memeexplorer.adapter.AdapterGridBasic;
import com.example.memeexplorer.helpers.OcrDetectorProcessor;
import com.example.memeexplorer.memeClasses.Meme;
import com.example.memeexplorer.memeClasses.MemeLab;
import com.example.memeexplorer.utilities.AdController;
import com.example.memeexplorer.utilities.ArrayListSaverInterface;
import com.example.memeexplorer.utilities.Constants;
import com.example.memeexplorer.utilities.Tools;
import com.example.memeexplorer.utilities.TranslatorService;
import com.example.memeexplorer.widgets.SpacingItemDecoration;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DebugActivity extends AppCompatActivity {
    private View parent_view;
    private RecyclerView recyclerView;
    private List<Meme> mMemes;
    private static MemeLab mMemeLab;
    private static TextRecognizer mTextRecognizer;
    public ArrayList<String> pathsArray = new ArrayList<>();
    SearchView searchView;
    private AdView adView;
    private AdapterGridBasic adapter;

    public static void detectText(Context context, Frame frame, String location) {
        mTextRecognizer = new TextRecognizer.Builder(context).build();
        mTextRecognizer.setProcessor(new OcrDetectorProcessor());
        SparseArray<TextBlock> items = mTextRecognizer.detect(frame);
        String s = "";
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                s += item.getValue() + " ";
            }
            //scanStatus.setText(s);
        }
        Log.i("textss :", s);
        saveInDb(location, s);
    }

    private static void saveInDb(String location, String tag) {
        Meme m = new Meme(location, tag);
        mMemeLab.addMeme(m);
    }

    public static Bitmap convertPathToBitmap(String filepath) {
        File sd = Environment.getExternalStorageDirectory();
        File image = new File(filepath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        if (bitmap != null) {
            return Bitmap.createScaledBitmap(bitmap, 120, 120, true);
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_basic);
        parent_view = findViewById(android.R.id.content);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("OCR Gallery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        Tools.APP_DIR = Environment.getExternalStorageDirectory().getPath() +
                File.separator + "OCRGallery";
        File dst = new File(Tools.APP_DIR);

        if (!dst.exists()) {
            if (!dst.mkdir()) {
                Toast.makeText(DebugActivity.this, "Failed to create save path", Toast.LENGTH_SHORT).show();
            }
        }
        recyclerView = findViewById(R.id.recyclerView);
        adView = findViewById(R.id.adView);

        AdController.loadBannerAd(DebugActivity.this, adView);
        AdController.loadInterAd(DebugActivity.this);

        pathsArray = new ArrayListSaverInterface(getApplicationContext()).getUnFilteredImageListPaths();

        setListLayoutManager();
        adapter = new AdapterGridBasic(DebugActivity.this, pathsArray);
        adapter.setOnItemClickListener((view, meme, position) -> {
            AdController.adCounter++;
            AdController.showInterAd(DebugActivity.this, null, 0);
            viewSingleImage(position,pathsArray);
        });
        recyclerView.setAdapter(adapter);

        mMemeLab = MemeLab.get(DebugActivity.this);

        mTextRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        mTextRecognizer.setProcessor(new OcrDetectorProcessor());
//        if (!mTextRecognizer.isOperational()) {
//            startActivity(new Intent(DebugActivity.this,loadingActivity.class));
//        }
        Intent i = TranslatorService.newIntent(getApplicationContext());
        i.putExtra("receiver", new DownReceiver(new Handler()));
        getApplicationContext().startService(i);

    }

    private void viewSingleImage(int position, ArrayList<String> paths) {
        Intent intent = new Intent(DebugActivity.this, ActivityFullScreenImage.class);
        intent.putExtra(ActivityFullScreenImage.EXTRA_POS, position);
        intent.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, paths);
        startActivity(intent);
    }

    private void setListLayoutManager() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 2), true));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.menu_item_search);

        searchView = (SearchView) myActionMenuItem.getActionView();
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                if (query.isEmpty()){
                    adapter = new AdapterGridBasic(DebugActivity.this, pathsArray);
                    adapter.setOnItemClickListener((view, meme, position) -> {
                        AdController.adCounter++;
                        AdController.showInterAd(DebugActivity.this, null, 0);
                        viewSingleImage(position,pathsArray);
                    });
                    recyclerView.setAdapter(adapter);
                }else{
                    GetMatchingPics matchingPics = new GetMatchingPics(query);
                    matchingPics.execute();
                }

                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                Log.e("queryTextSubmit", query);
                if (query.isEmpty()){
                    adapter = new AdapterGridBasic(DebugActivity.this, pathsArray);
                    adapter.setOnItemClickListener((view, meme, position) -> {
                        AdController.adCounter++;
                        AdController.showInterAd(DebugActivity.this, null, 0);
                        viewSingleImage(position,pathsArray);
                    });
                    recyclerView.setAdapter(adapter);
                }else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                return true;
            }
        };
        searchView.setOnQueryTextListener(listener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    class GetMatchingPics extends AsyncTask<String, String, ArrayList<String>> {
        String query;

        public GetMatchingPics(String p) {
            super();
            query = p;
        }

        @Override
        protected void onPostExecute(ArrayList<String> newPaths) {
            adapter = new AdapterGridBasic(DebugActivity.this, newPaths);
            adapter.setOnItemClickListener((view, meme, position) -> {
                AdController.adCounter++;
                AdController.showInterAd(DebugActivity.this, null, 0);
                viewSingleImage(position,newPaths);

//                Intent intent = new Intent(DebugActivity.this, SingleImageActivity.class);
//                intent.putStringArrayListExtra("paths", newPaths);
//                intent.putExtra("position", position);
//                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            Log.e("queryText", query);
            ArrayList<String> newPaths = new ArrayList<>();
            if (!query.isEmpty()) {
                mMemes = mMemeLab.getMemes(query);
                String tc = "";
                for (int m = 0; m < mMemes.size(); m++) {
                    Log.e("was called ", m + " ");
                    newPaths.add(mMemes.get(m).getLocation());
                    tc += mMemes.get(m).getLocation() + "\n";
                }
            }
            return newPaths;
        }
    }

    @SuppressLint("RestrictedApi")
    private class DownReceiver extends ResultReceiver {
        public DownReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.NEW_PROGRESS) {
                int progress = resultData.getInt("progress");
                if (progress == 100) {
//                    getProgressBar().setVisibility(View.GONE);
                } else {
//                    getProgressBar().setProgress(progress);
                }
            }
        }
    }
}
