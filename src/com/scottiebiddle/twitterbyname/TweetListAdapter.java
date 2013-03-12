package com.scottiebiddle.twitterbyname;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import twitter4j.Status;
import twitter4j.User;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetListAdapter<T> extends ArrayAdapter<T> {


private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
ImageView bmImage;

public DownloadImageTask(ImageView bmImage) {
this.bmImage = bmImage;
}

protected Bitmap doInBackground(String... urls) {
String urldisplay = urls[0];
Bitmap mIcon11 = null;
try {
    InputStream in = new java.net.URL(urldisplay).openStream();
    mIcon11 = BitmapFactory.decodeStream(in);
} catch (Exception e) {
    Log.e("Error", e.getMessage());
    e.printStackTrace();
}
return mIcon11;
}

protected void onPostExecute(Bitmap result) {
bmImage.setImageBitmap(result);

}
}
	
	
	public TweetListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	public TweetListAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		// TODO Auto-generated constructor stub
	}

	public TweetListAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public TweetListAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public TweetListAdapter(Context context, int resource,
			int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public TweetListAdapter(Context context, int resource,
			int textViewResourceId, List<T> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}
	
	
	public View getView(int index, View v, ViewGroup vg)
	{
		View view = v;
		if(view == null)
		{
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = li.inflate(R.layout.avatar_list, null);
		}
		
		TextView content = (TextView) view.findViewById(R.id.tweetContent);
		TextView metadata = (TextView) view.findViewById(R.id.tweetMetadata);
		twitter4j.Status s = (twitter4j.Status) getItem(index);
		content.setText(s.getText());
		User u = s.getUser();
		metadata.setText("by @" + u.getScreenName() + " at " + s.getCreatedAt().toString());
		
		ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
		imageView.setContentDescription(u.getScreenName());
		String url = s.getUser().getProfileImageURL();
		new DownloadImageTask(imageView).execute(url);
		
		
		
		return view;
		
		
	}
	

}