package com.cardapp.mainland.publibs.imagemodule.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cardapp.mainland.publibs.imagemodule.Helper_CPU;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.bither.util.NativeUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Michael.Mu on 1/28/2016.
 * @version 1.0.0
 */
public class BitmapRequest {
    public static final int MAX_TRYTIME = 3;
    private final int mIndex;
    private final String mBitmapPath;
    private boolean mIsUploading;
    private boolean mIsUploaded;
    private int mTrytime;
    public String mImgUrl;

    private Context mContext;

    public BitmapRequest(Context context, int i, String bitmapPath) {
        mContext = context;
        mIndex = i;
        mBitmapPath = bitmapPath;
    }

    public Bitmap getBitmap() {
        Bitmap lBitmap = ImageLoader.getInstance().loadImageSync("file://" + mBitmapPath);
        //64位的arm不压缩
        if (Helper_CPU.isCPUInfo64()) {
            return lBitmap;
        }
        //图片压缩
        return pictureCompress(lBitmap);
    }

    public boolean isTtyTimeOverMax() {
        return mTrytime >= MAX_TRYTIME;
    }

    public void increaseTtyTime() {
        mTrytime++;
    }

    public int getIndex() {
        return mIndex;
    }

    public byte[] getByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //此处只能用png解析，不然会报空指针异常
        try {
            final Bitmap lBitmap = getBitmap();
            lBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            lBitmap.recycle();
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 图片压缩
     */
    private Bitmap pictureCompress(Bitmap bitmap) {
//        Log.i("muti_image",getBitmapsize(bitmap) +"-----before");
        //original
        int quality = 100;//同学们可以与原生的压缩方法对比一下，同样设置成50效果如何
        File dirFile = mContext.getExternalCacheDir();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
//        Bitmap bitmap = UtilsClass.getDiskPic(imageUrl);
        File originalFile = new File(dirFile, "original.jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(
                    originalFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            File jpegTrueFile = new File(dirFile, "jpegtrue.jpg");
            File jpegFalseFile = new File(dirFile, "jpegfalse.jpg");
            NativeUtil.compressBitmap(bitmap, quality,
                    jpegTrueFile.getAbsolutePath(), true);
            NativeUtil.compressBitmap(bitmap, quality,
                    jpegFalseFile.getAbsolutePath(), false);
//            imageUrl=jpegTrueFile.getAbsolutePath();
            Bitmap lBitmap = getDiskPic(jpegTrueFile.getAbsolutePath());

//            Log.i("muti_image", getBitmapsize(lBitmap) + "-----after");
            return lBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地图片
     *
     * @param path
     * @return
     */
    public Bitmap getDiskPic(String path) {
        Bitmap b = null;
        File f = new File(path);
        if (f.exists()) {
            b = BitmapFactory.decodeFile(path);
        }
        return b;
    }
}
