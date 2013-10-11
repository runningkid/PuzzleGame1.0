package com.chunya.puzzlegame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowCongrats extends Activity implements OnClickListener {
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.congratulations);

		Bundle extras = this.getIntent().getExtras();
		int mode = extras.getInt("mode");
		String imgpath = extras.getString("imgPath");
		int steps = extras.getInt("steps");

		ImageView imgv = (ImageView) findViewById(R.id.congratsheart);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		if (imgpath.startsWith("img_")) {
			int resId = new Integer(imgpath.substring(4));
			BitmapFactory.decodeResource(this.getResources(), resId, options);
		} else {
			BitmapFactory.decodeFile(imgpath, options);
		}
		int imgHeight = options.outHeight;
		int imgWidth = options.outWidth;
		int reqWidth = this.getResources().getDisplayMetrics().widthPixels - 100;
		int reqHeight = (int) (this.getResources().getDisplayMetrics().heightPixels * 0.66);
		if (imgHeight > reqHeight || imgWidth > reqWidth) {
			int heightRatio = (int) ((float) imgHeight / (float) reqHeight) + 1;
			int widthRatio = (int) ((float) imgWidth / (float) reqWidth) + 1;
			options.inSampleSize = heightRatio > widthRatio ? heightRatio
					: widthRatio;
		}
		options.inJustDecodeBounds = false;
		Bitmap img;
		if (imgpath.startsWith("img_")) {
			int resId = new Integer(imgpath.substring(4));
			img = BitmapFactory.decodeResource(this.getResources(), resId,
					options);
		} else {
			img = BitmapFactory.decodeFile(imgpath, options);
		}

		imgv.setImageBitmap(img);

		TextView txtv = (TextView) findViewById(R.id.congratstext);

		SharedPreferences record = getSharedPreferences(PREFS_NAME, 0);
		int best = 0;
		switch (mode) {
		case 3:
			best = record.getInt("3by3best", 1000000);
			break;
		case 4:
			best = record.getInt("4by4best", 1000000);
			break;
		case 5:
			best = record.getInt("5by5best", 1000000);
			break;
		}
		if (best > 0 && steps < best) {
			SharedPreferences.Editor editor = record.edit();
			switch (mode) {
			case 3:
				editor.putInt("3by3best", steps);
				break;
			case 4:
				editor.putInt("4by4best", steps);
				break;
			case 5:
				editor.putInt("5by5best", steps);
			}
			// Commit the edits!
			editor.commit();
			best = steps;
		}

		String str = "Contratulations! You took " + steps + " steps!";
		if (best > 0) {
			str += "\n Your best record is " + best + ".";
		}
		txtv.setText(str);

		View wholeview = findViewById(R.id.congrats);
		wholeview.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
	}

}
