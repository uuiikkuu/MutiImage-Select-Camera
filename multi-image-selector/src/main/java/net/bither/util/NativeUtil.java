/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;


public class NativeUtil {
    private static int DEFAULT_QUALITY = 50;

    public static void compressBitmap(Bitmap bit, String fileName,
                                      boolean optimize) {
        compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize);

    }

    public static void compressBitmap(Bitmap bit, int quality, String fileName,
                                      boolean optimize) {

        Log.d("native", "compress of native");
        // if (bit.getConfig() != Config.ARGB_8888) {
        Bitmap result = null;
        if (bit.getWidth() > 3000) {
            quality = 200;
            result = Bitmap.createBitmap(bit.getWidth() / 4, bit.getHeight() / 4,
                    Config.ARGB_8888);//
//		result= UtilsClass.comp(result);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
            rect = new Rect(0, 0, bit.getWidth() / 4, bit.getHeight() / 4);//
            canvas.drawBitmap(bit, null, rect, null);

        } else if (3000 > bit.getWidth() && bit.getWidth() > 1400) {
            quality = 100;
            result = Bitmap.createBitmap(bit.getWidth() / 3, bit.getHeight() / 3,
                    Config.ARGB_8888);// 缩小3倍
//		result= UtilsClass.comp(result);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
            rect = new Rect(0, 0, bit.getWidth() / 3, bit.getHeight() / 3);// 缩小3倍
            canvas.drawBitmap(bit, null, rect, null);
        } else if (1400 > bit.getWidth() && bit.getWidth() > 500) {
            result = Bitmap.createBitmap(bit.getWidth() / 2, bit.getHeight() / 2,
                    Config.ARGB_8888);
//		result= UtilsClass.comp(result);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
            rect = new Rect(0, 0, bit.getWidth() / 2, bit.getHeight() / 2);//
            canvas.drawBitmap(bit, null, rect, null);
        } else {
            result = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(),
                    Config.ARGB_8888);
//		result= UtilsClass.comp(result);
            Canvas canvas = new Canvas(result);
            Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
            rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());//
            canvas.drawBitmap(bit, null, rect, null);
        }
        saveBitmap(result, quality, fileName, optimize);
        result.recycle();

    }

    private static void saveBitmap(Bitmap bit, int quality, String fileName,
                                   boolean optimize) {

        compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality,
                fileName.getBytes(), optimize);

    }

    private static native String compressBitmap(Bitmap bit, int w, int h,
                                                int quality, byte[] fileNameBytes, boolean optimize);

    static {
        System.loadLibrary("jpegbither");
        System.loadLibrary("bitherjni");
    }


}
