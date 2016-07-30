package com.example.aznho.floatyattempt;

/**
 * Created by aznho on 7/25/2016.
 */

import android.util.Log;

import com.loopj.android.http.*;

public class SlackWebhookRestClient {
    private static final String BASE_URL = "https://hooks.slack.com/services/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";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Log.e("SlackWebhookRest", params.toString());
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
