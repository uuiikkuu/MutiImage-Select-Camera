package com.cardapp.mainland.publibs.imagemodule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.bither.util.NativeUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Woode.Wang on 2015/11/9.
 * @since 1.0.0
 */
public abstract class MultiImageUploader {
    private Context mContext;
//    private MultiImageCv mMultiImageCv;
    private ArrayList<BitmapReq> mBitmapReqs;
    private Listener mListener;
    private int mMaxImageCount = 8;
    private int mSpanCount = 4;

    /**
     * 测试一张图得出的上传速度：353109/16000，再加半秒
     *
     * @param byteArray 目标图片的二进制数组
     * @return
     */
    public static int getImgTimeout(byte[] byteArray) {
        return byteArray.length / 22 + 500;
    }

    protected Context getContext() {
        return mContext;
    }

    public MultiImageUploader(Context context) {
        mContext = context;
    }

    /**
     * 此方法废弃，使用{@link #MultiImageUploader(Context)} 代替
     *
     * @param context
     * @param fragment
     */
    @Deprecated
    public MultiImageUploader(Context context, Fragment fragment) {
        mContext = context;
//        mMultiImageCv = multiImageCv;
    }

    /**
     * 设置最多可以显示的图片数量，默认为8
     *
     * @param maxImageCount
     * @return
     */
    public MultiImageUploader setMaxImageCount(int maxImageCount) {
        mMaxImageCount = maxImageCount;
        return this;
    }


    /**
     * 设置每行显示的图片数量，默认为4
     *
     * @param spanCount
     */
    public MultiImageUploader setSpanCount(int spanCount) {
        mSpanCount = spanCount;
        return this;
    }

    /**
     * 设置图片获取后是否剪切
     *
     * @param isImageCrop
     * @return
     */
    public MultiImageUploader setImageCrop(boolean isImageCrop) {
        return this;
    }

    public MultiImageUploader setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    /**
     * * 此方法废弃，使用{@link #startUpload(ArrayList)} 代替
     */
    @Deprecated
    public void startUpload() {
//        if (mMultiImageCv != null) {
//            startUpload(mMultiImageCv.getLocalBitmapPathList());
//        }
    }

    public void startUpload(ArrayList<String> bitmaps) {
        mBitmapReqs = new ArrayList<>();
        for (int i = 0; i < bitmaps.size(); i++) {
            BitmapReq lBitmapReq = new BitmapReq(i, bitmaps.get(i));
            mBitmapReqs.add(lBitmapReq);
        }
        LoopReq();
    }

    private void LoopReq() {
        BitmapReq lBitmapReq = getReq2Upload(mBitmapReqs);
        if (lBitmapReq != null) {
            //都未完成
            if (!lBitmapReq.mIsUploading) {
                lBitmapReq.mIsUploading = true;
                lBitmapReq.increaseTtyTime();
                if (lBitmapReq.isTtyTimeOverMax()) {
                    onTtyTimeOverMax(lBitmapReq);
                } else {
                    req2UploadImg(lBitmapReq);
                }
            }
        } else {
            //都已完成
            ArrayList<String> lFinishedImageUrlList = new ArrayList<>();
            ArrayList<String> lUnfinishedImageUrlList = new ArrayList<>();
            calImageUrlList(lFinishedImageUrlList, lUnfinishedImageUrlList);
            if (mListener != null) {
                mListener.onUploadAllDone(lFinishedImageUrlList);
            }
        }
    }

    private void calImageUrlList(ArrayList<String> finishedImageUrlList, ArrayList<String> unfinishedImageUrlList) {
        for (BitmapReq lBitmapReq : mBitmapReqs) {
            String lImgUrl = lBitmapReq.mImgUrl;
            if (lImgUrl == null) {
                unfinishedImageUrlList.add(lImgUrl);
            } else {
                finishedImageUrlList.add(lImgUrl);
            }
        }
    }

    public static abstract class Listener {
        public abstract void onUploadAllDone(ArrayList<String> imageUrlList);

        public abstract void onUploadFail(int failcode);

        public void onUploadSingleSucc(int index, String imgUrl) {

        }
    }

    void onTtyTimeOverMax(BitmapReq bitmapReq) {

    }

    protected abstract void req2UploadImg(BitmapReq bitmapReq);

    protected void afterReqFail(BitmapReq bitmapReq) {
        bitmapReq.mIsUploading = false;
        bitmapReq.mIsUploaded = false;
        LoopReq();
    }


    protected void afterReqSucc(BitmapReq bitmapReq, String imgUrl) {
        bitmapReq.mImgUrl = imgUrl;
        bitmapReq.mIsUploading = false;
        bitmapReq.mIsUploaded = true;
        if (mListener != null) {
            mListener.onUploadSingleSucc(bitmapReq.getIndex(), imgUrl);
        }
        LoopReq();
    }

    private BitmapReq getReq2Upload(ArrayList<BitmapReq> bitmapReqs) {
        for (BitmapReq lBitmapReq : bitmapReqs) {
            if (!lBitmapReq.mIsUploaded) {
                return lBitmapReq;
            }
        }
        return null;
    }

    public void init() {
//        mMultiImageCv.initView(mSpanCount, mMaxImageCount);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected class BitmapReq {
        public static final int MAX_TRYTIME = 3;
        private final int mIndex;
        private final String mBitmapPath;
        private boolean mIsUploading;
        private boolean mIsUploaded;
        private int mTrytime;
        public String mImgUrl;

        public BitmapReq(int i, String bitmapPath) {
            mIndex = i;
            mBitmapPath = bitmapPath;
        }

        public Bitmap getBitmap() {
            Bitmap lBitmap = ImageLoader.getInstance().loadImageSync("file://"+mBitmapPath);
            //64位的arm不压缩
            if (Helper_CPU.isCPUInfo64()) {
                return lBitmap;
            }
            //图片压缩
            return  pictureCompress(lBitmap);
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
    }

    /**
     *图片压缩
     */
    private Bitmap  pictureCompress(Bitmap bitmap) {
//        Log.i("muti_image",getBitmapsize(bitmap) +"-----before");
        //original
        int quality=100;//同学们可以与原生的压缩方法对比一下，同样设置成50效果如何
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
            Bitmap lBitmap=getDiskPic(jpegTrueFile.getAbsolutePath());

//            Log.i("muti_image", getBitmapsize(lBitmap) + "-----after");
            return lBitmap;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getBitmapsize(Bitmap bitmap){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

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
