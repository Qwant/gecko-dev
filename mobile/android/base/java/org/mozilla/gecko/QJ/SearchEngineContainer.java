package org.mozilla.gecko.QJ;

import android.content.Context;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gayamac on 06/02/2018.
 */

class SearchEngineContainer {

     class SearchEngine {
        public String name;
        public String domain;
        public Pattern domainRegex;
        public String search;
        public String safeSearchUrl;
        public String safeSearchRequestType;
        public String safeSearchRequestUrl;
        public String safeSearchRequestBody;
        public String state;

        public SearchEngine(String name, String domain, String search, String safeSearchUrl, String safeSearchRequestType, String safeSearchRequestUrl, String safeSearchRequestBody, String state) {
            this.name = name;
            this.domain = domain;
            domainRegex = Pattern.compile(domain);
            this.search = search;
            this.safeSearchUrl = safeSearchUrl;
            this.safeSearchRequestType = safeSearchRequestType;
            this.safeSearchRequestUrl = safeSearchRequestUrl;
            this.safeSearchRequestBody = safeSearchRequestBody;
            this.state = state;
        }
    }

    private Map<String, SearchEngine> searchEngines = new HashMap<>();

    public SearchEngineContainer(Context context) {
        try {
            InputStream is = context.getAssets().open("search_engines.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String str = new String(buffer, "UTF-8");
            Log.d("QWANT JUNIOR MOBILE LOG", "search engine json : " + str);
            JSONArray arr = (JSONArray) new JSONParser().parse(str);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);
                String name = (String) obj.get("name");
                String domain = (String) obj.get("domain");
                String search = (String) obj.get("search");
                String safeSearchUrl = (String) obj.get("safe_search_url");
                String safeSearchRequestType = (String) obj.get("safe_search_request_type");
                String safeSearchRequestUrl = (String) obj.get("safe_search_request_url");
                String safeSearchRequestBody = (String) obj.get("safe_search_request_body");
                String state = (String) obj.get("state");
                SearchEngine searchEngine = new SearchEngine(name, domain, search, safeSearchUrl, safeSearchRequestType, safeSearchRequestUrl, safeSearchRequestBody, state);
                searchEngines.put(searchEngine.name, searchEngine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String findSearchEngineName(String domain) {
        for (Map.Entry<String, SearchEngine> elem : searchEngines.entrySet()) {
            Matcher m = elem.getValue().domainRegex.matcher(domain);
            if (m.find()) {
                MatchResult mr = m.toMatchResult();
                String result = mr.group();
                if (result == domain) {
                    return elem.getKey();
                }
            }
        }
        return null;
    }

    public SearchEngine getSearchEngine(String name) {
        return searchEngines.get(name);
    }
}
