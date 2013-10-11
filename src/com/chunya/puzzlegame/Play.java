package com.chunya.puzzlegame;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Play extends Activity implements OnClickListener {
	private String imgpath;
	private int mode;
	private Bitmap cropped[];
	private ImageView puzzle[];
	private int order[];
	private int s = 0;
	private int blank;
	private int steps = 0;
	private Context mycontext;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.split_image);

		mycontext = this;
		Bundle extras = this.getIntent().getExtras();
		mode = extras.getInt("playmode");
		imgpath = extras.getString("imgPath");

		switch (mode) {
		case 3:
			s = 3;
			break;
		case 4:
			s = 4;
			break;
		case 5:
			s = 5;
			break;
		}
		cropped = new Bitmap[s * s];

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
		int reqHeight = this.getResources().getDisplayMetrics().heightPixels - 100;
		if (imgHeight > reqHeight || imgWidth > reqWidth) {
			int heightRatio = (int) ((float) imgHeight / (float) reqHeight) + 1;
			int widthRatio = (int) ((float) imgWidth / (float) reqWidth) + 1;
			options.inSampleSize = heightRatio > widthRatio ? heightRatio
					: widthRatio;
		}
		options.inJustDecodeBounds = false;
		Bitmap scaled;
		if (imgpath.startsWith("img_")) {
			int resId = new Integer(imgpath.substring(4));
			scaled = BitmapFactory.decodeResource(this.getResources(), resId,
					options);
		} else {
			scaled = BitmapFactory.decodeFile(imgpath, options);
		}

		int scaledWidth = scaled.getWidth();
		int scaledHeight = scaled.getHeight();

		float ratio = (float) scaledWidth / (float) scaledHeight;
		if (scaledWidth > reqWidth || scaledHeight > reqHeight) {
			int dstWidth, dstHeight;
			if (scaledWidth > reqWidth) {
				dstWidth = reqWidth;
				if ((float) dstWidth / ratio > (float) reqHeight) {
					dstHeight = reqHeight;
					dstWidth = (int) ((float) dstHeight * ratio);
				} else {
					dstHeight = (int) ((float) dstWidth / ratio);
				}
			} else {
				if (scaledHeight > reqHeight) {
					dstHeight = reqHeight;
					dstWidth = (int) ((float) dstHeight * ratio);
				} else {
					dstHeight = scaledHeight;
					dstWidth = scaledWidth;
				}
			}
			scaled = Bitmap.createScaledBitmap(scaled, dstWidth, dstHeight,
					true);
			scaledWidth = scaled.getWidth();
			scaledHeight = scaled.getHeight();
		}

		int croppedWidth = scaledWidth / s;
		int croppedHeight = scaledHeight / s;
		int color[] = new int[croppedWidth * croppedHeight];
		for (int i = 0; i < croppedWidth * croppedHeight; i++)
			color[i] = android.graphics.Color.DKGRAY;
		for (int i = 0; i < s; i++)
			for (int j = 0; j < s; j++) {
				if (i == s - 1 && j == s - 1)
					cropped[i * s + j] = Bitmap.createBitmap(color,
							croppedWidth, croppedHeight, Bitmap.Config.RGB_565);
				else
					cropped[i * s + j] = Bitmap.createBitmap(scaled, j
							* croppedWidth, i * croppedHeight, croppedWidth,
							croppedHeight);
			}

		scaled.recycle();

		boolean flag[] = new boolean[s * s];
		Random randomgen = new Random();
		order = new int[s * s];
		boolean keepon = true;
		while (keepon) {
			for (int i = 0; i < s * s; i++)
				flag[i] = false;
			for (int i = 0; i < s * s - 1; i++) {
				int r = randomgen.nextInt(s * s - 1);
				while (flag[r]) {
					r = randomgen.nextInt(s * s - 1);
				}
				order[i] = r;
				flag[r] = true;
			}
			order[s * s - 1] = s * s - 1;
			// check availability
			int revcnt = 0;
			for (int i = 0; i < s * s - 1; i++) {
				for (int j = i + 1; j < s * s - 1; j++) {
					if (order[i] > order[j])
						revcnt++;
				}
			}
			if (revcnt % 2 == 0)
				keepon = false;
		}

		puzzle = new ImageView[s * s];
		for (int i = 0; i < s * s; i++) {
			puzzle[i] = new ImageView(this);
			puzzle[i].setBackgroundResource(R.drawable.image_background);
			puzzle[i].setImageBitmap(cropped[order[i]]);
			puzzle[i].setId(i);
		}
		puzzle[s * s - 2].setOnClickListener(this);
		puzzle[s * s - 1 - s].setOnClickListener(this);
		blank = s * s - 1;

		LinearLayout row = (LinearLayout) findViewById(R.id.row);
		LinearLayout col[] = new LinearLayout[s];
		for (int i = 0; i < s; i++) {
			col[i] = new LinearLayout(this);
			col[i].setOrientation(LinearLayout.HORIZONTAL);
			col[i].setGravity(0x00000001);
			for (int j = 0; j < s; j++) {
				col[i].addView(puzzle[i * s + j]);
			}
			row.addView(col[i]);
		}
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean statusCheck() {
		for (int i = 0; i < s * s - 1; i++) {
			if (order[i] != i)
				return false;
		}
		return true;
	}

	public void showCongrats() {
		Intent i = new Intent(mycontext, ShowCongrats.class);
		i.putExtra("mode", mode);
		i.putExtra("imgPath", imgpath);
		i.putExtra("steps", steps);
		startActivity(i);
	}

	public void onClick(View v) {
		if (blank - s >= 0 && puzzle[blank - s].getId() == v.getId()) {
			puzzle[blank - s].setImageBitmap(cropped[order[blank]]);
			puzzle[blank].setImageBitmap(cropped[order[blank - s]]);
			int tmp = order[blank];
			order[blank] = order[blank - s];
			order[blank - s] = tmp;
			blank = blank - s;
			steps++;
			if (statusCheck()) {
				showCongrats();
				this.finish();
			}
		} else if (blank + s < s * s && puzzle[blank + s].getId() == v.getId()) {
			puzzle[blank + s].setImageBitmap(cropped[order[blank]]);
			puzzle[blank].setImageBitmap(cropped[order[blank + s]]);
			int tmp = order[blank];
			order[blank] = order[blank + s];
			order[blank + s] = tmp;
			blank = blank + s;
			steps++;
			if (statusCheck()) {
				showCongrats();
				this.finish();
			}
		} else if (blank - 1 >= 0 && (blank - 1) % s != s - 1
				&& puzzle[blank - 1].getId() == v.getId()) {
			puzzle[blank - 1].setImageBitmap(cropped[order[blank]]);
			puzzle[blank].setImageBitmap(cropped[order[blank - 1]]);
			int tmp = order[blank];
			order[blank] = order[blank - 1];
			order[blank - 1] = tmp;
			blank = blank - 1;
			steps++;
			if (statusCheck()) {
				showCongrats();
				this.finish();
			}
		} else if (blank + 1 < s * s && (blank + 1) % s != 0
				&& puzzle[blank + 1].getId() == v.getId()) {
			puzzle[blank + 1].setImageBitmap(cropped[order[blank]]);
			puzzle[blank].setImageBitmap(cropped[order[blank + 1]]);
			int tmp = order[blank];
			order[blank] = order[blank + 1];
			order[blank + 1] = tmp;
			blank = blank + 1;
			steps++;
			if (statusCheck()) {
				showCongrats();
				this.finish();
			}
		}
		if (blank - 1 >= 0 && (blank - 1) % s != s - 1)
			puzzle[blank - 1].setOnClickListener(this);
		if (blank + 1 < s * s && (blank + 1) % s != 0)
			puzzle[blank + 1].setOnClickListener(this);
		if (blank - s >= 0)
			puzzle[blank - s].setOnClickListener(this);
		if (blank + s < s * s)
			puzzle[blank + s].setOnClickListener(this);
	}

}
