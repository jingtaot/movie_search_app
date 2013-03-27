/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;


public class MainPage extends Activity {

    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php
    public static final String APP_ID = "412014455537871";

    private TextView mText;
    private Button mRequestButton;
    private Button mPostButton;
    private Button mDeleteButton;
    private Button mUploadButton;
    private Button mSearchButton;
    private Spinner mMediaSpinner;
    private EditText mMovieNameInput;

    private static Facebook mFacebook;
    private static AsyncFacebookRunner mAsyncRunner;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (APP_ID == null) {
            Util.showAlert(this, "Warning", "Facebook Applicaton ID must be " +
                    "specified before running this example: see Example.java");
        }

        setContentView(R.layout.main);
        mMovieNameInput = (EditText) findViewById(R.id.title);
        mMediaSpinner = (Spinner) findViewById(R.id.media);
        mSearchButton = (Button) findViewById(R.id.search);
         mFacebook = new Facebook(APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);

        SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        // set up the Spinner for the media list selection
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        this, R.array.media_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMediaSpinner.setAdapter(adapter);
        
//for search button         
      final Context context = this;
        mSearchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String servletURL;
            	String movieName = mMovieNameInput.getText().toString();
            	// check the input text of movie, if the text is empty give user alert
            	movieName = movieName.trim();
            	if (movieName.length() == 0)
            	{
            		Toast toast = Toast.makeText(context, "Please enter a movie name", 
            				Toast.LENGTH_LONG);
            		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 70);
            		toast.show();
            	}
            	// if movie name is not empty
            	else
            	{
            		// remove any extra whitespace
            		movieName = movieName.replaceAll("\\s+", "+");
	            	String mediaList = mMediaSpinner.getSelectedItem().toString();
	            	if (mediaList.equals("Feature Film"))
	            	{
	            		mediaList = "feature";
	            	}
            		mediaList = mediaList.replaceAll("\\s+", "+");
	            	// construct the query string
	            	// construct the final URL to Servlet
            		//String servletString = "?" + "title=" + movieName + "&" + "title_type=" + mediaList;
	            	//servletURL = "http://cs-server.usc.edu:10854/examples/servlet/Amovie"
	            	//+ servletString;
	            	//String servletString = "?" + "title=" + movieName + "&" + "media=" + mediaList;
	            	//servletURL = "http://cs-server.usc.edu:34404/examples/servlet/HelloWorldExample?title=" + movieName + "&" + "media=" + mediaList;
	            	//+ servletString;
	            	servletURL = "http://cs-server.usc.edu:10854/examples/servlet/Amovie?title="+movieName+"&"+"title_type="+mediaList;
	            	BufferedReader in = null;
	                try {
	                	// REFERENCE: this part of code is modified from:
	                	// "Example of HTTP GET Request using HttpClient in Android"
	                	// http://w3mentor.com/learn/java/android-development/android-http-services/example-of-http-get-request-using-httpclient-in-android/
	                	// get response (JSON string) from Servlet 
	                	HttpClient client = new DefaultHttpClient();
	                    HttpGet request = new HttpGet();
	                    request.setURI(new URI(servletURL));
	                    HttpResponse response = client.execute(request);
	                    in = new BufferedReader
	                    (new InputStreamReader(response.getEntity().getContent()));
	                    StringBuffer sb = new StringBuffer("");
	                    String line = "";
	                    String NL = System.getProperty("line.separator");
	                    while ((line = in.readLine()) != null) {
	                        sb.append(line + NL);
	                    }
	                    in.close();
	                    String page = sb.toString();
	                    //test for JSON string
                    	/*LinearLayout lView = new LinearLayout(context);
                    	TextView myText = new TextView(context);
                    	myText.setText(page);
                    	lView.addView(myText);
                    	setContentView(lView);*/
	                    
	                   
	                    // convert the JSON string to real JSON and get out the movie JSON array
	                    // to check if there is any movie data
	                    JSONObject finalJson;
	            		JSONObject movieJson;
	            		JSONArray movieJsonArray;
	            		finalJson = new JSONObject(page);
	        			movieJson = finalJson.getJSONObject("results");
	        			//System.out.println(movieJson);
	        			movieJsonArray = movieJson.getJSONArray("result");

	        			// if the response contains some movie data
	                    if (movieJsonArray.length() != 0)
	                    {

	                    	// start the ListView activity, and pass the JSON string to it
		                    Intent intent = new Intent(context, MovieListActivity.class);
		                    intent.putExtra("finalJson", page);
		                    startActivity(intent);   
	                    }
	                    // if the response does not contain any movie data,
	                    // show user that there is no result for this search
	                    else
	                    {
	                    	Toast toast = Toast.makeText(getBaseContext(), 
	                    			"No movie found for this search", 
	                    			Toast.LENGTH_LONG);
	                        toast.setGravity(Gravity.CENTER, 0, 0);
	                		toast.show();
	                    }
	                }
	                catch (URISyntaxException e) 
	                {
	                	e.printStackTrace();
	                }
	                catch (ClientProtocolException e)
	                {
	                	e.printStackTrace();
	                }
	                catch (JSONException e) 
	                {
						e.printStackTrace();
					}
	                catch (IOException e) 
	                {
						e.printStackTrace();
					}
	                finally {
	                	if (in != null) {
	                		try {
	                			in.close();
	                		} 
	                		catch (IOException e)
	                		{
	                			e.printStackTrace();
	                		}
	                    }
	                }
            	}
            }
        });
    }
    
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    public class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
            mText.setText("You have logged in! ");
            mRequestButton.setVisibility(View.VISIBLE);
            mUploadButton.setVisibility(View.VISIBLE);
            mPostButton.setVisibility(View.VISIBLE);
        }

        public void onAuthFail(String error) {
            mText.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            mText.setText("Logging out...");
        }

        public void onLogoutFinish() {
            mText.setText("You have logged out! ");
            mRequestButton.setVisibility(View.INVISIBLE);
            mUploadButton.setVisibility(View.INVISIBLE);
            mPostButton.setVisibility(View.INVISIBLE);
        }
    }

    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                MainPage.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }

    public class SampleUploadListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            try {
                // process the response here: (executed in background thread)
                Log.d("Facebook-Example", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String src = json.getString("src");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                MainPage.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mText.setText("Hello there, photo has been uploaded at \n" + src);
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    public class WallPostRequestListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            Log.d("Facebook-Example", "Got response: " + response);
            String message = "<empty>";
            try {
                JSONObject json = Util.parseJson(response);
                message = json.getString("message");
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
            final String text = "Your Wall Post: " + message;
            MainPage.this.runOnUiThread(new Runnable() {
                public void run() {
                    mText.setText(text);
                }
            });
        }
    }

    public class WallPostDeleteListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
            if (response.equals("true")) {
                Log.d("Facebook-Example", "Successfully deleted wall post");
                MainPage.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mDeleteButton.setVisibility(View.INVISIBLE);
                        mText.setText("Deleted Wall Post");
                    }
                });
            } else {
                Log.d("Facebook-Example", "Could not delete wall post");
            }
        }
    }

    public class SampleDialogListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                mAsyncRunner.request(postId, new WallPostRequestListener());
                mDeleteButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        mAsyncRunner.request(postId, new Bundle(), "DELETE",
                                new WallPostDeleteListener(), null);
                    }
                });
                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                Log.d("Facebook-Example", "No wall post made");
            }
        }
    }
    public static Facebook getFacebook(){
    	return mFacebook;
    }
    
    public static AsyncFacebookRunner getAsyncRunner()
    {
    	return mAsyncRunner;
    }

}
