package com.scottiebiddle.twitterbyname;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class TweetsActivity extends Activity {
	
	public Paging page;
	private String currentUser;
	public boolean loading;
	private HashMap<String, Bitmap> avatars;
	
	private class TwitterHelper extends AsyncTask<String, Long, ResponseList<Status>>
	{
		private ConfigurationBuilder conf;
		private TweetsActivity tweetActivity;
		private ProgressDialog progress;
		
		public TwitterHelper(TweetsActivity active)
		{
			super();
			tweetActivity = active;
			conf = new ConfigurationBuilder();
			conf.setDebugEnabled(true)
	        .setOAuthConsumerKey("6aVkC0ZimU3XbrIoRHg7Q")
	        .setOAuthConsumerSecret("aMcNS6bC5mFORibkS1uUVvPQwVDqG6InNo8QXk2U")
	        .setOAuthAccessToken("768670-8BQ1yZGAXEGa76grEBACjZ2RtZbUinUBTG7HpJA")
	        .setOAuthAccessTokenSecret("8tDb4Ae4FZD6F7KNkiQS3oGtRw8pzErodN9iYWX8uE");
			
		}
		
		protected void onPreExecute()
		{
			progress = ProgressDialog.show(tweetActivity, "Loading", "Loading more tweets. Be Patient!", true);
		}
		
		
		protected void onPostExecute(ResponseList<twitter4j.Status> tweets)
		{
			if(tweets != null)
			{
			tweetActivity.updateTweets(tweets);
			Log.d("SBRuntime", "Tweets Retrieved!");
			}
			else
			{
				progress.hide();
				Toast.makeText(tweetActivity, "An error occurred. You may not be able to see that user. Try again.", Toast.LENGTH_LONG).show();
				Log.w("SBRuntime", "Tweets were null :-(");
			}
			tweetActivity.loading = false;
			//Toast.makeText(getApplicationContext(), "Retrieved " + tweets.size() + " tweets.", Toast.LENGTH_LONG).show();
			progress.hide();
			
			
		}
		@Override
		protected ResponseList<twitter4j.Status> doInBackground(String... arg0) {
			Log.d("SBRuntime", "Starting to get tweets for " + arg0[0]);
			Twitter t = new TwitterFactory(conf.build()).getInstance();
			ResponseList<twitter4j.Status> tweets = null;
			Log.d("SBRuntime", tweetActivity.page.toString());
			try
			{
			tweets = t.getUserTimeline(arg0[0], tweetActivity.page);
			}
			catch (TwitterException te)
			{
				Log.e("SBRuntime", te.getMessage());
				return null;
				//Toast.makeText(getApplicationContext(), te.getMessage(), Toast.LENGTH_LONG).show();
			}
			// TODO Auto-generated method stub
			Log.d("SBRuntime", "got tweets, yo! for " + arg0[0]);
			return tweets;
		}
		
		
		
		
		
	}
	
	
	public void updateTweets(ResponseList<Status> tweets)
	{
		
		if(tweets == null)
		{
			Toast.makeText(getApplicationContext(), "Crap! An error occurred! Try again!", Toast.LENGTH_LONG).show();
			return;
		}
		ListView list  = (ListView) findViewById(R.id.tweetsView);	
		if(page.getPage() != 1)
		{
		TweetListAdapter<Status> tweetAdapter = (TweetListAdapter<Status>) list.getAdapter();
		if(tweetAdapter != null)
		{
		tweetAdapter.addAll(tweets);
		}
		list.refreshDrawableState();
		return;
		}
		
		TweetListAdapter<Status> tweetAdapter = new TweetListAdapter<Status>(this, R.layout.avatar_list, tweets);		
		list.setAdapter(tweetAdapter);
		list.refreshDrawableState();
		page = new Paging(1, 40);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	avatars = new HashMap<String, Bitmap>();
    	loading = false;
    	page = new Paging(1, 40);
    	final TweetsActivity act = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);
        ListView list = (ListView) findViewById(R.id.tweetsView);
        list.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView arg0, int first, int visible, int total) {
				Log.d("SBRuntime", "First : " + first + " visible : " + visible + " Total: " + total);

				boolean loadMore = first + visible >= total && total != 0;
				if(loadMore && !loading)
				{
					Log.d("SBRuntime", "Load more!");
					Paging page = act.page;
					page.setPage(page.getPage() + 1);
					loading = true;
					new TwitterHelper(act).execute(currentUser);
					
					
				}
				
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}});
       new TwitterHelper(this).execute("smb510");  
       currentUser = "smb510";
    }

    
    public void searchUser(View view)
    {
    	ListView list = (ListView) findViewById(R.id.tweetsView);
    	list.setAdapter(null);
    	list.refreshDrawableState();
    	EditText text = (EditText) findViewById(R.id.searchBar);
    	new TwitterHelper(this).execute(text.getText().toString());
    	currentUser = text.getText().toString();
    }
    
    public void getUserInfo(View view)
    {
    	ImageView src= (ImageView) view;
    	String user = (String) src.getContentDescription();
    	Toast.makeText(this, user, Toast.LENGTH_SHORT).show();
    	
    	
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tweets, menu);
        return true;
    }
    
}