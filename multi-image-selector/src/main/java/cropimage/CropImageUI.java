package cropimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.cardapp.mainland.publibs.imagemodule.ImageModule;
import com.cardapp.utils.resource.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.open.cropq.CropImageView4;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.nereo.multi_image_selector.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CropImageUI extends Activity {

    private String mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBitmap = getIntent().getStringExtra("bitmap");
        cropImage4();
    }

    private void cropImage4() {
        setContentView(R.layout.fragment_cropimage4);
        final CropImageView4 mCropImage = (CropImageView4) findViewById(R.id.cropImg);
        //// TODO: 2015/12/10 需要解决图片过大的问题  fixed
        mCropImage.setDrawable(new BitmapDrawable(null, ImageLoader.getInstance().loadImageSync("file://" + mBitmap, new ImageSize(400, 400))), 250, 250);

        findViewById(R.id.save).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //  2015/12/31 by woode 修复《公共bug》No.: 剪切图片奔溃 ，主要是
                // Ⅰ 回调事件不在UI线程操作导致奔溃，
                // Ⅱ选择框越界导致 x + width must be <= bitmap.width()的IllegalArgumentException
                Observable.just(v)
                        //在io线程操作
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<View, ArrayList<String>>() {
                            @Override
                            public ArrayList<String> call(View view) {
                                FileUtil.init(getApplicationContext().getFilesDir().getAbsolutePath());
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                                String date = sdf.format(new java.util.Date());
                                FileUtil.writeImage(mCropImage.getCropImage(), FileUtil.SDCARD_PAHT + "/" + date + "/crop.png", 100);
                                ArrayList<String> resultList = new ArrayList<String>();
                                resultList.add(FileUtil.SDCARD_PAHT + "/" + date + "/crop.png");
                                return resultList;
                            }
                        })
                        //会到主线程处理结果及错误
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<ArrayList<String>>() {
                                    @Override
                                    public void call(ArrayList<String> resultList) {
                                        finish();
                                        ImageModule.getInstance().getMultiPagerPicker().onImagePickSucc(resultList);
                                        ImageModule.getInstance().destroyMultiPagerPicker();
                                    }
                                },
                                new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        throwable.printStackTrace();
                                        if (throwable instanceof IllegalArgumentException) {
                                            Toast.Short(CropImageUI.this, "剪切范围超出当前图片范围，请缩小选择框");
                                            return;
                                        }
                                        Toast.Long(CropImageUI.this, "剪切图片失败，请稍候重试");
                                    }
                                });
            }
        });
    }

    public int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }
}
