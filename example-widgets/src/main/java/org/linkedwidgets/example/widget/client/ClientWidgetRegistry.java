package org.linkedwidgets.example.widget.client;

import java.util.Properties;

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
public class ClientWidgetRegistry {

    private static final Logger log = LoggerFactory.getLogger(ClientWidgetRegistry.class);
    private Properties properties;

    public ClientWidgetRegistry() {

        log.info("Context initialized");

        this.properties = Util.getProperties();

        initializeWidget("JSON Viewer", "visualization", "JsonViewerWidget/index.html",
                "A basic JSON viewer for any kind of JSON file", false);
        initializeWidget("Map Pointer", "data", "MapPointerWidget/index.html",
                "Map Pointer Widget for data inputs", false);
        initializeWidget("Leaflet Map", "visualization", "LeafletMapWidget/index.html",
                "Simple Leaflet Map Visualization", false);
        initializeWidget("OwnYourData Widget", "data", "OwnYourDataWidget/index.html",
                "Example OwnYourData Widget implementation", false);

        log.info("Context initialized done");
    }

    /**
     * A method to register your client widget to LWP.
     *
     * @param name the name of your widget
     * @param widgetType "data", "process", or "visualization"
     * @param htmlURL the url of your widget html file.
     * @param description the description of your widget
     * @param isExternal if true, then the html file is not the example-widgets project. You should add the full url address
     *        in the parameter htmlUrl.
     */
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
        log.info("add widget: " + widgetId);

        log.info(name + " widget initialization finished");
    }
}
