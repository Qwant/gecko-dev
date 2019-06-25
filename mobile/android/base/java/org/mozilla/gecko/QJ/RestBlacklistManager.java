package org.mozilla.gecko.QJ;

import android.util.Log;

import org.json.simple.JSONObject;

/**
 * Created by gayamac on 05/02/2018.
 */

class RestBlacklistManager {

    interface Listener {
        void onResponse(boolean[] res);
        void onError();
        void onTimeout();
    }

    private static String host = "mobile-secure.qwantjunior.com";
    private static String baseURL = "https://" + host + "/api/qwant-junior-mobile";
    //qwant-junior-mobile-server2.eu-gb.mybluemix.net
    //mobile-secure.qwantjunior.com


    public static String getQwantJuniorHost() {
        return host;
    }

    public static void testPaths(String route, final String[] paths, final Listener listener) {
        String postString = "";
        final boolean[] ret = new boolean[paths.length];
        int i = 0;
        while (i < paths.length) {
            if (i > 0) {
                postString += "&";
            }
            postString += "test" + (i + 1) + "=" + paths[i];
            ret[i] = true;
            Log.d("QWANT JUNIOR MOBILE LOG", (i + 1) + " -> " + paths[i]);
            i++;
        }

        RestRequester.makeHTTPPostRequest(baseURL + route, postString, true, new RestRequester.Listener() {
            @Override
            public void onResponse(Exception e, String str) {

            }

            @Override
            public void onResponseJSON(Exception e, JSONObject obj) {
                if (e != null) {
                    e.printStackTrace();
                    listener.onError();
                    return;
                }
                int i = 0;
                while (i < paths.length) {

                    Object test = obj.get("test" + (i + 1));
                    if (test == null) {
                        ret[i] = false;
                    }
                    try {
                        boolean b = (boolean) test;
                        ret[i] = b;
                    } catch (ClassCastException e2) {
                        e.printStackTrace();
                        ret[i] = false;
                    }
                    i += 1;
                }
                listener.onResponse(ret);
            }

            @Override
            public void onTimeout() {
                listener.onTimeout();
            }

        });
    }

    public static void ping() {

        RestRequester.makeHTTPPostRequest(baseURL + "/ping", "", true, new RestRequester.Listener() {
            @Override
            public void onResponse(Exception e, String str) {

            }

            @Override
            public void onResponseJSON(Exception e, JSONObject obj) {
                if (e != null) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTimeout() {

            }

        });
    }

}
