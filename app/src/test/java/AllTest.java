import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mockserverlibrary.util.MockUtil;
import com.mockserverlibrary.util.QueryMapUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ac on 2017/11/16.
 */

public class AllTest {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final MediaType COMMOM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

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
                domain = "http://" + domain;

                // avoid creating several instances, should be singleon
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(domain + "/test.php?" + QueryMapUtils.queryToQueryString(QueryMapUtils.urlEncodeMap(mQueryMap)))
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    System.out.print(response.body().string());
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
                domain = "http://" + domain;
                // avoid creating several instances, should be singleon
                OkHttpClient client = new OkHttpClient();

                //String json = "{'test1':'888', 'test2':'777'}";

                String bodyConent = QueryMapUtils.queryToQueryString(QueryMapUtils.urlEncodeMap(mQueryMap));

                RequestBody body = RequestBody.create(COMMOM, bodyConent);

                Request request = new Request.Builder()
                        .url(domain + "/test.php")
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    System.out.print(response.body().string());
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
