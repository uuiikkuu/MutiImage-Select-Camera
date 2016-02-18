package com.cardapp.mainland.publibs.imagemodule.image_viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import me.nereo.multi_image_selector.R;

public final class ImageViewFragment extends Fragment{
	public static final String ARGS_IMAGE_URL = "fragment.image_url";
	public static final String ARGS_ALL_IMAGE_URL = "fragment.image_urls";

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.item_viewpager_image, container, false);

		ImageView image = (ImageView) rootView.findViewById(R.id.image);


		final Bundle args = getArguments();
		if (args != null) {
			final String imageUrl = args.getString(ARGS_IMAGE_URL);
			Helper_Image.displayImage_network(image,imageUrl);
			image.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent picIntent = new Intent(getActivity(), ZoomPictureActivity.class);
					picIntent.putExtra(ZoomPictureActivity.INTENT_PICTURES,
							args.getStringArrayList(ARGS_ALL_IMAGE_URL));
					picIntent.putExtra(ZoomPictureActivity.INTENT_CURRENT_PIC, imageUrl);
					startActivity(picIntent);
				}
			});
		}

		return rootView;
	}
}
