package com.example.memeexplorer.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.memeexplorer.BuildConfig;
import com.example.memeexplorer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Tools {
    public static String APP_DIR;


    public static void RTLMode(Window window) {
        if (AppConfig.RTL_LAYOUT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public static void displayImageOriginal(Context ctx, ImageView img, String url) {

        try {
             Glide.with(ctx).asBitmap().load(new File(url))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(img);
        } catch (Exception e) {
        }
    }


    public static void setSystemBarColor(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static void systemBarLolipop(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void toastIconInfo(AppCompatActivity ctx) {
        Toast toast = new Toast(ctx);
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = ctx.getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText("Saved");
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_baseline_done_all);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(ctx.getResources().getColor(R.color.blue_500));

        toast.setView(custom_view);
        toast.show();
    }

    public static File exportFile(Context ctx,File src) throws IOException {
//        String dstPath = Environment.getExternalStorageDirectory() + File.separator + "myApp" + File.separator;
        String dstPath = Tools.APP_DIR + File.separator;
        File dst = new File(dstPath);
        String extension = src.getAbsolutePath().substring(src.getAbsolutePath().lastIndexOf("."));

        //if folder does not exist
        if (!dst.exists()) {
            if (!dst.mkdir()) {
                return null;
            }
        }

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File expFile;
        if (isImageFile(src.getPath())) {
            expFile = new File(dst.getPath() + File.separator + "IMG_" + timeStamp + extension);

        } else {
            expFile = new File(dst.getPath() + File.separator + "VID_" + timeStamp + extension);

        }
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }

    public static void share(Context ctx,File file){
        Intent shareIntent = new Intent();
        Uri uri  = Uri.fromFile(file);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uriNew  = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID,file);
        shareIntent.putExtra(Intent.EXTRA_STREAM,uriNew);
        shareIntent.setType(getMimeType(ctx,uri));
        ctx.startActivity(Intent.createChooser(shareIntent, "Share image"));
    }

    public static String getMimeType(Context ctx,Uri uri) {
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = ctx.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


}
