package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageViewPageAdapter extends FragmentPagerAdapter {
	private ArrayList<String> imageUrls = new ArrayList<String>();

	public ImageViewPageAdapter(FragmentManager fm) {
		super(fm);
	}

	public ImageViewPageAdapter(FragmentManager fm, ArrayList<String> url) {
		super(fm);
		this.imageUrls = url;
	}


	@Override
	public Fragment getItem(int arg0) {
		Fragment imageFragment = new ImageViewFragment();
		if (getCount() > 0) {
			Bundle args = new Bundle();
			args.putString(ImageViewFragment.ARGS_IMAGE_URL, imageUrls.get(arg0));
			args.putStringArrayList(ImageViewFragment.ARGS_ALL_IMAGE_URL, imageUrls);
			imageFragment.setArguments(args);
		}

		return imageFragment;
	}

	@Override
	public int getCount() {
		return imageUrls.size();
	}

	public void addImageUrl(String url) {
		String[] urls = null;
		if (url != null && !url.equals("null")) {
			urls = url.split(",");
			imageUrls.addAll(Arrays.asList(urls));
		}
		notifyDataSetChanged();
	}
}
