package com.chunya.puzzlegame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class ShowImage extends Activity implements OnClickListener {
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_image);
		// find our ImageView in the layout
		ImageView img = (ImageView) findViewById(R.id.single_photo);

		// retrieve the set of data passed to us by the Intent
		Bundle extras = getIntent().getExtras();

		// and retrieve the imageToDisplay ID from the extras bundle
		path = extras.getString("imageToDisplay");
		if (path.startsWith("img_")) {
			int resId = new Integer(path.substring(4));
			img.setImageResource(resId);
		}
		// set the ImageView to display the specified resource ID
		else {
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bm;
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			int imgHeight = options.outHeight;
			int imgWidth = options.outWidth;
			int reqWidth = this.getResources().getDisplayMetrics().widthPixels - 100;
			int reqHeight = this.getResources().getDisplayMetrics().heightPixels - 100;
			if (imgHeight > reqHeight || imgWidth > reqWidth) {
				int heightRatio = (int) ((float) imgHeight / (float) reqHeight) + 1;
				int widthRatio = (int) ((float) imgWidth / (float) reqWidth) + 1;
				options.inSampleSize = heightRatio > widthRatio ? heightRatio
						: widthRatio;
			}
			options.inJustDecodeBounds = false;
			bm = BitmapFactory.decodeFile(path, options);
			img.setImageBitmap(bm);
		}

		// close the Activity when a user taps/clicks on the image.
		// img.setOnClickListener(this);

		Button easy_button = (Button) findViewById(R.id.split3by3);
		Button normal_button = (Button) findViewById(R.id.split4by4);
		Button hard_button = (Button) findViewById(R.id.split5by5);
		easy_button.setOnClickListener(this);
		normal_button.setOnClickListener(this);
		hard_button.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent i = new Intent(this, Play.class);
		i.putExtra("imgPath", path);
		switch (v.getId()) {
		case R.id.split3by3:
			i.putExtra("playmode", 3);
			break;
		case R.id.split4by4:
			i.putExtra("playmode", 4);
			break;
		case R.id.split5by5:
			i.putExtra("playmode", 5);
			break;
		default:
		}
		startActivity(i);
	}

}
