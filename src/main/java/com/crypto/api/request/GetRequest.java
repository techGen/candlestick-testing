package com.crypto.api.request;

import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.PrintStream;
import java.io.StringWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;


public class GetRequest {

    private static final Logger logger = LogManager.getLogger(GetRequest.class);

    private final String URI;
    private Headers headers;
    private Response response;
    private Map<String, Object> requestParams;

    public GetRequest(String URI) {
        this.URI = URI;
    }

    public void setHeader(List<Header> headerList) {

        try {
            headers = new Headers(headerList);
        } catch (Exception e) {
            Header header = new Header("Content-Type", "application/json");
            headers = new Headers(header);
        }
    }

    public void setRequestUrlParameters(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    private Headers getHeaders() {
        return this.headers;
    }

    public Map<String, Object> getRequestParams() {
        return this.requestParams;
    }

    private void setResponse(Response response) {
        logger.info("Response:\n" + response.asString());
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public void createConnection() {

        try {
            logger.info("URI="+URI);
            logger.info("Headers="+getHeaders().toString());
            StringWriter writer = new StringWriter();
            @SuppressWarnings("deprecation")
            PrintStream captor = new PrintStream(new WriterOutputStream(writer), true);
            RestAssured.baseURI = URI;
            RequestSpecification httpRequest;
            int reqParamSize = 0;
            try {
                reqParamSize = getRequestParams().size();
            } catch (Exception e) {
            }
            if (reqParamSize > 0) {

                httpRequest = RestAssured.given().urlEncodingEnabled(false).headers(getHeaders()).queryParams(getRequestParams())
                        .config(RestAssured.config().logConfig(new LogConfig(captor, true))).log().all();

            } else {

                httpRequest = RestAssured.given().headers(getHeaders())
                        .config(RestAssured.config().logConfig(new LogConfig(captor, true))).log().all();
            }

            logger.info("Request Time :" + Instant.now(Clock.systemDefaultZone()));
            Response response = httpRequest.relaxedHTTPSValidation().request(Method.GET);

            logger.info("Request Body:\n" + writer.toString());
            setResponse(response);

        } catch (Exception ex) {
            logger.error("Error occurred while creating GET request");
            logger.error(ex.getMessage());
        }
    }

}
