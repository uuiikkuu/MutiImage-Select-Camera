package image_viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ZoomImageViewAdapter extends FragmentPagerAdapter {

	private ArrayList<String> imageUrls = new ArrayList<String>();
	int mType;
	public ZoomImageViewAdapter(FragmentManager fm,int tpye) {
		super(fm);
		mType=tpye;
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment imageFragment = new ZoomImageFragment(arg0);
		if (getCount() > 0) {
			Bundle args = new Bundle();
			args.putString(ImageViewFragment.ARGS_IMAGE_URL,
				imageUrls.get(arg0));
			args.putInt(ZoomPictureActivity.TYPE_LOCAL,mType);
			imageFragment.setArguments(args);
		}
		return imageFragment;
	}
	@Override
	public int getCount() {
		return imageUrls.size();
	}

	public void addImageUrl(ArrayList<String> urls) {
		imageUrls.addAll(urls);
	}

	public List<String> getUrls() {
		return imageUrls;
	}


}
