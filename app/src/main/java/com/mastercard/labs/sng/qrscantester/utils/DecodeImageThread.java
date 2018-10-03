package com.mastercard.labs.sng.qrscantester.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.zxing.Result;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DecodeImageThread implements Runnable{
    public final static String TAG = "DecodeImageThread";
    private final static int NANO_TO_MILLI_SECONDS = 1000000;
    private final static int IMAGE_MAXSIZE = 512;

    public interface DecodeImageCallback {
        void decodeSucceed(Result result, String performanceResult);
        void decodeFail();
    }

    private Uri path;
    private DecodeImageCallback callback;
    private Context context;

    public DecodeImageThread(Uri imgPath, Context context, DecodeImageCallback callback) {
        this.path = imgPath;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public void run() {
        testDecodeImage(IMAGE_MAXSIZE);
    }

    private void testDecodeImage(int maxImageSize) {
        Bitmap image = null;
        long startTime = 0, now = 0, timeDelta = 0;

        StringBuilder performanceResult = new StringBuilder();
        try {
            startTime = System.nanoTime();
            InputStream is = context.getContentResolver().openInputStream(path);
            image = BitmapFactory.decodeStream(is);
            now = System.nanoTime();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Cannot load image", e);
        }
        if (image != null) {
            timeDelta = now - startTime;
            performanceResult.append("Image loading time: " + (timeDelta / NANO_TO_MILLI_SECONDS) + " milliseconds\n");
            startTime = System.nanoTime();
            int resizedWidth = image.getWidth();
            int resizedHeight = image.getHeight();

            Bitmap resizedImage = image;
            if (resizedHeight >= resizedWidth && resizedHeight > maxImageSize) {
                float ratio = (float)resizedHeight / maxImageSize;
                resizedHeight = maxImageSize;
                resizedWidth = Math.round(resizedWidth / ratio);
                resizedImage = Bitmap.createScaledBitmap(image, resizedWidth, resizedHeight, true);
            }else if (resizedWidth >= resizedHeight && resizedWidth > maxImageSize) {
                float ratio = (float)resizedWidth / maxImageSize;
                resizedWidth = maxImageSize;
                resizedHeight = Math.round(resizedHeight / ratio);
                resizedImage = Bitmap.createScaledBitmap(image, resizedWidth, resizedHeight, true);
            }

            byte[] data = QrUtils.getYUV420sp(resizedImage);

            now = System.nanoTime();
            performanceResult.append("Image converting time: " + ((now - startTime) / NANO_TO_MILLI_SECONDS) + " milliseconds\n");
            timeDelta += now - startTime;
            try {
                startTime = System.nanoTime();
                Result result = QrUtils.decodeImage(data, resizedWidth, resizedHeight);
                now = System.nanoTime();
                timeDelta += now - startTime;
                performanceResult.append("QRCode decode time: " + ((now - startTime) / NANO_TO_MILLI_SECONDS) + " milliseconds\n");
                performanceResult.append("Total time: " + (timeDelta / NANO_TO_MILLI_SECONDS) + " milliseconds");
                callback.decodeSucceed(result, performanceResult.toString());
                return;
            } catch (Exception e) {
                Log.e(TAG, "Cannot decode image", e);
            }finally {
                image.recycle();
                resizedImage.recycle();
            }
        }
        if (maxImageSize == IMAGE_MAXSIZE) {
            testDecodeImage(maxImageSize / 2);
        }else {
            callback.decodeFail();
        }
    }


}
