package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cardapp.mainland.publibs.imagemodule.view.TouchImageView;
import com.cardapp.utils.resource.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.nereo.multi_image_selector.R;

/**
 * [Description]
 * <p>
 * [How to use]
 * <p>
 * [Tips]
 *
 * @author Created by Jim.Huang on 2015/12/21.
 * @since 1.0.0
 */

public class ZoomImageFragmentV2 extends Fragment {
    public static final String ARGS_IMAGE_URL = "fragment.image_url";
    private int position;
    TouchImageView image;
    private Bitmap mBitmap;

    public ZoomImageFragmentV2() {
    }

    @SuppressLint("ValidFragment")
    public ZoomImageFragmentV2(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.imagemodule_item_zoom_picture, container, false);

        image = (TouchImageView) rootView.findViewById(R.id.display_picture);
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        final Bundle args = getArguments();
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (args != null) {
                    showSaveDialog(args);
                }
                return false;
            }
        });

        if (args != null) {
            String imageUrl = args.getString(ARGS_IMAGE_URL);
            int mTpye = args.getInt(ZoomPictureActivity.TYPE_LOCAL);
            if (mTpye == 1) {
                String s = imageUrl.substring(0, 4);
                if (s.equals("http")) {
                    //多图添加时网络图片预览
                    Helper_Image.displayImage_network(image, imageUrl);

                } else {
                    //多图添加时本地图片预览
//                    Helper_Image.displayImage_local_string(imageUrl, image);
                    Bitmap lBitmap = createImageThumbnail(imageUrl);
                    lBitmap = getOriBitmap(imageUrl, lBitmap);
                    image.setImageBitmap(lBitmap);
                }
            } else {
                //浏览界面时网络图片预览
                Helper_Image.displayImage_network(image, imageUrl);

            }
        }
        return rootView;
    }

    private Bitmap getOriBitmap(String imageUrl, Bitmap bitmap) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        try {

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

    ImageSize lImageSize = new ImageSize(512, 512);

    private void showSaveDialog(Bundle bundle) {
        String imageUrl = bundle.getString(ARGS_IMAGE_URL);
        int mTpye = bundle.getInt(ZoomPictureActivity.TYPE_LOCAL);
        if (mTpye == 1) {
            String s = imageUrl.substring(0, 4);
            if (s.equals("http")) {
                mBitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
            } else {
                String lUri = "file://" + imageUrl;
                mBitmap = ImageLoader.getInstance().loadImageSync(lUri, lImageSize);
            }
        } else {
            mBitmap = ImageLoader.getInstance().loadImageSync(imageUrl);
        }

        String saveStr = "保存至相册";
        CharSequence[] actionTexts = new CharSequence[]{saveStr};
        BottomSelectorPopDialog mBottomSelectorPopDialog = new BottomSelectorPopDialog(getActivity(), actionTexts);
        mBottomSelectorPopDialog.setListener(new BottomSelectorPopDialog.Listener() {
            @Override
            public void onBtnItemClick(int position) {
                switch (position) {
                    case 0:
                        if (mBitmap != null) {
                            saveImageToGallery(getActivity(), mBitmap);
                            Toast.Short(getActivity(), "保存成功");
                        }
                    default:
                        break;
                }
            }

            @Override
            public void onCancelBtnClick() {
            }
        });
        mBottomSelectorPopDialog.show();
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "WanXia");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
    }


    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 512 * 512);
        opts.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
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
