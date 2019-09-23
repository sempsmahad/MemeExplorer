package com.example.memeexplorer.utilities;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.memeexplorer.helpers.TinyDB;
import com.example.memeexplorer.memeClasses.Meme;
import com.example.memeexplorer.memeClasses.MemeLab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.memeexplorer.utilities.Constants.SHARED_PREFS_FILE;

public class ArrayListSaverInterface {
    private ArrayList<String> newArray;
    //   private ArrayList<String> oldArray;
    private Context mContext;
    private int c = 0;
    private TinyDB tinydb;

    public ArrayListSaverInterface(Context context) {
        mContext = context;
        newArray = new ArrayList<>();
        tinydb = new TinyDB(mContext);

//        oldArray = new ArrayList<>();
    }

    public ArrayList<String> getPathsArray() {
        String[] STAR = {"*"};
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , STAR, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (new File(path).exists()) {
                        newArray.add(path);
                    }

                    Log.i("path :", path);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
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

}
