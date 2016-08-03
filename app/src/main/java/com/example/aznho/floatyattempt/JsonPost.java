package com.example.aznho.floatyattempt;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aznho on 8/2/2016.
 */
public class JsonPost {

    public static void makeSlackPost(String pokemonName, LatLng latLng){
        RequestParams params = new RequestParams("payload", "{\"text\":\"" +pokemonName+" at  https://maps.google.com/maps?q=loc:"+Double.toString(latLng.latitude)+","+Double.toString(latLng.longitude)+"\"}");

        SlackWebhookRestClient.post("",params,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(MainActivity.TAG, "On Success: "+Integer.toString(statusCode));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Log.e(MainActivity.TAG, "On failure: "+Integer.toString(statusCode) + " response: " + res);
            }
        });
    }//makeSlackPost
}
