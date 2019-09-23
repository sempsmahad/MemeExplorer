package com.example.memeexplorer.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.memeexplorer.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleImageActivity extends AppCompatActivity {


    @BindView(R.id.image)
    ImageView mImageViewerView;
    private Context context;
    ArrayList<String> paths;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        ButterKnife.bind(this);
        context = getApplicationContext();
        Intent it = getIntent();
        position = it.getIntExtra("position", 0);
        //Log.i("drgg", "onItemClick: " + position);
        paths = it.getStringArrayListExtra("paths");
        Log.i("drgg", "onItemClick: " + paths.size());
        Glide.with(this).load(new File(paths.get(position))).into(mImageViewerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.piv_menu_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(paths.get(position)));
                startActivity(Intent.createChooser(share, "Share Image"));
                return true;
            case R.id.pic_menu_save:
                saveImageToExternalStorage(MainActivity.convertPathToBitmap(paths.get(position)));
        }
        return true;
    }
    private void saveImageToExternalStorage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        File myDir = new File(root + "/Saved Memes");
        myDir.mkdirs();
        String fname = "Image-" + UUID.randomUUID() + ".jpg";
        File file = new File(myDir, fname);
        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            out.write(byteArrayOutputStream.toByteArray());

            //out.flush();
            out.close();
            Toast.makeText(SingleImageActivity.this, "saved", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[] { file.toString() }, null,
                (path, uri) -> {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                });

    }
}
