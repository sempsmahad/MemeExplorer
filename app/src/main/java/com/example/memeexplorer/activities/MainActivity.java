package com.example.memeexplorer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.memeexplorer.R;
import com.example.memeexplorer.helpers.OcrDetectorProcessor;
import com.example.memeexplorer.helpers.ProgressActivity;
import com.example.memeexplorer.memeClasses.Meme;
import com.example.memeexplorer.memeClasses.MemeLab;
import com.example.memeexplorer.utilities.ArrayListSaverInterface;
import com.example.memeexplorer.utilities.Constants;
import com.example.memeexplorer.utilities.Function;
import com.example.memeexplorer.utilities.TranslatorService;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends ProgressActivity {
    public ArrayList<String> pathsArray = new ArrayList<String>();
    static GridView resultsList;
    private static TextRecognizer mTextRecognizer;
    private Context context;
    private static MemeLab mMemeLab;
    private List<Meme> mMemes;
    static final int REQUEST_PERMISSION_KEY = 1;
    SearchView searchView;
    static View parent2;
    private AlbumAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = getApplicationContext();
        resultsList = findViewById(R.id.resultsList);
        mMemeLab = MemeLab.get(MainActivity.this);
//        parent2 = (View) (findViewById(R.id.thumb_pic)).getParent();

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }else {
            mTextRecognizer = new TextRecognizer.Builder(context).build();
            mTextRecognizer.setProcessor(new OcrDetectorProcessor());
            if (!mTextRecognizer.isOperational()) {
                startActivity(new Intent(MainActivity.this,loadingActivity.class));
            }
        }
        Intent i = TranslatorService.newIntent(context);
        i.putExtra("receiver", new DownReceiver(new Handler()));
        context.startService(i);

        pathsArray = new ArrayListSaverInterface(context).getPathsArray();


        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);
        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            resultsList.setColumnWidth(Math.round(px));
        }

    }
    @Override
    protected void onResume() {
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }else {
            mTextRecognizer = new TextRecognizer.Builder(context).build();
            mTextRecognizer.setProcessor(new OcrDetectorProcessor());
            if (!mTextRecognizer.isOperational()) {
                startActivity(new Intent(MainActivity.this,loadingActivity.class));
            }
        }
        Intent i = TranslatorService.newIntent(context);
        i.putExtra("receiver", new DownReceiver(new Handler()));
        context.startService(i);
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.menu_item_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                GetMatchingPics matchingPics = new GetMatchingPics(query);
                matchingPics.execute();
                return true;
            }
            public boolean onQueryTextSubmit(String query) {
                Log.e("queryTextSubmit", query);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        };
        searchView.setOnQueryTextListener(listener);
        return true;
    }
    class GetMatchingPics extends AsyncTask<String,String,ArrayList<String>>{
        String query;
        public GetMatchingPics(String p) {
            super();
            query = p;
        }
        @Override
        protected void onPostExecute(ArrayList<String> newPaths) {
            AlbumAdapter adapter = new AlbumAdapter(MainActivity.this, newPaths);
            resultsList.setAdapter(adapter);
            resultsList.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(MainActivity.this, SingleImageActivity.class);
                // Log.i("drgg", "onItemClick: "+newPaths.size());
                intent.putStringArrayListExtra("paths", newPaths);
                intent.putExtra("position",i);
                startActivity(intent);
            });
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {

            Log.e("queryText",query);
            ArrayList<String> newPaths = new ArrayList<>();
            if (!query.isEmpty()) {
                mMemes = mMemeLab.getMemes(query);
                String tc = "";
                for (int m = 0; m < mMemes.size(); m++) {
                    Log.e("was called ", m+" ");
                    newPaths.add(mMemes.get(m).getLocation());
                    tc += mMemes.get(m).getLocation() + "\n";
                }
            }
            return newPaths;
        }
    }
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
    public static Bitmap convertPathToBitmap(String filepath){
//            File image = new File(filepath);
//            View parent = (View) resultsList.getParent();
//
//        DisplayMetrics metrics = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) get.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inSampleSize = calculateSampleSize(bmOptions.outWidth, bmOptions.outHeight, parent.getWidth(),
//                parent.getHeight());
//        Bitmap bitmap = null;
//        try {
//            bitmap = BitmapFactory.decodeStream(new FileInputStream(image));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
//            return bitmap;


//        File sd = Environment.getExternalStorageDirectory();
//        File image = new File(sd+filePath, imageName);
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
//        return Bitmap.createScaledBitmap(bitmap,parent2.getWidth(),parent2.getHeight(),true);
//        return bitmap;
//         BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 4;
//        return BitmapFactory.decodeFile(filepath, options);
return null;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mTextRecognizer = new TextRecognizer.Builder(context).build();
                    mTextRecognizer.setProcessor(new OcrDetectorProcessor());
                    if (!mTextRecognizer.isOperational()) {
                        startActivity(new Intent(MainActivity.this,loadingActivity.class));
                    }
//                    getPathsArray();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }
           

        }
    }
    public String getBytesFromBitmap(Bitmap bitmap) {
        String str = "";
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            //byte[] bytes = {...}
            str += new String(stream.toByteArray(), "UTF-8"); // for UTF-8 encoding

        } catch (IOException ioe) {
            //Handle exception here, most of the time you will just log it.
        }
        return str;
    }
    class AlbumAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<Meme> data = new ArrayList<>();
        public AlbumAdapter(Activity a, ArrayList<String> d) {
            activity = a;
            for (int k = 0;k<d.size();k++){
                Meme meme = mMemeLab.get(context).getMeme(d.get(k));
                data.add(meme);
            }
        }
        public int getCount() {
            return data.size();
        }
        public Object getItem(int position) {
            return data.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            AlbumViewHolder holder = null;
            if (convertView == null) {
                holder = new AlbumViewHolder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.thumb, parent, false);
                holder.galleryImage = convertView.findViewById(R.id.thumb_pic);
                convertView.setTag(holder);
            } else {
                holder = (AlbumViewHolder) convertView.getTag();
            }
            holder.galleryImage.setId(position);
//            holder.galleryImage.setOnClickListener(view -> {
//
//            });
            String song = data.get(position).getLocation();
            try {
                Log.e("was called ", song);
                Glide.with(activity)
                        .load(new File(song)).into(holder.galleryImage);
            } catch (Exception e) {
                Log.e("was called ", "failed");
            }
            return convertView;
        }}
    class AlbumViewHolder {
        ImageView galleryImage;

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
                if (progress == 100){
                    getProgressBar().setVisibility(View.GONE);
                }else {
                    getProgressBar().setProgress(progress);
                }
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) {
//            return;
//        } else {
//            Uri treeUri = data.getData();
//            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
//            grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        }
//    }
}

