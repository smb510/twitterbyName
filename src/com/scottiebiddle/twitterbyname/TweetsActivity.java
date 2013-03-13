package com.scottiebiddle.twitterbyname;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class TweetsActivity extends Activity {
	
	public Paging page;
	private String currentUser;
	public boolean loading;
	private HashMap<String, Bitmap> avatars;
	public TwitterHelper loader;
	public class TwitterHelper extends AsyncTask<String, Long, ResponseList<Status>>
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
		page = new Paging(1, 20);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	avatars = new HashMap<String, Bitmap>();
    	loading = false;
    	page = new Paging(1, 20);
    	final TweetsActivity act = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweets);
        ListView list = (ListView) findViewById(R.id.tweetsView);
        list.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView arg0, int first, int visible, int total) {
				//Log.d("SBRuntime", "First : " + first + " visible : " + visible + " Total: " + total);

				boolean loadMore = first + visible >= total && total != 0;
				if(loadMore && !loading)
				{
					Log.d("SBRuntime", "Load more!");
					Paging page = act.page;
					page.setPage(page.getPage() + 1);
					loading = true;
					loader = new TwitterHelper(act);
					loader.execute(currentUser);
					
					
				}
				
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}});
       //new TwitterHelper(this).execute("smb510");  
       currentUser = "";
       EditText searchBar = (EditText) findViewById(R.id.searchBar);
       searchBar.setOnEditorActionListener(new OnEditorActionListener() {
    	    

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(arg1 == EditorInfo.IME_ACTION_DONE)
				{
					searchUserByName(arg0.getText().toString());

				}
				return false;
			}
    	});
       
       
    }

    
    public void searchUser(View view)
    {
    	ListView list = (ListView) findViewById(R.id.tweetsView);
    	list.setAdapter(null);
    	list.refreshDrawableState();
    	EditText text = (EditText) findViewById(R.id.searchBar);
    	loader = new TwitterHelper(this);
    	loader.execute(text.getText().toString());
    	currentUser = text.getText().toString();
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }
    
    public void searchUserByName(String username)
    {
    	
    	loader = new TwitterHelper(this);
    	loader.execute(username);
    	currentUser = username;
    	EditText e = (EditText) findViewById(R.id.searchBar);
    	if(username.charAt(0) == '@')
    	{
    	e.setText(currentUser.substring(1));
    	}
    }
    
    public void searchUserFromReply(View view)
    {
    	TextView text = (TextView) view;
    	String username = text.getContentDescription().toString();
    	new TwitterHelper(this).execute(username);
    }
    
    public void getUserInfo(View view)
    {
    	ImageView src= (ImageView) view;
    	String user = (String) src.getContentDescription();
    	String[] info = user.split("#");
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View infoView = li.inflate(R.layout.info_popup, null);
    	ImageView avatar = (ImageView) infoView.findViewById(R.id.user_avatar);
    	avatar.setImageDrawable(src.getDrawable());
    	TextView username = (TextView) infoView.findViewById(R.id.username);
    	TextView following = (TextView) infoView.findViewById(R.id.following);
    	TextView followers = (TextView) infoView.findViewById(R.id.followers);
    	
    	username.setText("@" + info[0]);
    	username.setTextSize(22);
    	following.setText(info[2] + " following");
    	followers.setText(info[1] + " followers");
    	following.setTextSize(18);
    	followers.setTextSize(18);
    	builder.setView(infoView);
    	AlertDialog dialog = builder.create();
    	dialog.setButton(AlertDialog.BUTTON_POSITIVE, "View Tweets", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				AlertDialog sender = (AlertDialog) dialog;
				TextView name = (TextView) sender.findViewById(R.id.username);
				searchUserByName(name.getText().toString());
				sender.dismiss();
				
			}	
    	});
    	
    	dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
			}
		});
    	dialog.show();
    	
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tweets, menu);
        return true;
    }
    
}
