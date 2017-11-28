package org.linkedwidgets.example.widget.client;

import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.linkedwidgets.example.collection.CollectionRegistry;
import org.linkedwidgets.example.widget.util.Util;
import org.lw.shared.datamodel.Resource;
import org.lw.shared.datamodel.WidgetInfo;
import org.lw.shared.util.OntologyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * This class facilitate registration of all internal server widgets from LW-Server.
 * 
 * @author Ekaputra
 */
public class ClientWidgetRegistry implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(ClientWidgetRegistry.class);
    private Properties properties;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        log.info("Context initialized");

        this.properties = Util.loadProperties("config.properties");

        initializeWidget("JSON Viewer", "visualization", "JsonViewerWidget/index.html",
                "A basic JSON viewer for any kind of JSON file", false);
        initializeWidget("Map Pointer", "data", "MapPointerWidget/index.html",
                "Map Pointer Widget for data inputs", false);
        initializeWidget("Leaflet Map", "visualization", "LeafletMapWidget/index.html",
                "Simple Leaflet Map Visualization", false);

        // initializeWidget("Google Map Visualization (Carousel)", "visualization", "GoogleMapWidget/index.html",
        // "Google Maps input visualization", false);
        // initializeWidget("Flickr Geo Search", "process", "FlickrGeoSearch/index.html",
        // "Add Flickr images from area nearby appointed locations. To be used with Google Map visualization widget",
        // false);
        // initializeWidget("Geo Merge", "data", "GeoMergeWidget/index.html", "", false);

        log.info("Context initialized done");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void initializeWidget(String name, String widgetType,
        String htmlURL, String description, Boolean isExternal) {

        log.info(name + " widget initialization started");
        String url = properties.getProperty("widgetURL", "http://localhost:8080/example-widgets/html/") + "client/";
        String widgetId = name.replaceAll("[^a-zA-Z0-9]", "");

        Resource resource = new Resource();
        resource.setName(name);
        resource.setUri(OntologyConstant.RESOURCE + widgetId);

        WidgetInfo widgetInfo = new WidgetInfo();
        widgetInfo.setWidget(resource);
        widgetInfo.setWidgetType(widgetType);
        widgetInfo.setUrl(isExternal ? htmlURL : url + htmlURL);
        widgetInfo.setDescription(description);

        Gson gson = new Gson();

        log.info("sendPost");

        Util.sendPost(properties.getProperty("serverAddWidgetURL"), gson.toJson(widgetInfo));
        CollectionRegistry.widgets.put(widgetId, widgetInfo);
        log.info("add widget: "+widgetId);

        log.info(name + " widget initialization finished");
    }
}
