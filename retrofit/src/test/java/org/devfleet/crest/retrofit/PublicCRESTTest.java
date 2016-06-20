package org.devfleet.crest.retrofit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.devfleet.crest.CrestService;
import org.devfleet.crest.model.CrestDictionary;
import org.devfleet.crest.model.CrestMarketBulkOrder;
import org.devfleet.crest.model.CrestMarketHistory;
import org.devfleet.crest.model.CrestMarketOrder;
import org.devfleet.crest.model.CrestSolarSystem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class PublicCRESTTest {

    private static final Logger LOG = LoggerFactory.getLogger(PublicCRESTTest.class);

    private static CrestService service;


    @BeforeClass
    public static void setupCREST() throws Exception {
        service = CrestClient.TQ().build().fromDefault();
    }

    @Test
    @Ignore
    public void testLocations() {
        final List<CrestSolarSystem> locations = service.getLocations();
        Assert.assertFalse(locations.isEmpty());
        Assert.assertNotNull(service.getSolarSystem(locations.get(0).getId()));
    }

    @Test
    @Ignore
    public void testMarketHistory() {
        final List<CrestMarketHistory> h = service.getMarketHistory(10000033, 23713);
        Assert.assertFalse(h.isEmpty());
    }

    @Test
    @Ignore
    public void testGetJita150mmRailIIMarketPrices ( ) {
        final List<CrestMarketOrder> o = service.getMarketOrders(10000002, "sell", 3074);
        LOG.info("Retrieved " + o.size() + " items");
        Assert.assertFalse(o.isEmpty());
    }
    
    @Test
    @Ignore
    public void testGetAllMarketOrders ( ) {
        final List<CrestMarketBulkOrder> bo = service.getAllOrders(10000002);
        Assert.assertFalse(bo.isEmpty());
    }

    @Test
    @Ignore
    public void testGetAllOrdersAsync ( ) throws InterruptedException {
        final List<CrestMarketBulkOrder> bo = Collections.synchronizedList(new ArrayList<CrestMarketBulkOrder>());
        Callback<CrestDictionary<CrestMarketBulkOrder>> cb = new Callback<CrestDictionary<CrestMarketBulkOrder>>() {
            @Override
            public void onResponse(Call<CrestDictionary<CrestMarketBulkOrder>> call,
                    Response<CrestDictionary<CrestMarketBulkOrder>> response)
            {
                CrestDictionary<CrestMarketBulkOrder> body = response.body();
                bo.addAll(body.getItems());
            }

            @Override
            public void onFailure(Call<CrestDictionary<CrestMarketBulkOrder>> call, Throwable t) {
                LOG.error("onFailure called");
            }
        };

        service.getAllOrdersAsync(10000002, cb);

        //Give some time for the executor to call the callbacks as many times as it needs (this shouldn't take 30 seconds
        //unless you're on a crappy connection
        Thread.sleep(30000);
        Assert.assertFalse(bo.isEmpty());
    }
}
