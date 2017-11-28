package org.linkedwidgets.example.widget.server;

import static org.linkedwidgets.example.widget.util.Util.loadProperties;
import static org.linkedwidgets.example.widget.util.Util.sendPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.linkedwidgets.example.widget.server.rdf.loader.TurtleLoader;
import org.linkedwidgets.example.widget.server.rdf.loader.TurtleLoaderWithParam;
import org.linkedwidgets.example.widget.server.rdf.merger.RDFMerger;
import org.lw.serverwidget.core.ServerWidget;
import org.lw.serverwidget.core.ServerWidgetJob;
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
public class ServerWidgetRegistry implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(ServerWidgetRegistry.class);
    private Properties properties;
    private List<ServerWidget> serverWidgets;
    private final String file1 = "data_example_1.ttl";
    private final String file2 = "data_example_2.ttl";
    private final String halteFile = "Wiener-Linien-Haltestelle.ttl";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Context initialized");

        this.serverWidgets = new ArrayList<>();
        this.properties = loadProperties("config.properties");

        initializeWidget(new TurtleLoaderWithParam(file2), "Turtle Loader with Param", "data",
                "loader/rdf-loader-1.html", "Loading RDF data from file: " + file2 + " with parameter prefix",
                "turtle_loader_1");
        initializeWidget(new TurtleLoader(file1), "Turtle Loader", "data",
                "loader/rdf-loader-2.html", "Loading RDF data from file: " + file1 + " without parameter",
                "turtle_loader_2");
        initializeWidget(new TurtleLoader(halteFile), "Wien Public Transport Stops", "data",
                "loader/rdf-loader-3.html", "A list of bus/tram/u-bahn stops in Wien",
                "wien_public_transport_stops");
        initializeWidget(new RDFMerger(), "RDF Merge", "process",
                "merger/rdf-merger.html", "Merging two RDF files",
                "rdf_merger");

        log.info("Context initialized done");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        for (ServerWidget serverWidget : serverWidgets) {
            serverWidget.destroy();
        }
    }

    private void initializeWidget(ServerWidgetJob widgetJob, String widgetName, String widgetType,
        String htmlURL, String description, String widgetId) {

        log.info(widgetName + " widget initialization started");

        String wsURL = properties.getProperty("wsURL", "ws://localhost:28980/lwrouter");
        String wsRealm = properties.getProperty("wsRealm", "realm1");
        String widgetURL =
            properties.getProperty("widgetURL", "http://localhost:8080/example-widgets/html/") + "server/";
        String serverAddWidgetURL =
            properties.getProperty("serverAddWidgetURL", "http://localhost:8080/lw-server/mashup/addWidget");

        serverWidgets.add(new ServerWidget(wsURL, wsRealm, widgetId) {
            @Override
            protected ServerWidgetJob createAJob() {
                return widgetJob;
            }
        });

        Resource resource = new Resource();
        resource.setName(widgetName);
        resource.setUri(OntologyConstant.RESOURCE + widgetName.replaceAll("[^A-Za-z0-9]", ""));

        WidgetInfo widgetInfo = new WidgetInfo();
        widgetInfo.setWidget(resource);
        widgetInfo.setWidgetType(widgetType);
        widgetInfo.setUrl(widgetURL + htmlURL);
        widgetInfo.setDescription(description);

        Gson gson = new Gson();

        log.info("sendPost");
        sendPost(serverAddWidgetURL, gson.toJson(widgetInfo));

        log.info(widgetName + " widget initialization finished");
    }

}
