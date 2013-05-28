package edu.olemiss.mmdock.memes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class MemeList extends Activity implements OnItemClickListener {

	ArrayList<Meme> listMeme;
	ListView listViewMeme;
	MemeListAdapterWithCache adapter;
	Context ctx = this;
	String[] array = { "One Does Not Simply", "Grumpy Cat", "Futurama Fry",
			"Condescending Wonka", "Aliens", "Brace Yourselves",
			"Bad Luck Brian", "Philosoraptor", "Third World Success",
			"Scumbag Steve", "Skeptical Baby", "Bad Ass Tyson" };

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.meme_list);

		createArrayList();
		setListViewListenerAndAdapter();

	}

	public void createArrayList() {
		listMeme = new ArrayList<Meme>();
		listMeme.add(new Meme("From Device", "picture"));
		for (int i = 0; i < 12; i++) {
			listMeme.add(new Meme(array[i], "pic" + i));
		}
	}

	public void setListViewListenerAndAdapter() {
		listViewMeme = (ListView) findViewById(R.id.Meme_list);
		adapter = new MemeListAdapterWithCache(ctx, R.layout.meme_row_item,
				listMeme);
		listViewMeme.setAdapter(adapter);
		listViewMeme.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			retrieveFromDevice();
		} else {
			Meme item = (Meme) parent.getItemAtPosition(position);
			String uri = "drawable/" + item.getImage();
			int imageResource = this.getResources().getIdentifier(uri, null,
					this.getPackageName());
			Uri theUri = Uri
					.parse("android.resource://edu.olemiss.mmdock.memes/"
							+ imageResource);
			retIntention(theUri);
		}
	}

	public void retIntention(Uri image) {
		Intent returnIntent = new Intent();
		returnIntent.setData(image);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	public void retrieveFromDevice() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		startActivityForResult(intent, 1);
	}

	// public File getFile(Context context) {
	// File path = new File(Environment.getExternalStorageDirectory(),
	// context.getPackageName());
	// if (!path.exists()) {
	// path.mkdir();
	// }
	// return new File(path, "image.tmp");
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Intent returnIntent = new Intent();
		if (requestCode == 1) {

			if (data == null) {
				showToast(R.string.nothing_picked);
				return;
			}

			if (data.getData() == null) {
				showToast(R.string.nothing_picked);
				return;
			}
			returnIntent.setData(data.getData());
			setResult(resultCode, returnIntent);
		} else {
			setResult(-1000, returnIntent);
		}

		finish();

	}

	public void showToast(String string) {
		Toast.makeText(ctx, string, Toast.LENGTH_LONG).show();
	}

	public void showToast(int string) {
		Toast.makeText(ctx, string, Toast.LENGTH_LONG).show();
	}

//	public void notify(String title, String text) {
//		String ns = Context.NOTIFICATION_SERVICE;
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
//		int icon = R.drawable.ic_launcher;
//		CharSequence tickerText = title;
//		long when = System.currentTimeMillis();
//
//		Context context = getApplicationContext();
//		CharSequence contentTitle = title;
//		CharSequence contentText = text;
//		Intent notificationIntent = new Intent(ctx, MemeEditActivity.class);
//		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
//				notificationIntent, 0);
//
//		Notification notification = new NotificationCompat.Builder(context)
//				.setAutoCancel(true).setContentTitle(contentTitle)
//				.setContentText(contentText).setContentIntent(contentIntent)
//				.setSmallIcon(icon).setWhen(when).setTicker(tickerText).build();
//		// Clicking on the notification will re open the app on top of the
//		// current
//		mNotificationManager.notify(1, notification);
//	}
}
