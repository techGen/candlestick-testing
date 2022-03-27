package com.crypto.candlestick.test;

import com.crypto.api.GetCandlestick;
import com.crypto.api.GetTrades;
import com.crypto.enums.Environment;
import com.crypto.utilities.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CandlestickAPITest {

    private static final Logger logger = LogManager.getLogger(CandlestickAPITest.class);
    private SoftAssertions softAssertions;

    @BeforeAll
    public static void BeforeAll() {
        logger.info("Starting test execution");
    }

    @BeforeEach
    public void BeforeEach(){
        softAssertions = new SoftAssertions();
    }

    @Test
    public void oneCandleStickTest_1m() {

        //calling candlestick api with 1m time frame
        GetCandlestick candleStickAPI = new GetCandlestick(Environment.PROD, "BTC_USDT", "1m");
        Assertions.assertEquals(200, candleStickAPI.getStatusCode());

        //Storing o,c,h,l price info in map of most latest single candle only
        Map<String, BigDecimal> candleStickPrice = candleStickAPI.getCandleStickInfo(candleStickAPI.getCandleStick(1).get(0));

        //Storing ending and starting time in map of most latest single candle only
        Map<String, Date> candleStickTime = candleStickAPI.getCandleStickTime(candleStickAPI.getCandleStick(1).get(0),
                TimeUnit.MINUTES, 1);

        //Calling trades api
        GetTrades tradesAPI = new GetTrades(Environment.PROD, "BTC_USDT");
        Assertions.assertEquals(200, tradesAPI.getStatusCode());

        //Getting all trades within same time period of above candle stick and finding o,c,h,l  price
        Map<String, BigDecimal> tradesPrice = tradesAPI.getTradesInfo(candleStickTime.get(Constants.CandleStick.START_TIME),
                candleStickTime.get(Constants.CandleStick.END_TIME));

        //o,c,h,l price validation between candle and trade data
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.HIGHEST_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.HIGHEST_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.LOWEST_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.LOWEST_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.OPENING_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.OPENING_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.CLOSING_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.CLOSING_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Quantity.VOLUME)).isEqualTo(tradesPrice.get(Constants.Quantity.VOLUME));
    }

    @Test
    public void twoCandleStickTest_5m() {

        GetCandlestick candleStickAPI = new GetCandlestick(Environment.PROD, "BTC_USDT", "5m");
        Assertions.assertEquals(200, candleStickAPI.getStatusCode());

        Map<String, BigDecimal> candleStickPrice_1 = candleStickAPI.getCandleStickInfo(candleStickAPI.getCandleStick(2).get(0));
        Map<String, BigDecimal> candleStickPrice_2 = candleStickAPI.getCandleStickInfo(candleStickAPI.getCandleStick(2).get(1));

        Map<String, Date> candleStickTime_1 = candleStickAPI.getCandleStickTime(candleStickAPI.getCandleStick(2).get(0),
                TimeUnit.MINUTES, 5);
        Map<String, Date> candleStickTime_2 = candleStickAPI.getCandleStickTime(candleStickAPI.getCandleStick(2).get(1),
                TimeUnit.MINUTES, 5);

        GetTrades tradesAPI = new GetTrades(Environment.PROD, "BTC_USDT");
        Assertions.assertEquals(200, tradesAPI.getStatusCode());

        Map<String, BigDecimal> tradesPrice_1 = tradesAPI.getTradesInfo(candleStickTime_1.get(Constants.CandleStick.START_TIME),
                candleStickTime_1.get(Constants.CandleStick.END_TIME));
        Map<String, BigDecimal> tradesPrice_2 = tradesAPI.getTradesInfo(candleStickTime_2.get(Constants.CandleStick.START_TIME),
                candleStickTime_2.get(Constants.CandleStick.END_TIME));

        softAssertions.assertThat(candleStickPrice_1.get(Constants.Price.HIGHEST_PRICE)).isEqualTo(tradesPrice_1.get(Constants.Price.HIGHEST_PRICE));
        softAssertions.assertThat(candleStickPrice_1.get(Constants.Price.LOWEST_PRICE)).isEqualTo(tradesPrice_1.get(Constants.Price.LOWEST_PRICE));
        softAssertions.assertThat(candleStickPrice_1.get(Constants.Price.OPENING_PRICE)).isEqualTo(tradesPrice_1.get(Constants.Price.OPENING_PRICE));
        softAssertions.assertThat(candleStickPrice_1.get(Constants.Price.CLOSING_PRICE)).isEqualTo(tradesPrice_1.get(Constants.Price.CLOSING_PRICE));
        softAssertions.assertThat(candleStickPrice_1.get(Constants.Quantity.VOLUME)).isEqualTo(tradesPrice_1.get(Constants.Quantity.VOLUME));

        softAssertions.assertThat(candleStickPrice_2.get(Constants.Price.HIGHEST_PRICE)).isEqualTo(tradesPrice_2.get(Constants.Price.HIGHEST_PRICE));
        softAssertions.assertThat(candleStickPrice_2.get(Constants.Price.LOWEST_PRICE)).isEqualTo(tradesPrice_2.get(Constants.Price.LOWEST_PRICE));
        softAssertions.assertThat(candleStickPrice_2.get(Constants.Price.OPENING_PRICE)).isEqualTo(tradesPrice_2.get(Constants.Price.OPENING_PRICE));
        softAssertions.assertThat(candleStickPrice_2.get(Constants.Price.CLOSING_PRICE)).isEqualTo(tradesPrice_2.get(Constants.Price.CLOSING_PRICE));
        softAssertions.assertThat(candleStickPrice_2.get(Constants.Quantity.VOLUME)).isEqualTo(tradesPrice_2.get(Constants.Quantity.VOLUME));
    }

    @Test
    public void oneCandleStickTest_1h_ETHBTC() {

        GetCandlestick candleStickAPI = new GetCandlestick(Environment.PROD, "ETH_BTC", "1h");
        Assertions.assertEquals(200, candleStickAPI.getStatusCode());

        Map<String, BigDecimal> candleStickPrice = candleStickAPI.getCandleStickInfo(candleStickAPI.getCandleStick(1).get(0));

        Map<String, Date> candleStickTime = candleStickAPI.getCandleStickTime(candleStickAPI.getCandleStick(1).get(0),
                TimeUnit.HOURS, 1);

        GetTrades tradesAPI = new GetTrades(Environment.PROD, "ETH_BTC");
        Assertions.assertEquals(200, tradesAPI.getStatusCode());

        Map<String, BigDecimal> tradesPrice = tradesAPI.getTradesInfo(candleStickTime.get(Constants.CandleStick.START_TIME),
                candleStickTime.get(Constants.CandleStick.END_TIME));

        softAssertions.assertThat(candleStickPrice.get(Constants.Price.HIGHEST_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.HIGHEST_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.LOWEST_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.LOWEST_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.OPENING_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.OPENING_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Price.CLOSING_PRICE)).isEqualTo(tradesPrice.get(Constants.Price.CLOSING_PRICE));
        softAssertions.assertThat(candleStickPrice.get(Constants.Quantity.VOLUME)).isEqualTo(tradesPrice.get(Constants.Quantity.VOLUME));
    }

    @AfterAll
    public static void AfterAll() {
        logger.info("Finished test execution");
    }

    @AfterEach
    public void AfterEach(){
        softAssertions.assertAll();
    }
}
