package org.mozilla.gecko.QJ;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import org.json.simple.JSONObject;
import org.mozilla.gecko.home.SearchEngine;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;

/**
 * Created by gayamac on 05/02/2018.
 */

public class BlacklistSingleton {

    public static final BlacklistSingleton sharedInstance = new BlacklistSingleton();
    private BDBlacklistHighLevelManager.BlacklistBDD blacklistManager = null;
    private SearchEngineContainer searchEngineContainer = null;
    private boolean firstIsSearchEngine = true;

    private BlacklistSingleton() {

        RestBlacklistManager.ping();

    }

    public void loadBlacklistDatabase(Context context) {
        if (blacklistManager == null) {
            blacklistManager = new BDBlacklistHighLevelManager.BlacklistBDD(context);
            blacklistManager.open();
        }
        if (blacklistManager == null)
            Log.d("QWANT JUNIOR MOBILE LOG", "could not create blacklist database");
    }

    public static boolean isQwantJuniorHost(String hostTesting) {
        return RestBlacklistManager.isQwantJuniorHost(hostTesting);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String hashPath(String path) {
        Log.d("QWANT JUNIOR MOBILE LOG", "[HASH PATH]");
        Log.d("QWANT JUNIOR MOBILE LOG", "path : " + path);
        String path2 = path.replaceFirst("https://", "").replaceFirst("http://", "").replaceFirst("www.", "");
        URL url = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String host = (url != null && url.getHost() != null && url.getHost() != "") ? url.getHost() : path2;
        Log.d("QWANT JUNIOR MOBILE LOG", "host : " + host);
        String[] sp = host.split("\\.");
        Log.d("QWANT JUNIOR MOBILE LOG", "sp : " + Arrays.toString(sp));
        ArrayList<String> arr = new ArrayList<>();
        for (String s : sp)
            arr.add(s);
        Collections.reverse(arr);
        String reversedHost = TextUtils.join(".", arr);
        Log.d("QWANT JUNIOR MOBILE LOG", "reversedHost : " + reversedHost);
        String newPath = path2.replaceFirst(host, reversedHost);
        Log.d("QWANT JUNIOR MOBILE LOG", "newPath : " + newPath);
        String md5Hex = md5(newPath);
        Log.d("QWANT JUNIOR MOBILE LOG", "md5Hex: " + md5Hex);
        return md5Hex;
    }

    private static String removeParameterInUrl(String url) {
        Log.d("QWANT JUNIOR MOBILE LOG", "[REMOVE PARAMETER IN URL]");
        Log.d("QWANT JUNIOR MOBILE LOG", "url : " + url);
        int rangeParameter = url.indexOf("?");
        if (rangeParameter != -1) {
            String strParameter = url.substring(0, rangeParameter);
            Log.d("QWANT JUNIOR MOBILE LOG", "url without parameter : " + strParameter);
            return strParameter;
        }
        Log.d("QWANT JUNIOR MOBILE LOG", "url without parameter : " + url);
        return url;
    }

    public boolean domainIsBlacklisted(final String domain) {
        final boolean[] ret = new boolean[1];
        ret[0] = false;
        long before = System.currentTimeMillis();
        blacklistManager.getDomainInBlacklist(domain, new BDBlacklistHighLevelManager.BlacklistBDD.GetListener() {
            @Override
            public void onResult(final boolean found, boolean available, final boolean expires) {
                if (found && !expires) {
                    ret[0] = !available;
                    Log.d("QWANT JUNIOR MOBILE LOG", "Local");
                } else {
                    String[] paths = new String[1];
                    paths[0] = hashPath(domain);
                    RestBlacklistManager.testPaths("/blacklist/domains/hash", paths, new RestBlacklistManager.Listener() {
                        @Override
                        public void onResponse(boolean[] res) {
                            ret[0] = res[0];
                            Log.d("QWANT JUNIOR MOBILE LOG", "Network");
                            if (!found)
                                blacklistManager.insertDomainInBlacklist(domain, !ret[0]);
                            if (expires)
                                blacklistManager.updateDomainInBlacklist(domain, !ret[0]);
                        }

                        @Override
                        public void onTimeout() {
                            ret[0] = true;
                        }

                        @Override
                        public void onError() {
                            ret[0] = true;
                        }
                    });
                }
            }
        });
        if (ret[0]) {
            Log.d("QWANT JUNIOR MOBILE LOG", "BLACKLIST !");
        } else {
            Log.d("QWANT JUNIOR MOBILE LOG", "OK GO !");
        }
        long after = System.currentTimeMillis();
        Log.d("QWANT JUNIOR MOBILE LOG", "elpased : " + (after - before));
        return ret[0];
    }

    public boolean domainIsRedirect(final String domain) {
        final boolean[] ret = new boolean[1];
        ret[0] = false;
        long before = System.currentTimeMillis();
        blacklistManager.getDomainInRedirect(domain, new BDBlacklistHighLevelManager.BlacklistBDD.GetListener() {
            @Override
            public void onResult(final boolean found, boolean available, final boolean expires) {
                if (found && !expires) {
                    ret[0] = !available;
                    Log.d("QWANT JUNIOR MOBILE LOG", "Local");
                } else {
                    String[] paths = new String[1];
                    paths[0] = hashPath(domain);
                    RestBlacklistManager.testPaths("/redirect/domains/hash", paths, new RestBlacklistManager.Listener() {
                        @Override
                        public void onResponse(boolean[] res) {
                            ret[0] = res[0];
                            Log.d("QWANT JUNIOR MOBILE LOG", "Network");
                            if (!found)
                                blacklistManager.insertDomainInRedirect(domain, !ret[0]);
                            if (expires)
                                blacklistManager.updateDomainInRedirect(domain, !ret[0]);
                        }

                        @Override
                        public void onTimeout() {
                            ret[0] = true;
                        }

                        @Override
                        public void onError() {
                            ret[0] = true;
                        }
                    });
                }
            }
        });
        if (ret[0]) {
            Log.d("QWANT JUNIOR MOBILE LOG", "BLACKLIST !");
        } else {
            Log.d("QWANT JUNIOR MOBILE LOG", "OK GO !");
        }
        long after = System.currentTimeMillis();
        Log.d("QWANT JUNIOR MOBILE LOG", "elpased : " + (after - before));
        return ret[0];
    }

    public boolean urlIsBlacklisted(final String url) {
        final boolean[] ret = new boolean[1];
        ret[0] = false;
        long before = System.currentTimeMillis();
        final String urlWithoutParameter = removeParameterInUrl(url);
        blacklistManager.getUrlInBlacklist(urlWithoutParameter, new BDBlacklistHighLevelManager.BlacklistBDD.GetListener() {
            @Override
            public void onResult(final boolean found, boolean available, final boolean expires) {
                if (found && !expires) {
                    ret[0] = !available;
                    Log.d("QWANT JUNIOR MOBILE LOG", "Local");
                } else {
                    String[] paths = new String[1];
                    paths[0] = hashPath(urlWithoutParameter);
                    RestBlacklistManager.testPaths("/blacklist/urls/hash", paths, new RestBlacklistManager.Listener() {
                        @Override
                        public void onResponse(boolean[] res) {
                            ret[0] = res[0];
                            Log.d("QWANT JUNIOR MOBILE LOG", "Network");
                            if (!found)
                                blacklistManager.insertUrlInBlacklist(urlWithoutParameter, !ret[0]);
                            if (expires)
                                blacklistManager.updateUrlInBlacklist(urlWithoutParameter, !ret[0]);
                        }

                        @Override
                        public void onTimeout() {
                            ret[0] = true;
                        }

                        @Override
                        public void onError() {
                            ret[0] = true;
                        }
                    });
                }
            }
        });
        if (ret[0]) {
            Log.d("QWANT JUNIOR MOBILE LOG", "BLACKLIST !");
        } else {
            Log.d("QWANT JUNIOR MOBILE LOG", "OK GO !");
        }
        long after = System.currentTimeMillis();
        Log.d("QWANT JUNIOR MOBILE LOG", "elpased : " + (after - before));
        return ret[0];
    }

    public boolean urlIsRedirect(final String url) {
        final boolean[] ret = new boolean[1];
        ret[0] = false;
        long before = System.currentTimeMillis();
        final String urlWithoutParameter = removeParameterInUrl(url);
        blacklistManager.getUrlInRedirect(urlWithoutParameter, new BDBlacklistHighLevelManager.BlacklistBDD.GetListener() {
            @Override
            public void onResult(final boolean found, boolean available, final boolean expires) {
                if (found && !expires) {
                    ret[0] = !available;
                    Log.d("QWANT JUNIOR MOBILE LOG", "Local");
                } else {
                    String[] paths = new String[1];
                    paths[0] = hashPath(urlWithoutParameter);
                    RestBlacklistManager.testPaths("/redirect/urls/hash", paths, new RestBlacklistManager.Listener() {
                        @Override
                        public void onResponse(boolean[] res) {
                            ret[0] = res[0];
                            Log.d("QWANT JUNIOR MOBILE LOG", "Network");
                            if (!found)
                                blacklistManager.insertUrlInRedirect(urlWithoutParameter, !ret[0]);
                            if (expires)
                                blacklistManager.updateUrlInRedirect(urlWithoutParameter, !ret[0]);
                        }

                        @Override
                        public void onTimeout() {
                            ret[0] = true;
                        }

                        @Override
                        public void onError() {
                            ret[0] = true;
                        }
                    });
                }
            }
        });
        if (ret[0]) {
            Log.d("QWANT JUNIOR MOBILE LOG", "BLACKLIST !");
        } else {
            Log.d("QWANT JUNIOR MOBILE LOG", "OK GO !");
        }
        long after = System.currentTimeMillis();
        Log.d("QWANT JUNIOR MOBILE LOG", "elpased : " + (after - before));
        return ret[0];
    }

    public boolean isIp(String hostTesting) {
        Matcher matcher = Patterns.IP_ADDRESS.matcher(hostTesting);
        if (matcher.matches())
            return true;
        return false;
    }

    public void loadSearchEnginesFile(Context context) {
        if (searchEngineContainer == null)
            searchEngineContainer = new SearchEngineContainer(context);
    }


    public boolean isFirstSearchEngine(String hostTesting) {

        if (!firstIsSearchEngine) {
            return false;
        }

        if (searchEngineContainer.findSearchEngineName(hostTesting) != null) {
            firstIsSearchEngine = false;
            return true;
        }
        return false;
    }

    public boolean isSearchEngine(String hostTesting) {

        return searchEngineContainer.findSearchEngineName(hostTesting) != null;
    }

    public String findSearchEngineName(String hostTesting) {

        return searchEngineContainer.findSearchEngineName(hostTesting);
    }

    public boolean searchEngineHasSearch(String searchEngineName, String url) {

        SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (url.contains(searchEngine.search)) {
                return true;
            }
        }
        return false;
    }

    public boolean searchEngineHasSafeSearchUrlAvailable(String searchEngineName, String url) {

        SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (searchEngine.safeSearchUrl != null && url.contains(searchEngine.search)) {
                return true;
            }
        }
        return false;
    }

    public boolean searchEngineHasSafeSearchUrl(String searchEngineName, String url) {

        SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (searchEngine.safeSearchUrl != null && url.contains(searchEngine.search)) {
                return url.contains(searchEngine.safeSearchUrl);
            }
        }
        return false;
    }

    public String convertSearchEngineSafeSearchUrl(String searchEngineName, String url) {

        SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (searchEngine.safeSearchUrl != null && url.contains(searchEngine.search)) {
                if (url.contains("?")) {
                    return url + "&" + searchEngine.safeSearchUrl;
                } else {
                    return url + "?" + searchEngine.safeSearchUrl;
                }

            }
        }
        return url;
    }

    public boolean searchEngineHasSafeSearchRequestAvailable(String searchEngineName) {

        SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (searchEngine.safeSearchRequestType != null && searchEngine.safeSearchRequestUrl != null) {
                return true;
            }
        }
        return false;
    }

    public boolean searchEngineHasSafeSearchRequest(String url) {

        return url.contains("safe=done");
    }

    public String convertSearchEngineSafeSearchRequest(String url) {

        if (url.contains("?")) {
            return url + "&" + "safe=done";
        } else {
            return url + "?" + "safe=done";
        }
    }


    public void runSearchEngineSafeSearchRequest(String searchEngineName) {

        final SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (searchEngine.safeSearchRequestType != null && searchEngine.safeSearchRequestUrl != null) {
                if (searchEngine.safeSearchRequestType.equals("GET")) {
                    RestRequester.makeHTTPGetRequest(searchEngine.safeSearchRequestUrl, false, new RestRequester.Listener() {
                        @Override
                        public void onResponse(Exception e, String str) {
                            Log.d("QWANT JUNIOR MOBILE LOG", "search engine request GET" + searchEngine.safeSearchRequestUrl + ", " + searchEngine.safeSearchRequestBody);
                        }

                        @Override
                        public void onResponseJSON(Exception e, JSONObject obj) {

                        }

                        @Override
                        public void onTimeout() {

                        }
                    });
                } else if (searchEngine.safeSearchRequestType.equals("POST")) {
                    RestRequester.makeHTTPPostRequest(searchEngine.safeSearchRequestUrl, searchEngine.safeSearchRequestBody, false, new RestRequester.Listener() {
                        @Override
                        public void onResponse(Exception e, String str) {
                            if (e != null)
                                e.printStackTrace();
                            Log.d("QWANT JUNIOR MOBILE LOG", "search engine request POST " + searchEngine.safeSearchRequestUrl + ", " + searchEngine.safeSearchRequestBody);
                        }

                        @Override
                        public void onResponseJSON(Exception e, JSONObject obj) {

                        }


                        @Override
                        public void onTimeout() {

                        }
                    });
                }
            }
        }
    }

    public boolean searchEngineHasValidState(String searchEngineName) {

        SearchEngineContainer.SearchEngine searchEngine = searchEngineContainer.getSearchEngine(searchEngineName);
        if (searchEngine != null) {
            if (searchEngine.state.equals("valid")) {
                return true;
            }
        }
        return false;
    }
}
