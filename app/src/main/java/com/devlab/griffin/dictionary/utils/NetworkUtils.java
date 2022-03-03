package com.devlab.griffin.dictionary.utils;

import android.net.Uri;
import android.util.Log;

import com.devlab.griffin.dictionary.BuildConfig;
import com.devlab.griffin.dictionary.constants.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final OkHttpClient client = new OkHttpClient();

    public static URL buildFreeDictionaryUrl(String word) {
        Uri uri = Uri.parse(Constants.FREE_DICTIONARY_ENGLISH_BASE_URL).buildUpon().appendPath(word).build();

        URL url = null;
        try{
            url = new URL(uri.toString());
        }
        catch(MalformedURLException e) {
            Log.e(TAG, "buildMeaningUrl: malformed uri", e.fillInStackTrace());
        }

        return url;
    }

    public static URL buildBigHugeThesaurusUrl(String word) {
        Uri uri = Uri.parse(Constants.BIG_HUGE_THESAURUS_BASE_URL).buildUpon().appendPath(BuildConfig.BH_THESAURUS_KEY).appendPath(word).appendPath("json").build();

        URL url = null;
        try{
            url = new URL(uri.toString());
        }
        catch(MalformedURLException e) {
            Log.e(TAG, "buildBigHugeThesaurusUrl: malformed uri", e.fillInStackTrace());
        }

        return url;
    }

    public static URL buildUrbanDictionaryUrl(String word) {
        Uri uri = Uri.parse(Constants.URBAN_DICTIONARY_BASE_URL).buildUpon().appendQueryParameter(Constants.URBAN_DICTIONARY_QUERY_PARAM_TERM, word).build();

        URL url = null;
        try{
            url = new URL(uri.toString());
        }
        catch(MalformedURLException e) {
            Log.e(TAG, "buildUrbanDictionaryUrl: malformed uri", e.fillInStackTrace());
        }

        return url;
    }

    public static String getFreeDictionaryResponse(URL url) {

        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "getFreeDictionaryResponse: Unsuccessful response" + response);
                return null;
            }

            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "getFreeDictionaryResponse: IOException", e.fillInStackTrace());
        } finally {
            if(response != null)
                response.close();
        }

        return null;
    }

    public static String getBigThesaurusResponse(URL url) {

        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "getBigThesaurusResponse: Unsuccessful response" + response);
                return null;
            }

            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "getBigThesaurusResponse: IOException", e.fillInStackTrace());
        } finally {
            if(response != null)
                response.close();
        }

        return null;
    }

    public static String getUrbanDictionaryResponse(URL url) {

        Request request = new Request.Builder().url(url).
                addHeader(Constants.X_RAPID_API_HOST_HEADER_KEY, BuildConfig.X_RAPID_API_HOST).
                addHeader(Constants.X_RAPID_API_KEY_HEADER_KEY, BuildConfig.X_RAPID_API_KEY).
                build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.e(TAG, "getUrbanDictionaryResponse: Unsuccessful response" + response);
                return null;
            }

            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "getUrbanDictionaryResponse: IOException", e.fillInStackTrace());
        } finally {
            if(response != null)
                response.close();
        }

        return null;
    }
}
