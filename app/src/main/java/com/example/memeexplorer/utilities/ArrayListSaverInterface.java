package com.example.memeexplorer.utilities;

import static com.example.memeexplorer.utilities.Constants.SHARED_PREFS_FILE;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.memeexplorer.helpers.TinyDB;
import com.example.memeexplorer.memeClasses.ImageDataModel;
import com.example.memeexplorer.memeClasses.Meme;
import com.example.memeexplorer.memeClasses.MemeLab;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ArrayListSaverInterface {
    private ArrayList<String> newArray;
    public static ArrayList<ImageDataModel> allImages = new ArrayList<ImageDataModel>();

    //   private ArrayList<String> oldArray;
    private Context mContext;
    private int c = 0;
    private TinyDB tinydb;

    public ArrayListSaverInterface(Context context) {
        mContext = context;
        newArray = new ArrayList<>();
        tinydb = new TinyDB(mContext);
    }
//    public ArrayListSaverInterface(Context context) {
//        mContext = context;
//        newArray = new ArrayList<>();
//        tinydb = new TinyDB(mContext);
//    }


    public ArrayList<String> getUnFilteredImageListPaths() {
        ArrayList<ImageDataModel> images = new ArrayList<>();
        ArrayList<String> pathArray = new ArrayList<>();
        images = gettAllImages(mContext);


        for (ImageDataModel image : images) {
            if (new File(image.getImagePath()).exists()  && isImageFile(image.getImagePath())) {
                pathArray.add(image.getImagePath());
            }

        }
        return pathArray;

    }
    public ArrayList<String> getPathsArray() {
        allImages = gettAllImages(mContext);
        for (ImageDataModel image : allImages) {
            if (new File(image.getImagePath()).exists()) {
                newArray.add(image.getImagePath());
            }
        }

//        String[] STAR = {"*"};
//        String[] projection = {MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.DISPLAY_NAME};
//        Uri uri = MediaStore.Files.getContentUri("external");
////        Cursor cursor = mContext.getContentResolver().query(uri, STAR, null, null, null);
//        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
//
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                do {
//                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                    if (new File(path).exists()) {
//                        newArray.add(path);
//                    }
//
//                    Log.i("path :", path);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
        return readArraylist();

    }

    public void saveArraylist(ArrayList<String> paths) {
        tinydb.clear();
        tinydb.putListString(SHARED_PREFS_FILE, paths);
    }

    public ArrayList<String> readArraylist() {
        ArrayList<String> oldArray;
        ArrayList<String> oldArrayFromDb = new ArrayList<>();
        List<Meme> oldArrayMemes = MemeLab.get(mContext).getMemes();
        for (int i = 0; i < oldArrayMemes.size(); i++) {
            oldArrayFromDb.add(oldArrayMemes.get(i).getLocation());
        }
        oldArray = tinydb.getListString(SHARED_PREFS_FILE);
        if (oldArrayFromDb.size() == 0) {
            saveArraylist(newArray);
            return newArray;
        } else if (oldArrayFromDb.size() != oldArray.size()) {
            saveArraylist(newArray);
            return compareArraylist(newArray, oldArrayFromDb);
        } else {

            ArrayList<String> newA;
            newA = (ArrayList<String>) newArray.clone();
            saveArraylist(newArray);
            return compareArraylist(newA, oldArray);
        }
    }

    public ArrayList<String> compareArraylist(ArrayList<String> nArray, ArrayList<String> oArray) {

        nArray.removeAll(oArray);
        return nArray;
    }

    public static ArrayList<ImageDataModel> gettAllImages(Context activity) {

        //Remove older images to avoid copying same image twice

        allImages.clear();
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null, imageName;

        //get all images from external storage

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

        while (cursor.moveToNext()) {

            absolutePathOfImage = cursor.getString(column_index_data);

            imageName = cursor.getString(column_index_folder_name);

            allImages.add(new ImageDataModel(imageName, absolutePathOfImage));

        }

        // Get all Internal storage images

        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

        while (cursor.moveToNext()) {

            absolutePathOfImage = cursor.getString(column_index_data);

            imageName = cursor.getString(column_index_folder_name);

            allImages.add(new ImageDataModel(imageName, absolutePathOfImage));
        }

        return allImages;
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }
}
