package com.collinlavoie.webtemp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String DEBUG_TAG = "HttpExample";
    private TextView textView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.string_temperature);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void checkTemp(View view) {
        String stringUrl = "http://collinlavoie.dyndns.org:8080";
        ConnectivityManager connMgr = (ConnectivityManager) 
                getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
            	 new DownloadWebpageText().execute(stringUrl);
            } else {
            	textView.setText("No network connection available.");
            }
    }
    
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageText extends AsyncTask<String, Integer, String> {
    	
       @Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           try {
               return downloadUrl(urls[0]);
           } catch (IOException e) {
               return "Unable to retrieve web page. URL may be invalid.";
           }
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
           textView.setText(result);
      }

   }    

 // Given a URL, establishes an HttpUrlConnection and retrieves
 // the web page content as a InputStream, which it returns as
 // a string.
 private String downloadUrl(String myurl) throws IOException {
     InputStream is = null;
     // Only display the first 500 characters of the retrieved
     // web page content.
     int len = 500;
         
     try {
         URL url = new URL(myurl);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setReadTimeout(10000 /* milliseconds */);
         conn.setConnectTimeout(15000 /* milliseconds */);
         conn.setRequestMethod("GET");
         conn.setDoInput(true);
         // Starts the query
         conn.connect();
         int response = conn.getResponseCode();
         Log.d(DEBUG_TAG, "The response is: " + response);
         is = conn.getInputStream();

         // Convert the InputStream into a string
         String contentAsString = readIt(is, len);
         return contentAsString;
         
     // Makes sure that the InputStream is closed after the app is
     // finished using it.
     } finally {
         if (is != null) {
             is.close();
         } 
     }
 }
 
//Reads an InputStream and converts it to a String.
public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
  Reader reader = null;
  reader = new InputStreamReader(stream, "UTF-8");        
  char[] buffer = new char[len];
  reader.read(buffer);
  return new String(buffer);
}
}
