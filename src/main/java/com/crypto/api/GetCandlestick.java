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
import java.util.concurrent.TimeUnit;

public class GetCandlestick {

    private static final Logger logger = LogManager.getLogger(GetCandlestick.class);

    private final Environment environment;
    private final String instrumentName;
    private final String timeFrame;
    private Response response;

    public GetCandlestick(Environment environment, String instrumentName, String timeFrame) {
        this.environment = environment;
        this.instrumentName = instrumentName;
        this.timeFrame = timeFrame;
        callGetCandleStickAPI();
    }

    private void callGetCandleStickAPI() {
        GetRequest getCandleStickRequest = new GetRequest(ConfigFileReader.getInstance().getPropertyValue(environment.getEnvironment())
                + ConfigFileReader.getInstance().getPropertyValue(Constants.API.CANDLESTICK_API));
        getCandleStickRequest.setHeader(null);
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("instrument_name", instrumentName);
        reqParams.put("timeframe", timeFrame);
        getCandleStickRequest.setRequestUrlParameters(reqParams);
        getCandleStickRequest.createConnection();
        response = getCandleStickRequest.getResponse();
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public int getCandleStickCount() {

        int candleStickCount = 0;
        try {
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<JSONObject> candleStickList = jsonPathEvaluator.getList("result.data");
            candleStickCount = candleStickList.size();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }
        return candleStickCount;
    }

    public List<JSONObject> getCandleStick(int requiredCandleCount) {

        int count = 0;
        List<JSONObject> requiredCandleStickList = new ArrayList<>();

        if (getCandleStickCount() > 0) {
            JsonPath jsonPathEvaluator = response.jsonPath();
            List<LinkedHashMap> TotalCandleStickList = jsonPathEvaluator.getList("result.data");

            for (int i = TotalCandleStickList.size() - 1; i >= 0; i--) {
                LinkedHashMap candleStick = TotalCandleStickList.get(i);
                requiredCandleStickList.add(new JSONObject(candleStick));
                count++;
                if (requiredCandleCount == count) {
                    break;
                }
            }
        }
        return requiredCandleStickList;
    }

    public Map<String, BigDecimal> getCandleStickInfo(JSONObject candleStick) {

        BigDecimal highestPrice;
        BigDecimal lowestPrice;
        BigDecimal openingPrice;
        BigDecimal closingPrice;
        BigDecimal volume;
        Map<String, BigDecimal> candleInfoMap = new HashMap<>();

        String s_openingPrice = candleStick.get("o").toString();
        openingPrice = new BigDecimal(s_openingPrice).setScale(8,RoundingMode.FLOOR);
        String s_closingPrice = candleStick.get("c").toString();
        closingPrice = new BigDecimal(s_closingPrice).setScale(8,RoundingMode.FLOOR);
        String s_highestPrice = candleStick.get("h").toString();
        highestPrice = new BigDecimal(s_highestPrice).setScale(8,RoundingMode.FLOOR);
        String s_lowestPrice = candleStick.get("l").toString();
        lowestPrice = new BigDecimal(s_lowestPrice).setScale(8,RoundingMode.FLOOR);
        String s_volume = candleStick.get("v").toString();
        volume = new BigDecimal(s_volume).setScale(8,RoundingMode.FLOOR);

        candleInfoMap.put(Constants.Price.OPENING_PRICE, openingPrice);
        candleInfoMap.put(Constants.Price.CLOSING_PRICE, closingPrice);
        candleInfoMap.put(Constants.Price.HIGHEST_PRICE, highestPrice);
        candleInfoMap.put(Constants.Price.LOWEST_PRICE, lowestPrice);
        candleInfoMap.put(Constants.Quantity.VOLUME, volume);

        return candleInfoMap;
    }

    public Map<String,Date> getCandleStickTime(JSONObject candleStick, TimeUnit timeUnit,int timeValue){

        Map<String,Date> candleStickTimeMap = new HashMap<>();
        long endTime = Long.parseLong(candleStick.get("t").toString());
        Date candleStickEndTime = new Date(endTime);
        Date candleStickStartTime = new Date(candleStickEndTime.getTime() - timeUnit.toMillis(timeValue));
        candleStickTimeMap.put(Constants.CandleStick.START_TIME,candleStickStartTime);
        candleStickTimeMap.put(Constants.CandleStick.END_TIME,candleStickEndTime);
        return candleStickTimeMap;
    }

}
