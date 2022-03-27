package com.crypto.api;

import com.crypto.api.request.GetRequest;
import com.crypto.enums.Environment;
import com.crypto.utilities.ConfigFileReader;
import com.crypto.utilities.Constants;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class GetTrades {

    private static final Logger logger = LogManager.getLogger(GetTrades.class);

    private final Environment environment;
    private final String instrumentName;
    private Response response;

    public GetTrades(Environment environment, String instrumentName) {
        this.environment = environment;
        this.instrumentName = instrumentName;
        callGetTradesAPI();
    }

    private void callGetTradesAPI() {

        GetRequest getTradeRequest = new GetRequest(ConfigFileReader.getInstance().getPropertyValue(environment.getEnvironment())
                + ConfigFileReader.getInstance().getPropertyValue(Constants.API.TRADES_API));
        getTradeRequest.setHeader(null);
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("instrument_name", instrumentName.trim());
        getTradeRequest.setRequestUrlParameters(reqParams);
        getTradeRequest.createConnection();
        response = getTradeRequest.getResponse();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public Map<String, BigDecimal> getTradesInfo(Date startTime, Date endTime) {

        Map<String, BigDecimal> tradesInfoMap = new HashMap<>();
        List<JSONObject> tradesList = getTradesBetweenTwoTime(startTime, endTime);

        if (tradesList.size() > 1) {
            return getTradesInfoMap(tradesList);
        } else if (tradesList.size() == 1) {
            BigDecimal tradePrice = BigDecimal.valueOf(Double.parseDouble(tradesList.get(0).get("p").toString()));
            String quantityString = tradesList.get(0).get("q").toString();
            BigDecimal volume = new BigDecimal(quantityString).setScale(8, RoundingMode.FLOOR);

            tradesInfoMap.put(Constants.Price.OPENING_PRICE, tradePrice);
            tradesInfoMap.put(Constants.Price.CLOSING_PRICE, tradePrice);
            tradesInfoMap.put(Constants.Price.HIGHEST_PRICE, tradePrice);
            tradesInfoMap.put(Constants.Price.LOWEST_PRICE, tradePrice);
            tradesInfoMap.put(Constants.Quantity.VOLUME, volume);
        } else {
            logger.info("No trades are available");
        }
        return tradesInfoMap;
    }

    private int getTradesCount() {

        int tradeCounts = 0;
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<JSONObject> tradesList = jsonPathEvaluator.getList("result.data");
            tradeCounts = tradesList.size();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        return tradeCounts;
    }

    private List<JSONObject> getTradesBetweenTwoTime(Date startTime, Date endTime) {

        List<JSONObject> trades = new ArrayList<>();

        if (getTradesCount() > 0) {
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<LinkedHashMap> tradesList = jsonPathEvaluator.getList("result.data");

            for (LinkedHashMap trade : tradesList) {
                long tradeEpochTime = (long) trade.get("t");
                Date tradeTime = new Date(tradeEpochTime);

                if ((tradeTime.compareTo(startTime) > 0 || tradeTime.compareTo(startTime) == 0) &&
                        (tradeTime.compareTo(endTime) < 0 || tradeTime.compareTo(endTime) == 0)) {
                    trades.add(new JSONObject(trade));
                }
            }
        }
        return trades;
    }

    private Map<String, BigDecimal> getTradesInfoMap(List<JSONObject> tradeList) {

        BigDecimal highestPrice;
        BigDecimal lowestPrice;
        BigDecimal openingPrice = null;
        BigDecimal closingPrice = null;
        BigDecimal volume = null;

        Map<String, BigDecimal> tradeInfoMap = new HashMap<>();
        List<BigDecimal> priceList = new ArrayList<>();
        List<BigDecimal> quantityList = new ArrayList<>();

        for (int i = 0; i < tradeList.size(); i++) {
            JSONObject trade = tradeList.get(i);
            String s_price = trade.get("p").toString();
            BigDecimal tradePrice = new BigDecimal(s_price).setScale(8, RoundingMode.FLOOR);
            String s_quantity = trade.get("q").toString();
            BigDecimal tradeQuantity = new BigDecimal(s_quantity).setScale(8, RoundingMode.FLOOR);
            priceList.add(tradePrice);
            quantityList.add(tradeQuantity);
            if (i == 0) {
                closingPrice = tradePrice;
            } else if (i == tradeList.size() - 1) {
                openingPrice = tradePrice;
            }
        }
        for(BigDecimal quantity: quantityList){
            volume = volume.add(quantity);
        }

        highestPrice = priceList.stream().max(Comparator.naturalOrder()).orElse(null);
        lowestPrice = priceList.stream().min(Comparator.naturalOrder()).orElse(null);

        tradeInfoMap.put(Constants.Price.OPENING_PRICE, openingPrice);
        tradeInfoMap.put(Constants.Price.CLOSING_PRICE, closingPrice);
        tradeInfoMap.put(Constants.Price.HIGHEST_PRICE, highestPrice);
        tradeInfoMap.put(Constants.Price.LOWEST_PRICE, lowestPrice);
        tradeInfoMap.put(Constants.Quantity.VOLUME, volume);

        return tradeInfoMap;
    }

}