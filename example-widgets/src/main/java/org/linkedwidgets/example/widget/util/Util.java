package org.linkedwidgets.example.widget.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

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

    public static Properties loadProperties(String propertyFileName) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream configStream = classloader.getResourceAsStream(propertyFileName);
        Properties properties = new Properties();
        try {
            properties.load(configStream);
        } catch (IOException e) {
            log.error("Error loading properties");
            e.printStackTrace();
        }
        return properties;
    }

}
