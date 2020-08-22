package com.ui.attracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Objects;

public class InternalStorage
{
    private static final String bitmapDirectory = "images";
    private static final String workbookDirectory = "tables";

    public static File getBitmapDirectory(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = new File(contextWrapper.getFilesDir(), bitmapDirectory);
        if (!directory.isDirectory())
            directory.mkdir();
        return directory;
    }

    public static File getWorkbookDirectory(Context context) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = new File(contextWrapper.getFilesDir(), workbookDirectory);
        if (!directory.isDirectory())
            directory.mkdir();
        return directory;
    }

    public static void saveBitmapToInternalStorage(String name, Bitmap bitmapImage, Context context) {
        File directory = getBitmapDirectory(context);
        File myPath = new File(directory, name + ".png");

        try (FileOutputStream outputStream = new FileOutputStream(myPath)) {
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (Exception e) {
            Log.v("Internal Storage", e.toString());
        }
    }

    public static Bitmap loadBitmapFromStorage(String name, Context context) {
        File directory = getBitmapDirectory(context);
        File file = new File(directory, name + ".png");

        try (FileInputStream inpStream = new FileInputStream(file)) {
            return BitmapFactory.decodeStream(inpStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteAllFiles(Context context) {
        File imageDirectory = getBitmapDirectory(context);
        for (final File fileEntry : Objects.requireNonNull(imageDirectory.listFiles()))
            fileEntry.delete();

        File workbookDirectory = getWorkbookDirectory(context);
        for (final File fileEntry : Objects.requireNonNull(workbookDirectory.listFiles()))
            fileEntry.delete();
    }

    public static void saveWorkbookToInternalStorage(String name, Workbook workbook, Context context) {
        File directory = getWorkbookDirectory(context);
        File myPath = new File(directory, name + ".xls");

        try (FileOutputStream outputStream = new FileOutputStream(myPath)) {
            workbook.write(outputStream);
        } catch (Exception e) {
            Log.v("Internal Storage", e.toString());
        }
    }
}
