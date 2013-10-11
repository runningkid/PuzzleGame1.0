package com.chunya.puzzlegame;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {
	private Context myContext;
	private ArrayList<Bitmap> cache;
	private ArrayList<String> tag;

	private DBAdapter db;
	private ArrayList<String> imgPath;
	private int count;

	public ImageAdapter(Context c, DBAdapter database) {
		myContext = c;

		db = database;
		imgPath = db.getPaths();
		// check image path's validation
		// if the image is no longer in the gallery,
		// delete the path from database and ArrayList
		for (int i = 0; i < imgPath.size();) {
			if (!imgPath.get(i).startsWith("img_")
					&& !(new File(imgPath.get(i))).exists()) {
				db.deletePath(imgPath.get(i));
				imgPath.remove(i);
			} else
				i++;
		}

		count = imgPath.size();
		cache = new ArrayList<Bitmap>();
		tag = new ArrayList<String>();
	}

	public void notifyDataSetChanged() {
		imgPath = db.getPaths();
		// check image path's validation
		// if the image is no longer in the gallery,
		// delete the path from database and ArrayList
		for (int i = 0; i < imgPath.size();) {
			if (!imgPath.get(i).startsWith("img_")
					&& !(new File(imgPath.get(i))).exists()) {
				db.deletePath(imgPath.get(i));
				imgPath.remove(i);
			} else
				i++;
		}
		count = imgPath.size();
		while (cache.size() > count) {
			int index = cache.size() - 1;
			cache.remove(index);
			tag.remove(index);
		}
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imgView;

		if (convertView == null) {
			// create a new view
			imgView = new ImageView(myContext);
			imgView.setLayoutParams(new GridView.LayoutParams(100, 100));
		} else {
			imgView = (ImageView) convertView;
		}

		if (!(cache.size() >= position + 1 && tag.get(position).compareTo(
				imgPath.get(position)) == 0)) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap thumb;
			if (imgPath.get(position).startsWith("img_")) {
				int id = Integer.parseInt(imgPath.get(position).substring(4));
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(myContext.getResources(), id,
						options);
				int imgHeight = options.outHeight;
				int imgWidth = options.outWidth;
				if (imgHeight > 100 || imgWidth > 100) {
					int heightRatio = (int) ((float) imgHeight / 100.0) + 1;
					int widthRatio = (int) ((float) imgWidth / 100.0) + 1;
					options.inSampleSize = heightRatio > widthRatio ? heightRatio
							: widthRatio;
				}
				options.inJustDecodeBounds = false;
				thumb = BitmapFactory.decodeResource(myContext.getResources(),
						id, options);
			} else {
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(imgPath.get(position), options);
				int imgHeight = options.outHeight;
				int imgWidth = options.outWidth;
				if (imgHeight > 100 || imgWidth > 100) {
					int heightRatio = (int) ((float) imgHeight / 100.0) + 1;
					int widthRatio = (int) ((float) imgWidth / 100.0) + 1;
					options.inSampleSize = heightRatio > widthRatio ? heightRatio
							: widthRatio;
				}
				options.inJustDecodeBounds = false;
				thumb = BitmapFactory
						.decodeFile(imgPath.get(position), options);
			}
			if (cache.size() >= position + 1) {
				tag.set(position, imgPath.get(position));
				cache.set(position, thumb);
			} else {
				tag.add(imgPath.get(position));
				cache.add(thumb);
			}
		}

		imgView.setImageBitmap(cache.get(position));
		imgView.setTag(tag.get(position));
		return imgView;
	}

}
