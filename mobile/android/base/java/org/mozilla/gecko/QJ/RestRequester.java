package org.mozilla.gecko.QJ;

import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by gayamac on 05/02/2018.
 */

class RestRequester {

    interface Listener {
        void onResponse(Exception e, String str);
        void onResponseJSON(Exception e, JSONObject obj);
        void onTimeout();
    }

    public static void makeHTTPGetRequest(final String path, boolean isJsonResponse, Listener listener) {

        final ArrayList<Byte> responseData = new ArrayList<>();
        final PointerE ptrE = new PointerE();
        final ConditionVariable cond = new ConditionVariable();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL(path);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream is = urlConnection.getInputStream();

                    int data = is.read();
                    while (data != -1) {
                        responseData.add((byte) data);
                        data = is.read();
                    }

                    is.close();

                } catch (MalformedURLException | FileNotFoundException e) {
                    ptrE.e = e;
                } catch (IOException e) {
                    ptrE.e = e;
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                    cond.open();
                }


            }
        }).start();

        if (!cond.block(60000)) {
            listener.onTimeout();
            return;
        }

        if (ptrE.e != null) {
            if (isJsonResponse)
                listener.onResponseJSON(ptrE.e, null);
            else
                listener.onResponse(ptrE.e, null);
            return;
        }

        byte[] arr = new byte[responseData.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = responseData.get(i);
        }

        String str = null;
        try {
            str = new String(arr, "UTF-8");
            if (isJsonResponse) {
                JSONObject obj = (JSONObject) new JSONParser().parse(str);
                listener.onResponseJSON(null, obj);
            } else {
                listener.onResponse(null, str);
            }

        } catch (UnsupportedEncodingException e) {
            ptrE.e = e;
        } catch (ParseException e) {
            ptrE.e = e;
        }
        if (ptrE.e != null) {
            if (isJsonResponse)
                listener.onResponseJSON(ptrE.e, null);
            else
                listener.onResponse(ptrE.e, null);
            return;
        }

    }

    public static void makeHTTPPostRequest(final String path, final String postString, boolean isJsonResponse, Listener listener) {

        final ArrayList<Byte> responseData = new ArrayList<>();
        final PointerE ptrE = new PointerE();
        final ConditionVariable cond = new ConditionVariable();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL(path);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setDoOutput(true);
                    OutputStream os = urlConnection.getOutputStream();
                    os.write(postString.getBytes());
                    os.flush();
                    os.close();
                    InputStream is = urlConnection.getInputStream();

                    int data = is.read();
                    while (data != -1) {
                        responseData.add((byte) data);
                        data = is.read();
                    }

                    is.close();

                } catch (MalformedURLException e) {
                    ptrE.e = e;
                } catch (IOException e) {
                    ptrE.e = e;
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                    cond.open();
                }


            }
        }).start();

        if (!cond.block(60000)) {
            listener.onTimeout();
            return;
        }

        if (ptrE.e != null) {
            if (isJsonResponse)
                listener.onResponseJSON(ptrE.e, null);
            else
                listener.onResponse(ptrE.e, null);
            return;
        }

        byte[] arr = new byte[responseData.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = responseData.get(i);
        }

        String str = null;
        try {
            str = new String(arr, "UTF-8");
            if (isJsonResponse) {
                JSONObject obj = (JSONObject) new JSONParser().parse(str);
                listener.onResponseJSON(null, obj);
            } else {
                listener.onResponse(null, str);
            }

        } catch (UnsupportedEncodingException e) {
            ptrE.e = e;
        } catch (ParseException e) {
            ptrE.e = e;
        }
        if (ptrE.e != null) {
            if (isJsonResponse)
                listener.onResponseJSON(ptrE.e, null);
            else
                listener.onResponse(ptrE.e, null);
            return;
        }

    }

    private static class PointerE {
        public Exception e;
    }
}
