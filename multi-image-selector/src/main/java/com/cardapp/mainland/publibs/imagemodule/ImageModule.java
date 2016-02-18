package com.cardapp.mainland.publibs.imagemodule;

import android.content.Context;
import android.content.Intent;

import com.cardapp.Module.BaseModule;
import com.cardapp.Module.OnModuleFragmentListener;
import com.cardapp.mainland.publibs.imagemodule.image_viewpager.ZoomPictureActivity;

import java.util.ArrayList;
import java.util.Locale;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/**
 * [Description]
 * <p/>
 * [How to use]
 * <p/>
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/11/19.
 * @since 1.0.0
 */

public class ImageModule implements BaseModule {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //多图回调接口
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private MultiPagerPicker mMultiPagerPicker;

    public void destroyMultiPagerPicker() {
        mMultiPagerPicker = null;
    }

    private int ImageCount=9;

    public interface MultiPagerPicker {

        void onImagePickSucc(ArrayList<String> resultList);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //多图选择器创建者
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class MultiImagesPickerBuilder {

        private Context mContext1;
        private boolean mShowCamera = true;
        private int mSelectCount = 9;
        private boolean isSingleChoose = false;
        private boolean isCut= false;

        public MultiImagesPickerBuilder(Context context1) {
            mContext1 = context1;
        }

        public MultiImagesPickerBuilder setShowCamera(boolean showCamera) {
            mShowCamera = showCamera;
            return this;
        }

        public MultiImagesPickerBuilder setSelectCount(int selectCount) {
            mSelectCount = selectCount;
            return this;
        }

        public MultiImagesPickerBuilder setSingleChooseMode() {
            this.isSingleChoose = true;
            return this;
        }

        public MultiImagesPickerBuilder setSingleChooseAndCutMode() {
            this.isSingleChoose = true;
            this.isCut = true;
            return this;
        }

        public void startPick() {
            Intent mIntent = new Intent(mContext1, MultiImageSelectorActivity.class);
// 是否显示调用相机拍照
            mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, mShowCamera);
// 最大图片选择数量
            mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, mSelectCount);
// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
            final int lModeMulti = !isSingleChoose ? MultiImageSelectorActivity.MODE_MULTI : MultiImageSelectorActivity.MODE_SINGLE;
            mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, lModeMulti);
            mIntent.putExtra(MultiImageSelectorActivity.EXTRA_CUT, isCut);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext1.startActivity(mIntent);
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //单例
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static ImageModule sECommerce;


    private Context mContext;
    public static ImageModule getInstance() {
        if (sECommerce == null) {
            synchronized (ImageModule.class) {
                if (sECommerce == null) {
                    sECommerce = new ImageModule();
                }
            }
        }
        return sECommerce;
    }

    public ImageModule init(Context context) {
        this.mContext = context.getApplicationContext();
        return this;
    }

    public void previewBigPictures_network(int position, ArrayList<String> imageList, Context context){
        Intent picIntent = new Intent(context, ZoomPictureActivity.class);
        picIntent.putExtra(ZoomPictureActivity.INTENT_PICTURES,
                imageList);
        picIntent.putExtra(ZoomPictureActivity.INTENT_CURRENT_PIC, imageList.get(position));
        picIntent.putExtra(ZoomPictureActivity.TYPE_NETWORK,2);
        context.startActivity(picIntent);
    }

    private ImageBackListener mImageBack;

    public void setImageBack(ImageBackListener imageBack) {
        mImageBack = imageBack;
    }

    public ImageBackListener getImageBack() {
        return mImageBack;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void displayAsActivity() {

    }

    public MultiPagerPicker getMultiPagerPicker() {

        return mMultiPagerPicker;
    }

    public MultiImagesPickerBuilder createMultiImagesPickerBuilder(Context context, MultiPagerPicker multiPagerPicker) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        mMultiPagerPicker = multiPagerPicker;
        return new MultiImagesPickerBuilder(mContext);
    }

    public void pickMultiImages(Context context, MultiPagerPicker multiPagerPicker,int maxImageCount) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
        if (maxImageCount!=0)
            ImageCount=maxImageCount;
        mMultiPagerPicker = multiPagerPicker;
        Intent mIntent = new Intent(mContext, MultiImageSelectorActivity.class);

// 是否显示调用相机拍照
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);

// 最大图片选择数量
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, ImageCount);

// 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        mIntent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }

    @Override
    public void displayInFragment(int i, OnModuleFragmentListener onModuleFragmentListener) {

    }

    @Override
    public void displayInFragment(int i) {

    }

    @Override
    public void changeLanguageMode(Locale locale) {

    }

    @Override
    public void popBack() {

    }

    @Override
    public void reAttach() {

    }

}
