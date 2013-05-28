package edu.olemiss.mmdock.memes;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MemeListAdapterWithCache extends ArrayAdapter<Meme> {

	private int resource;
	private LayoutInflater inflater;
	private Context context;

	public MemeListAdapterWithCache(Context ctx, int resourceId,
			List<Meme> objects) {
		super(ctx, resourceId, objects);
		resource = resourceId;
		inflater = LayoutInflater.from(ctx);
		context = ctx;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Meme Meme = getItem(position);
		MemeListViewCache viewCache;

		if (convertView == null) {
			convertView = (RelativeLayout) inflater.inflate(resource, null);
			viewCache = new MemeListViewCache(convertView);
			convertView.setTag(viewCache);
		} else {
			convertView = (RelativeLayout) convertView;
			viewCache = (MemeListViewCache) convertView.getTag();
		}

		TextView txtName = viewCache.getTextNameMeme(resource);
		txtName.setText(Meme.getName());

		ImageView imageMeme = viewCache.getImageView(resource);
		String uri = "drawable/" + Meme.getImage();
		int imageResource = context.getResources().getIdentifier(uri, null,
				context.getPackageName());
		Drawable image = context.getResources().getDrawable(imageResource);
		imageMeme.setImageDrawable(image);

		return convertView;

	}

}
