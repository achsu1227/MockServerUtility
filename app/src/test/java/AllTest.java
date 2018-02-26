import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.MockUtil;
import util.QueryMapUtils;

/**
 * Created by ac on 2017/11/16.
 */

public class AllTest {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Map mQueryMap = new HashMap();

    @Before
    public void setUp() {
        mQueryMap.put("test1", "888");
        mQueryMap.put("test2", "777");
    }

    @Test
    public void test_get_api() {
        MockUtil.requestWithFile("mock_server_error.json", "test.php" , new MockUtil.OnApiTest(){
            @Override
            public void doApiTest(String domain) {
                // avoid creating several instances, should be singleon
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(domain + "/test.php?" + QueryMapUtils.urlEncodeMap2String(mQueryMap))
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new MockUtil.OnApiParams() {
            @Override
            public void onQueryParams(HashMap queryParams, HashMap formParams) {
                //return queryParams and formParams
                Assert.assertTrue(Maps.difference(mQueryMap, queryParams).areEqual());
            }
        });
    }

    @Test
    public void test_post_api() {
        MockUtil.requestWithFile("mock_server_error.json", "test.php" , new MockUtil.OnApiTest(){
            @Override
            public void doApiTest(String domain) {

                //BaseProxy.API_SERVER = domain;

                // avoid creating several instances, should be singleon
                OkHttpClient client = new OkHttpClient();

                String json = "{'test1':'888', 'test2':'777'}";

                RequestBody body = RequestBody.create(JSON, json);

                Request request = new Request.Builder()
                        .url(domain + "/test.php")
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new MockUtil.OnApiParams() {
            @Override
            public void onQueryParams(HashMap queryParams, HashMap formParams) {
                //return queryParams and formParams
                Assert.assertTrue(Maps.difference(mQueryMap, formParams).areEqual());
            }
        });
    }
}
