package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.e104.exception.E104RemoteException;
import com.e104.helper.QueryMapUtils;
import com.e104.helper.UrlEncodeUtils;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;

/**
 * Created by ac on 2017/10/26.
 */

public class MockUtil {

    private static MockWebServer mockWebServer;
    private static boolean isStart;

    /**
     * 讀取檔案 to MockResponse.
     *
     * @param fileName 檔案名稱
     * @return MockResponse
     */
    public static MockResponse readFile(String fileName) {
        InputStream jsonStream = MockUtil.class.getClassLoader().getResourceAsStream(fileName);
        //byte[] jsonBytes = IOUtils.toByteArray(jsonStream);
        try {
            return new MockResponse().setResponseCode(200).setBody(new Buffer().readFrom(jsonStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new MockResponse().setResponseCode(404);
    }

    /**
     * 讀取檔案 to String.
     *
     * @param fileName 檔案名稱
     * @return String
     */
    public static String readFile2String(String fileName) {
        InputStream jsonStream = MockUtil.class.getClassLoader().getResourceAsStream(fileName);
        //byte[] jsonBytes = IOUtils.toByteArray(jsonStream);

        try {
            return IOUtils.toString(jsonStream, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 讀取檔案 to InputStream.
     *
     * @param fileName 檔案名稱
     * @return InputStream
     */
    public static InputStream readFileInputStream(String fileName) {
        InputStream inputStream = MockUtil.class.getClassLoader().getResourceAsStream(fileName);
        return inputStream;
    }

    public static void requestWithDispatcher(Dispatcher dispatcher, String mockPath, OnApiTest mOnApiTest) {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);   //dispatcher见下文

        try {
            mockWebServer.start();
            HttpUrl url = mockWebServer.url(mockPath);
            mOnApiTest.doApiTest(url.host() + ":" + url.port());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        } finally {
            try {
                mockWebServer.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void releaseMockServer() {
        if (mockWebServer != null) {
            try {
                mockWebServer.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isStart = false;
        }
    }

    public static void requestWithFile(String mockFile, String mockPath, Map query, OnApiTest mOnApiTest) {

        try {
            if (mockWebServer == null) {
                mockWebServer = new MockWebServer();
                //MockWebServer mockWebServer = new MockWebServer();
            }
            mockWebServer.setDispatcher(getDispatcher(mockFile, mockPath, query, null));

            if (!isStart) {
                mockWebServer.start();
                isStart = true;
            }
            HttpUrl url = mockWebServer.url(mockPath);
            mOnApiTest.doApiTest(url.host() + ":" + url.port());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertFalse(true);
            //releaseMockServer();
        } /*finally {
            try {
                mockWebServer.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static void requestWithFile(String mockFile, String mockPath, Map query, OnApiTest mOnApiTest, OnApiParams mOnApiParams) {

        try {
            if (mockWebServer == null) {
                mockWebServer = new MockWebServer();
                //MockWebServer mockWebServer = new MockWebServer();
            }
            mockWebServer.setDispatcher(getDispatcher(mockFile, mockPath, query, mOnApiParams));

            if (!isStart) {
                mockWebServer.start();
                isStart = true;
            }
            HttpUrl url = mockWebServer.url(mockPath);
            mOnApiTest.doApiTest(url.host() + ":" + url.port());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        } catch (Exception e) {
            Assert.assertFalse(true);
            //releaseMockServer();
        } /*finally {
            try {
                mockWebServer.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    private static Dispatcher getDispatcher(final String mockFile, final String mockPath, final Map query, final OnApiParams mOnApiParams) {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                checkParameterEqual(request, query, mOnApiParams);

                if (request.getPath().contains(mockPath)) {
                    return MockUtil.readFile(mockFile);
                } else {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };
        return dispatcher;
    }

    private static HashMap<String, String> checkParameterEqual(RecordedRequest request, Map<String, String> query, final OnApiParams mOnApiParams) {
        HashMap<String, String> resultMap = new HashMap<String, String>();

        HashMap<String, String> resultQueryMap = getQueryParams(request);
        HashMap<String, String> resultFormMap = getFormParams(request);

        if (mOnApiParams != null) {
            mOnApiParams.onQueryParams(resultQueryMap, resultFormMap);
        }

        resultMap.putAll(resultQueryMap);
        resultMap.putAll(resultFormMap);

        //compareParams(query, resultMap);

        return resultMap;
    }

    private static HashMap getQueryParams(RecordedRequest request) {
        HashMap<String, String> resultMap = new HashMap<String, String>();

        String getParam = request.getRequestUrl().encodedQuery();
        System.out.println(getParam);

        if (getParam != null) {
            Map getMap = QueryMapUtils.getQueryMap(getParam);
            if (getMap != null) {
                resultMap.putAll(getMap);
            }
        }

        return resultMap;
    }

    private static HashMap getFormParams(RecordedRequest request) {
        HashMap<String, String> resultMap = new HashMap<String, String>();

        String postForm = request.getBody().readUtf8();
        System.out.println(postForm);

        if (postForm != null) {
            Map postMap = QueryMapUtils.getQueryMap(postForm);
            if (postMap != null) {
                resultMap.putAll(postMap);
            }
        }

        return resultMap;
    }

    private static void compareParams(Map<String, String> rawParams, Map<String, String> resultParams) {
        for (String key : rawParams.keySet()) {
            String resultValue = "";
            if (resultParams.containsKey(key)) {
                resultValue = UrlEncodeUtils.isURLEncoded(resultParams.get(key))
                        ? UrlEncodeUtils.decodeURL(resultParams.get(key)) : resultParams.get(key);
            }

            boolean result = rawParams.get(key).equals(resultValue);
            System.out.println(" " + key + " query = " + rawParams.get(key) + "  resultMap : " + resultValue);
            Assert.assertTrue(result);
        }
    }

    public interface OnApiTest {
        void doApiTest(String domain) throws E104RemoteException;
    }

    public interface OnApiParams {
        void onQueryParams(HashMap queryParams, HashMap formParams);
    }
}
