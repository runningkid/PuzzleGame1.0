package com.chunya.puzzlegame;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;


public class MainActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener, OnClickListener {
	private static int LOAD_IMAGE = 1;
	public DBAdapter db;
	private GridView grid;
	private ImageAdapter imgAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		db = new DBAdapter(this);
		db.open();

		grid = (GridView) findViewById(R.id.gridview);
		imgAdapter = new ImageAdapter(this, db);
		grid.setAdapter(imgAdapter);
		grid.setOnItemClickListener(this);
		grid.setOnItemLongClickListener(this);
		ImageView addimg = (ImageView) findViewById(R.id.addpicfrm);
		ImageView delimg = (ImageView) findViewById(R.id.delpicfrm);
		addimg.setOnClickListener(this);
		delimg.setOnClickListener(this);
		delimg.setOnDragListener(new OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
				Drawable enterShape = getResources().getDrawable(
						R.drawable.shape_droptarget);
				switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_STARTED:
					break;
				case DragEvent.ACTION_DRAG_ENTERED:
					v.setBackground(enterShape);
					break;
				case DragEvent.ACTION_DRAG_EXITED:
					v.setBackground(null);
					break;
				case DragEvent.ACTION_DROP:
					View img = (View) event.getLocalState();
					String path = img.getTag().toString();
					db.deletePath(path);
					break;
				case DragEvent.ACTION_DRAG_ENDED:
					v.setBackground(null);
					imgAdapter.notifyDataSetChanged();
					grid.setAdapter(imgAdapter);
					grid.invalidateViews();
					break;
				}
				return true;
			}

		});
	}

	public void onDestroy() {
		super.onDestroy();
		db.close();
	}

	/*
	 * public void onResume() { super.onResume(); db.open(); }
	 * 
	 * public void onPause() { super.onPause(); db.close(); }
	 */

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// create the Intent to open our ShowImage activity.
		Intent i = new Intent(this, ShowImage.class);

		// pass a key:value pair into the 'extra' bundle for
		// the intent so the activity is made aware which
		// photo was selected.
		i.putExtra("imageToDisplay", v.getTag().toString());

		// start our activity
		startActivity(i);
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		ClipData data = ClipData.newPlainText("TAG", view.getTag().toString());
		DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
		view.startDrag(data, shadowBuilder, view, 0);
		view.setVisibility(View.INVISIBLE);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addpicfrm:
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, LOAD_IMAGE);
			break;
		case R.id.delpicfrm:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK
				&& data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			final String picturePath = cursor.getString(columnIndex);
			cursor.close();

			if (!db.queryPath(picturePath)) {
				db.insertPath(picturePath);
				imgAdapter.notifyDataSetChanged();
				grid.setAdapter(imgAdapter);
				grid.invalidateViews();
			}
		}
	}
}
