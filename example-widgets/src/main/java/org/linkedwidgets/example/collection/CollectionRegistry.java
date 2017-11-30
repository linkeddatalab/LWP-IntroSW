package org.linkedwidgets.example.collection;

import static org.linkedwidgets.example.widget.util.Util.sendPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.linkedwidgets.example.widget.util.Util;
import org.lw.shared.datamodel.MashupInfo;
import org.lw.shared.datamodel.Resource;
import org.lw.shared.datamodel.UserInfo;
import org.lw.shared.datamodel.WidgetCollectionInfo;
import org.lw.shared.datamodel.WidgetInfo;
import org.lw.shared.util.OntologyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class CollectionRegistry {

    private static final Logger log = LoggerFactory.getLogger(CollectionRegistry.class);

    private Properties properties;

    public static Map<String, WidgetInfo> widgets = new HashMap<>();
    public static Map<String, WidgetCollectionInfo> widgetCollections = new HashMap<>();
    public static Map<String, MashupInfo> mashups = new HashMap<>();

    public CollectionRegistry() {

        properties = Util.getProperties();

        UserInfo userInfo = new UserInfo("fajar.juang@gmail.com");

        List<WidgetInfo> exampleWidgetList = new ArrayList<>();
        exampleWidgetList.add(widgets.get("turtle_loader_1"));
        exampleWidgetList.add(widgets.get("turtle_loader_2"));
        exampleWidgetList.add(widgets.get("rdf_merger"));
        exampleWidgetList.add(widgets.get("JSONViewer"));
        addWidgetCollection("ExampleWidgetsCollection", "Example Widgets Collection",
                "An example collection of simple RDF/Turtle widgets", userInfo, exampleWidgetList);

        List<WidgetInfo> mapWidgetList = new ArrayList<>();
        mapWidgetList.add(widgets.get("MapPointer"));
        mapWidgetList.add(widgets.get("LeafletMap"));
        mapWidgetList.add(widgets.get("point_limit"));
        mapWidgetList.add(widgets.get("wien_public_transport_stops"));
        addWidgetCollection("MapWidgetsCollection", "Map Widgets Collection",
                "An example collection of map widgets", userInfo, mapWidgetList);

        addMashup("MapMashup", "Example Map Mashup", "An example mashup that shows 10 of public transport stops",
                "{\"mode\":null, \"savedOptions\":[{\"id\":\"0\", \"options\":[null]}], \"widgetCollection\":\"WidgetCollection5b2b33606618848768a3ce3e36ca2efc_1511873527779\", \"widgets\":[{\"uri\":\"http://linkedwidgets.org/ontology/resource/MapPointsLimiter\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/server/MapPointLimit/index.html\", \"name\":\"Map Points Limiter\", \"x\":282, \"y\":275, \"width\":0, \"height\":0, \"minimize\":false},{\"uri\":\"http://linkedwidgets.org/ontology/resource/WienPublicTransportStops\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/server/loader/rdf-loader-3.html\", \"name\":\"Wien Public Transport Stops\", \"x\":56, \"y\":85, \"width\":0, \"height\":0, \"minimize\":false},{\"uri\":\"http://linkedwidgets.org/ontology/resource/Leaflet Map\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/client/LeafletMapWidget/index.html\", \"name\":\"Leaflet Map\", \"x\":656, \"y\":101, \"width\":0, \"height\":0, \"minimize\":false}], \"wires\":[{\"src\":{\"terminal\":\"output\", \"widgetId\":0}, \"tgt\":{\"terminal\":\"loc\", \"widgetId\":2}},{\"src\":{\"terminal\":\"output\", \"widgetId\":1}, \"tgt\":{\"terminal\":\"input\", \"widgetId\":0}}]}",
                new Resource("http://linkedwidgets.org/ontology/resource/CategoryBasic", "Basic"), userInfo,
                widgetCollections.get("MapWidgetsCollection").getWidgetCollection());

        addMashup("ExampleMashup", "Example RDF Mashup",
                "An example mashup that merge two turtle files into one RDF data",
                "{\"mode\":null, \"savedOptions\":[], \"widgetCollection\":\"WidgetCollection5b2b33606618848768a3ce3e36ca2efc_1511873576903\", \"widgets\":[{\"uri\":\"http://linkedwidgets.org/ontology/resource/TurtleLoader\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/server/loader/rdf-loader-2.html\", \"name\":\"Turtle Loader\", \"x\":83, \"y\":116, \"width\":0, \"height\":0, \"minimize\":false},{\"uri\":\"http://linkedwidgets.org/ontology/resource/TurtleLoaderwithParam\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/server/loader/rdf-loader-1.html\", \"name\":\"Turtle Loader with Param\", \"x\":76, \"y\":272, \"width\":0, \"height\":0, \"minimize\":false},{\"uri\":\"http://linkedwidgets.org/ontology/resource/RDFMerge\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/server/merger/rdf-merger.html\", \"name\":\"RDF Merge\", \"x\":361, \"y\":204, \"width\":0, \"height\":0, \"minimize\":false},{\"uri\":\"http://linkedwidgets.org/ontology/resource/JSON Viewer\", \"id\":null, \"url\":\"http://localhost:8080/example-widgets/html/client/JsonViewerWidget/index.html\", \"name\":\"JSON Viewer\", \"x\":644, \"y\":24, \"width\":536, \"height\":621, \"minimize\":false}], \"wires\":[{\"src\":{\"terminal\":\"output\", \"widgetId\":0}, \"tgt\":{\"terminal\":\"input\", \"widgetId\":2}},{\"src\":{\"terminal\":\"output\", \"widgetId\":1}, \"tgt\":{\"terminal\":\"input\", \"widgetId\":2}},{\"src\":{\"terminal\":\"output\", \"widgetId\":2}, \"tgt\":{\"terminal\":\"input\", \"widgetId\":3}}]}",
                new Resource("http://linkedwidgets.org/ontology/resource/CategoryBasic", "Basic"), userInfo,
                widgetCollections.get("MapWidgetsCollection").getWidgetCollection());
    }

    private void addWidgetCollection(String collectionURI, String collectionName, String description,
        UserInfo userInfo, List<WidgetInfo> widgetInfoList) {

        WidgetCollectionInfo wci = new WidgetCollectionInfo();
        Resource wciResource = new Resource(OntologyConstant.RESOURCE + collectionURI, collectionName);
        wci.setWidgetCollection(wciResource);
        wci.setDescription(description);
        wci.setUser(userInfo);
        wci.setWidgets(widgetInfoList);

        Gson gson = new Gson();

        String serverAddCollectionURL = properties.getProperty("saveWidgetCollectionURL");
        sendPost(serverAddCollectionURL, gson.toJson(wci));
        log.info(collectionName + "|" + collectionURI);
        widgetCollections.put(collectionURI, wci);
    }

    private void addMashup(String mashupURI, String mashupName, String description, String mashupConfig,
        Resource category,
        UserInfo userInfo, Resource widgetCollection) {

        MashupInfo mashupInfo = new MashupInfo();
        Resource mashupInfoResource = new Resource(OntologyConstant.RESOURCE + mashupURI, mashupName);
        mashupInfo.setMashup(mashupInfoResource);
        mashupInfo.setDescription(description);
        mashupInfo.setMashupConfig(mashupConfig);
        mashupInfo.getCategories().add(category);
        mashupInfo.setUser(userInfo);
        mashupInfo.setWidgetCollection(widgetCollection);
        WidgetCollectionInfo wci = widgetCollections.get(OntologyConstant.getLocalName(widgetCollection.getUri()));
        wci.getWidgets().forEach(widget -> {
            mashupInfo.getWidgets().add(widget.getWidget());
        });

        Gson gson = new Gson();

        String serverAddCollectionURL = properties.getProperty("saveMashupURL");
        sendPost(serverAddCollectionURL, gson.toJson(mashupInfo));
        log.info(mashupName + "|" + mashupURI);
        mashups.put(mashupName, mashupInfo);
    }
}
