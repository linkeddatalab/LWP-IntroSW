package org.linkedwidgets.example.widget;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.linkedwidgets.example.collection.CollectionRegistry;
import org.linkedwidgets.example.widget.client.ClientWidgetRegistry;
import org.linkedwidgets.example.widget.server.ServerWidgetRegistry;
import org.linkedwidgets.example.widget.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WidgetRegistry implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(WidgetRegistry.class);

    private ScheduledExecutorService scheduler;
    private Properties properties;
    private String url;

    ClientWidgetRegistry clientWidgetRegistry;
    ServerWidgetRegistry serverWidgetRegistry;
    CollectionRegistry collectionRegistry;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        properties = Util.getProperties();
        url = properties.getProperty("pingURL");

        scheduler = Executors.newSingleThreadScheduledExecutor(); // create a thread scheduler to check LW-server
        scheduler.scheduleAtFixedRate(() -> {
            if (Util.sendPing(url)) { // check if LW-server is ready to receive message, if not wait 5 seconds..
                log.info("LW server initiated!");

                clientWidgetRegistry = new ClientWidgetRegistry(); // initiate a set of example client widgets
                serverWidgetRegistry = new ServerWidgetRegistry(); // initiate a set of example server widgets
                collectionRegistry = new CollectionRegistry(); // initiate widget collections and mashups

                throw new RuntimeException(); // stop task
            } else {
                log.info("LW server not yet initiated ...");
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        scheduler.shutdownNow();
    }
}
