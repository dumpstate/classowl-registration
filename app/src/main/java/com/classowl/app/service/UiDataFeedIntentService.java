package com.classowl.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.classowl.app.message.Constants;
import com.classowl.app.message.SchoolsMsg;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UiDataFeedIntentService extends IntentService {
    private static final String TAG = UiDataFeedIntentService.class.getSimpleName();

    private final LocalBroadcastManager mBroadcastManager;

    public UiDataFeedIntentService() {
        super(UiDataFeedIntentService.class.getSimpleName());
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private static String get(final String url) {
        InputStream is = null;
        try {
            final HttpClient httpClient = new DefaultHttpClient();
            final HttpResponse response = httpClient.execute(new HttpGet(url));
            is = response.getEntity().getContent();
            if(is != null) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                final StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } catch(IOException e) {
            Log.e(TAG, "Failed to retrieve: " + url);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close stream.");
                }
            }
        }
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.hasExtra(Constants.MSG_TYPE)) {
            switch(intent.getIntExtra(Constants.MSG_TYPE, -1)) {
                case Constants.MSG_GET_SCHOOLS: {
                    Log.w("ALBERT", "SCHOOLS: " + get(Constants.HTTP_SCHOOLS_URL));


                    SchoolsMsg.School[] schools = new SchoolsMsg.School[2];
                    schools[0] = new SchoolsMsg.School(0, "Stanford");
                    schools[1] = new SchoolsMsg.School(1, "Oxford");

                    Intent i = new Intent(Constants.SCHOOLS_ACTION);
                    i.putExtra(
                            Constants.SCHOOLS_DATA,
                            new SchoolsMsg(schools));
                    mBroadcastManager.sendBroadcast(i);
                    break;
                }
                default: {
                    Log.w(TAG, "Unknown message: " + intent);
                }
            }
        }
    }
}
