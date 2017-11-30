package org.linkedwidgets.example.widget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);
    private static final String config = "config.properties";
    private static Properties properties;

    public static String sendPost(String url, String json) {

        log.info("Start sendPost");
        try {
            log.info("Start httpClient");

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);

            httpPost.addHeader("content-type", "application/json");
            httpPost.setEntity(new StringEntity(json));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);
                log.info("End httpClient");
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("End sendPost");

        return null;
    }

    public static boolean sendPing(String url) {

        log.info("Start sendPing");
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(
                    RequestConfig.custom().setConnectTimeout(3000).setConnectionRequestTimeout(3000).build());
            httpGet.addHeader("content-type", "text/plain");
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                String content = EntityUtils.toString(respEntity);
                if (content.equals("pong"))
                    return true;
            }
        } catch (ConnectTimeoutException e) {
            log.error("server is not ready" + e.getMessage());
        } catch (IOException e) {
            log.error("server is not ready" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("End sendPing");
        return false;
    }

    public static Properties getProperties() {

        if (properties == null) {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream configStream = classloader.getResourceAsStream(config);
            properties = new Properties();
            try {
                properties.load(configStream);
            } catch (IOException e) {
                log.error("Error loading properties");
                e.printStackTrace();
            }
        }

        return properties;
    }

}
