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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	
	@SuppressWarnings("unchecked")
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
		TextView replyTo = (TextView) view.findViewById(R.id.tweetReply);
		twitter4j.Status s = (twitter4j.Status) getItem(index);
		if (s.isRetweet())
		{
			Status r = s.getRetweetedStatus();
			remove((T) s);
			insert((T) r, index);
			s = r;
		}
		content.setText(s.getText());
		User u = s.getUser();
		metadata.setText("by @" + u.getScreenName() + " on " + s.getCreatedAt().toString());
		if (s.getInReplyToScreenName() != null)
		{
			replyTo.setText(Html.fromHtml("in reply to @<b>" + s.getInReplyToScreenName() + "</b>"));
			replyTo.setClickable(true);
			replyTo.setContentDescription(s.getInReplyToScreenName());
		}
		else
		{
			replyTo.setHeight(0);
			//replyTo.setClickable(false);
		}
		
		ImageView imageView = (ImageView) view.findViewById(R.id.avatar);
		imageView.setContentDescription(u.getScreenName() + "#" + u.getFollowersCount() + "#" + u.getFriendsCount());
		String url = s.getUser().getProfileImageURL();
		new DownloadImageTask(imageView).execute(url);
		
		
		
		return view;
		
		
	}
	

}
