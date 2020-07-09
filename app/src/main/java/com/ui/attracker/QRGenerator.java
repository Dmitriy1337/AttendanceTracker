package com.ui.attracker;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.ui.attracker.NewEventActivity.QR_SIZE;

public class QRGenerator
{
    public static Bitmap generateQR(String message) {
        QRGEncoder qrgEncoder = new QRGEncoder(message, null, QRGContents.Type.TEXT, QR_SIZE);
        Bitmap bitmap;
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            bitmap = Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.RGB_565);
            Log.v("QRGEncoder", e.toString());
        }
        return bitmap;
    }
}
