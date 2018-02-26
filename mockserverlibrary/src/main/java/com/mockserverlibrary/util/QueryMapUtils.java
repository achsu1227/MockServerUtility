package com.mockserverlibrary.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ac on 2018/1/9.
 */

public class QueryMapUtils {

    /*public static Map<String, String> trim(Map<String, String> mQuery) {
        Map<String, String> map = new HashMap<>();
        map.putAll(mQuery);
        map.values().remove(null);
        map.keySet().remove(null);
        return map;
    }*/

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] nameAndValue = param.split("=");
            String name = nameAndValue[0];
            String value = "";
            if (nameAndValue.length > 1) {
                value = nameAndValue[1];
            }
            map.put(name, value);
        }
        return map;
    }

    public static Map<String, String> trim(Map<String, String> mQuery) {
        Map<String, String> map = new HashMap<>();
        //map.putAll(mQuery);
        map.keySet().remove(null);
        for(String key : mQuery.keySet()) {
            if(mQuery.get(key) == null) {
                map.put(key, mQuery.get(key) + "");
            } else {
                map.put(key, mQuery.get(key) + "");
            }
        }
        return map;
    }

    public static Map<String, String> mapDecoder(Map<String, String> mQuery) {
        Map queryMap = new HashMap<String, String>();

        if (mQuery != null && mQuery.size() > 0) {
            Set<String> keys = mQuery.keySet();
            for (String key : keys) {
                if(mQuery.get(key) != null && UrlEncodeUtils.isURLEncoded(mQuery.get(key))){
                    queryMap.put(key, UrlEncodeUtils.decodeURL(mQuery.get(key)));
                } else {
                    queryMap.put(key, mQuery.get(key));
                }
            }
        }

        return queryMap;
    }

    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        if(pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        }
        return query_pairs;
    }

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        if(pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        }
        return query_pairs;
    }

    /**
     *
     * @param query
     * @return
     */
    public static Map urlEncodeMap(Map<String, String> query) {
        Map queryMap = new HashMap<String, String>();

        if (query != null && query.size() > 0) {
            Set<String> keys = query.keySet();
            for (String key : keys) {
                if (query.get(key) != null && !UrlEncodeUtils.isURLEncoded(query.get(key))) {
                    queryMap.put(key, UrlEncodeUtils.encodeURL(query.get(key)));
                } else {
                    queryMap.put(key, query.get(key));
                }
            }
        }

        return queryMap;
    }

    public static String queryToQueryString(Map<String, String> query) {
        StringBuffer queryStr = new StringBuffer();
        if (query != null && query.size() > 0) {
            Set<String> keys = query.keySet();
            for (String key : keys) {
                queryStr.append(key).append("=").append(query.get(key))
                        .append("&");
            }
            queryStr.deleteCharAt(queryStr.length() - 1);
        }
        return queryStr.toString();
    }
}
