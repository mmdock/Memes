package edu.olemiss.mmdock.memes;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class MemeEditActivity extends Activity {

	private static final String IMAGE_MIME_TYPE = "image/jpeg";
	private static final int SDK_INT = android.os.Build.VERSION.SDK_INT;
	private static final int PHOTO_PICKED = 1;
	private static final Bitmap.CompressFormat SAVED_IMAGE_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
	private static final String SAVED_IMAGE_EXTENSION = ".jpg";
	private static final String SAVE_DIRECTORY = "/myMEMES";
	private Uri PhotoUri;
	private Uri SavedImageUri;
	private MemeView memeView;
	private ShareActionProvider shareActionProvider;
	private String SavedImageFilename = "Saved image file name";
	private AlertDialog captionDialog;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meme_activity);
		memeView = (MemeView) findViewById(R.id.main_image);
		//registerForContextMenu(memeView);
		if (SDK_INT > 10) {
			ActionBar ab = getActionBar();
			ab.setDisplayShowTitleEnabled(false);
			ab.setDisplayShowHomeEnabled(false);
		}

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_meme_list, menu);
		if (SDK_INT >= 14) {
			shareActionProvider = (ShareActionProvider) menu.findItem(
					R.id.Share).getActionProvider();
			// mShareActionProvider.setShareIntent(new Intent());
		}
		return true;
	}

	public void saveImage() {
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File myDir = new File(root + SAVE_DIRECTORY);
		myDir.mkdirs();
		Random generator = new Random();
		int n = generator.nextInt(10000);
		String fname = "Image-" + n + SAVED_IMAGE_EXTENSION;
		File file = new File(myDir, fname);

		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			Bitmap bitmap = ((BitmapDrawable) memeView.getDrawable())
					.getBitmap();
			bitmap.compress(SAVED_IMAGE_COMPRESS_FORMAT, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		SavedImageUri = Uri.fromFile(file);
	}

	public void saveImageForShare() {
		String root = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File myDir = new File(root + SAVE_DIRECTORY);
		myDir.mkdirs();
		String fname = "temp" + SAVED_IMAGE_EXTENSION;
		File file = new File(myDir, fname);

		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			Bitmap bitmap = ((BitmapDrawable) memeView.getDrawable())
					.getBitmap();
			bitmap.compress(SAVED_IMAGE_COMPRESS_FORMAT, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		SavedImageUri = Uri.fromFile(file);
	}

	private Intent getDefaultShareIntent() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType(IMAGE_MIME_TYPE);
		intent.putExtra(Intent.EXTRA_STREAM, SavedImageUri);
		return intent;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// State from the Activity:
		outState.putParcelable("PHOTO_URI", PhotoUri);
		outState.putString("SAVED_IMAGE_FILENAME", SavedImageFilename);
		outState.putParcelable("SAVED_IMAGE_URI", SavedImageUri);

		// State from the LolcatView:
		outState.putString("top", memeView.getTopCaption());
		outState.putString("bottom", memeView.getBottomCaption());
		outState.putIntArray("positions", memeView.getCaptionPositions());
	}

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
//		menu.setHeaderTitle("What to do...");
//		menu.add(0, R.id.Captions, 0, "Add Captions");
//		menu.add(0, R.id.Chooser, 0, "Load Image");
//		menu.add(0, R.id.Save, 0, "Save Image");
//		menu.add(0, R.id.Share, 0, "Share Image");
//		menu.add(0, R.id.Remove, 0, "Clear Image");
//		menu.add(0, R.id.Refresh, 0, "Clear Captions");
//	}
//
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		onOptionsItemSelected(item);
//		return true;
//	}

	public void loadPhoto(Uri uri) {

		clearPhoto();
		PhotoUri = uri;
		memeView.loadFromUri(PhotoUri);

		//memeView.setLongClickable(true);
	}

	private void clearPhoto() {
		memeView.clear();

		PhotoUri = null;
		SavedImageFilename = null;
		SavedImageUri = null;

		clearCaptions();
	}

	@Override
	protected void onRestoreInstanceState(Bundle sIS) {
		Uri photoUri = sIS.getParcelable("PHOTO_URI");
		if (photoUri != null) {
			loadPhoto(photoUri);
		}

		SavedImageFilename = sIS.getString("SAVED_IMAGE_FILENAME");
		SavedImageUri = sIS.getParcelable("SAVED_IMAGE_URI");

		String topCaption = sIS.getString("top");
		String bottomCaption = sIS.getString("bottom");
		int[] captionPositions = sIS.getIntArray("positions");
		if (!TextUtils.isEmpty(topCaption)) {
			memeView.setCaptions(topCaption, bottomCaption);
			memeView.setCaptionPositions(captionPositions);
		}
		super.onRestoreInstanceState(sIS);
	}

	private void clearCaptions() {
		memeView.clearCaptions();

		// Clear the text fields in the caption dialog too.
		if (captionDialog != null) {
			EditText topText = (EditText) captionDialog
					.findViewById(R.id.top_edittext);
			topText.setText("");
			EditText bottomText = (EditText) captionDialog
					.findViewById(R.id.bottom_edittext);
			bottomText.setText("");
			topText.requestFocus();
		}

		// This also invalidates any image we've previously written to the
		// SD card...
		SavedImageFilename = null;
		SavedImageUri = null;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.Share:
			// if (SDK_INT < 14) {
			shareImage();
			// }else{
			// mShareActionProvider.setShareIntent(new Intent());
			// }
			break;
		case R.id.Chooser:
			chooseImage();
			break;
		case R.id.Save:
			saveImage();
			break;
		case R.id.Refresh:
			clearCaptions();
			break;
		case R.id.Remove:
			clearPhoto();
			//memeView.setLongClickable(false);
			break;
		case R.id.Captions:
			showDialog(1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void shareImage() {
		saveImageForShare();
		Intent intent = getDefaultShareIntent();
		try {
			startActivity(Intent.createChooser(intent, getResources()
					.getString(R.string.sendImage_label)));
		} catch (android.content.ActivityNotFoundException ex) {
			showToast(R.string.share_failed);
		}
	}

	public void chooseImage() {
		Intent intent = new Intent(this, MemeList.class);
		startActivityForResult(intent, PHOTO_PICKED);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			showToast(R.string.nothing_picked);
			return;
		}

		if (requestCode == PHOTO_PICKED) {

			if (data == null) {
				showToast(R.string.nothing_picked);
				return;
			}

			if (data.getData() == null) {
				showToast(R.string.nothing_picked);
				return;
			}

			loadPhoto(data.getData());
		}
	}

	public void showToast(int string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}

	public void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}

	private void updateCaptionsFromDialog() {

		if (captionDialog == null) {
			return;
		}

		// Get the two caption strings:

		EditText topText = (EditText) captionDialog
				.findViewById(R.id.top_edittext);
		String topString = topText.getText().toString();

		EditText bottomText = (EditText) captionDialog
				.findViewById(R.id.bottom_edittext);
		String bottomString = bottomText.getText().toString();

		memeView.setCaptions(topString, bottomString);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// Can add other dialogs, for things like a saving screen or something.
		if (id == 1) {
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.caption_dialog,
					null);
			captionDialog = new AlertDialog.Builder(this)
					.setTitle("Captions:")
					.setIcon(0)
					.setView(textEntryView)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									updateCaptionsFromDialog();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Nothing to do here (for now at least)
								}
							}).create();

		}
		return captionDialog;
	}

}
