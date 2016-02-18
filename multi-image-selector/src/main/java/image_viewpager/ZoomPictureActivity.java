package image_viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import me.nereo.multi_image_selector.R;

public class ZoomPictureActivity extends FragmentActivity {

	public static final String INTENT_PICTURES = "intent.viewpager_pictures";
	public static final String INTENT_CURRENT_PIC = "intent.current_picture";
	public static final String TYPE_LOCAL="local";
	public static final String TYPE_NETWORK="network";

	ViewPager mDisplayerPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_picture);
		Intent intent = getIntent();
		ArrayList<String> imageUrls = intent.getStringArrayListExtra(INTENT_PICTURES);
		String currentUrl = intent.getStringExtra(INTENT_CURRENT_PIC);
		int type=intent.getIntExtra(TYPE_LOCAL,0);
		int position = imageUrls.indexOf(currentUrl);
		mDisplayerPage = (ViewPager) findViewById(R.id.displayer_pager);
		ZoomImageViewAdapter zoomImageAdapter = new ZoomImageViewAdapter(getSupportFragmentManager(),type);
		zoomImageAdapter.addImageUrl(imageUrls);
		mDisplayerPage.setAdapter(zoomImageAdapter);
		mDisplayerPage.setCurrentItem(position);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

}
