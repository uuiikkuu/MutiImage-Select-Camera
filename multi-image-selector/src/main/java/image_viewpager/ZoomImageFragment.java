package image_viewpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cardapp.utils.resource.Toast;
import com.cardapp.utils.view.TouchImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.nereo.multi_image_selector.R;


public class ZoomImageFragment extends Fragment {

	public static final String ARGS_IMAGE_URL = "fragment.image_url";
	private int position;
	TouchImageView image;
	private Bitmap mBitmap;
	public ZoomImageFragment() {
	}

	public ZoomImageFragment(int position) {
		this.position = position;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.image_module_item_zoom_picturer, container, false);

		image = (TouchImageView) rootView.findViewById(R.id.display_picture);
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});

		image.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showSaveDialog();
				return false;
			}
		});

		Bundle args = getArguments();
		if (args != null) {
			String imageUrl = args.getString(ARGS_IMAGE_URL);
			int mTpye=args.getInt(ZoomPictureActivity.TYPE_LOCAL);
			if (mTpye==1){
				Helper_Image.displayImage_local_string(imageUrl,image);
			}else {
				Helper_Image.displayImage_network(image, imageUrl);
			}
			mBitmap=ImageLoader.getInstance().loadImageSync(imageUrl);
		}
		return rootView;
	}

	private void showSaveDialog() {
		String chose_from_galley = "保存至相册";
		CharSequence[] actionTexts = new CharSequence[]{chose_from_galley};
		BottomSelectorPopDialog mBottomSelectorPopDialog = new BottomSelectorPopDialog(getActivity(), actionTexts);
		mBottomSelectorPopDialog.setListener(new BottomSelectorPopDialog.Listener() {
			@Override
			public void onBtnItemClick(int position) {
				switch (position) {
					case 0:
						saveImageToGallery(getActivity(),mBitmap);
						Toast.Short(getActivity(),"已保存至相册");
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
		File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
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
}
