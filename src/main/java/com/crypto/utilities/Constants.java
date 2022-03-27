package com.crypto.utilities;

public class Constants {

    public static final String CONFIG_FILE = "config.properties";

    public static final class API {

        public static final String TRADES_API = "getTradesAPI";
        public static final String CANDLESTICK_API = "getCandlestickAPI";
    }

    public static final class Price {

        public static final String OPENING_PRICE = "openingPrice";
        public static final String CLOSING_PRICE = "closingPrice";
        public static final String HIGHEST_PRICE = "highestPrice";
        public static final String LOWEST_PRICE = "lowestPrice";
    }

    public static final class Quantity {

        public static final String VOLUME = "volume";
    }

    public static final class CandleStick {

        public static final String START_TIME = "startTime";
        public static final String END_TIME = "endTime";
    }

}
