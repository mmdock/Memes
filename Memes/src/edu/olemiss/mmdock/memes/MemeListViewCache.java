package edu.olemiss.mmdock.memes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class MemeListViewCache {

	private View baseView;
	private TextView textNameMeme;
	private ImageView imageMeme;

	public MemeListViewCache(View baseView) {

		this.baseView = baseView;
	}

	public View getViewBase() {
		return baseView;
	}

	public TextView getTextNameMeme(int resource) {
		if (textNameMeme == null) {
			textNameMeme = (TextView) baseView.findViewById(R.id.MemeName);
		}
		return textNameMeme;
	}

	public ImageView getImageView(int resource) {
		if (imageMeme == null) {
			imageMeme = (ImageView) baseView.findViewById(R.id.ImageMeme);
		}
		return imageMeme;
	}
}