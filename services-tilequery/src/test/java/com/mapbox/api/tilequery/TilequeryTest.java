package com.mapbox.api.tilequery;

import com.mapbox.core.TestUtils;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TilequeryTest  extends TestUtils {

    private static final String TILEQUERY_FIXTURE = "tilequery.json";

    private MockWebServer server;
    private HttpUrl mockUrl;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
      server = new MockWebServer();

      server.setDispatcher(new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
          try {
            String response = loadJsonFixture(TILEQUERY_FIXTURE);
            return new MockResponse().setBody(response);
          } catch (IOException ioException) {
            throw new RuntimeException(ioException);
          }
        }
      });
      server.start();
      mockUrl = server.url("");
    }

    @After
    public void tearDown() throws IOException {
      server.shutdown();
    }

    /**
     * Test the most basic request (default response format)
     */
    @Test
    public void testCallSanity() throws ServicesException, IOException {
      MapboxTilequery client = MapboxTilequery.builder()
        .accessToken(ACCESS_TOKEN)
        .query(Point.fromLngLat(-77.04341,38.9096))
        .mapIds("mapbox.mapbox-streets-v7")
        .baseUrl(mockUrl.toString())
        .build();
      Response<FeatureCollection> response = client.executeCall();
      assertEquals(200, response.code());

      // Check the response body
      assertNotNull(response.body());
    }

  }
