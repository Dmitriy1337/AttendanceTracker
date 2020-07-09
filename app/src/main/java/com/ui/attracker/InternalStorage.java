package com.ui.attracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class InternalStorage
{
    public static void saveBitmapToInternalStorage(String name, Bitmap bitmapImage, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        File myPath = new File(directory, name + ".png");

        try (FileOutputStream outputStream = new FileOutputStream(myPath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            Log.v("Internal Storage", e.toString());
        }
    }

    public static Bitmap loadBitmapFromStorage(String name, Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir("images", Context.MODE_PRIVATE);
        File file = new File(directory, name + ".png");

        try (FileInputStream inpStream = new FileInputStream(file)) {
            return BitmapFactory.decodeStream(inpStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
