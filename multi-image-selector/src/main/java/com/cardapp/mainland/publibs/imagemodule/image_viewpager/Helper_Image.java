package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.IOException;
import java.util.ArrayList;

import me.nereo.multi_image_selector.R;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/11/26.
 * @since 1.0.0
 */
public class Helper_Image {
    static int FADEINTIME = 200;
    /**
     * 使用ImageLoader加载图片
     *
     * @param image
     * @param imageUrl
     */
    public static void displayImage_network(ImageView image, String imageUrl) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.loading_bg_cn_2_1)
                .showImageOnFail(R.drawable.loading_fail_bg_cn_2_1)
                .showImageForEmptyUri(R.drawable.loading_none_bg_cn_2_1)
                .cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(FADEINTIME))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, image, options);
    }

    public static void previewBigPictures_local(int position, ArrayList<String> imageList, Context context){
        Intent picIntent = new Intent(context, ZoomPictureActivity.class);
        picIntent.putExtra(ZoomPictureActivity.INTENT_PICTURES,
                imageList);
        picIntent.putExtra(ZoomPictureActivity.INTENT_CURRENT_PIC, imageList.get(position));
        picIntent.putExtra(ZoomPictureActivity.TYPE_LOCAL,1);
        context.startActivity(picIntent);
    }

    public static void displayImage_local_string(String imgStr, ImageView image){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.loading_bg_cn_2_1)
                .showImageOnFail(R.drawable.loading_fail_bg_cn_2_1)
                .showImageForEmptyUri(R.drawable.loading_none_bg_cn_2_1)
                .cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).resetViewBeforeLoading(true)
                .displayer(new FadeInBitmapDisplayer(FADEINTIME)).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        String imagePath = "/mnt/sdcard/image.png";
        String imageUrl = ImageDownloader.Scheme.FILE.wrap(imagePath);
        ImageLoader.getInstance().displayImage(imgStr, image, options);
    }

    public static Bitmap getBitmap(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 512 * 512);

        opts.inJustDecodeBounds = false;
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), m, true);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
