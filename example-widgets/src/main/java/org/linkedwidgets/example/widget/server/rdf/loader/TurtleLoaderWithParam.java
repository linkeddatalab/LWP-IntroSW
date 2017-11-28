package org.linkedwidgets.example.widget.server.rdf.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONException;
import org.json.JSONObject;
import org.lw.serverwidget.core.ServerWidgetJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that contain the function to load and pass a turtle file as a data widget.
 */
public class TurtleLoaderWithParam extends ServerWidgetJob {

    private static final Logger log = LoggerFactory.getLogger(TurtleLoaderWithParam.class);

    private File jsonLDFile; // holding the temporary file location

    public TurtleLoaderWithParam(String filename) {
        super();

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(filename);

        try {
            // read turtle file with Jena
            Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
            RDFDataMgr.read(model, is, Lang.TURTLE);

            // store temporarily in the server
            jsonLDFile = new File(System.getProperty("java.io.tmpdir") + filename + "_temp.json");
            System.out.println(jsonLDFile.getAbsolutePath());
            RDFDataMgr.write(new FileOutputStream(jsonLDFile), model, Lang.JSONLD);
            model.close();
            is.close();

        } catch (FileNotFoundException e) {
            log.error("File not found: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO Exception: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        log.info("start running the widget");

        try {
            log.info("Print param1 value: " + params.getString("param1"));
        } catch (JSONException e) {
            log.error("JSONException: " + e.getMessage());
        }

        // variable output will be returned when returnOutput() is called
        output = new JSONObject();
        String param = params.get("param1").toString();
        String[] prefix = param.split("\\|"); // assuming that the parameter is a complete prefix

        try {
            // start reading file
            InputStream is = new FileInputStream(jsonLDFile);
            Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
            RDFDataMgr.read(model, is, Lang.JSONLD);
            log.info("prefix[0]: "+prefix[0]);
            log.info("prefix[1]: "+prefix[1]);

            model.setNsPrefix(prefix[0], prefix[1]);

            StringWriter writer = new StringWriter();
            RDFDataMgr.write(writer, model, Lang.JSONLD);
            String jsonTxt = writer.toString();

            // finished reading file
            output = new JSONObject(jsonTxt);

            // write JSON file as an output
            writer.close();
            model.close();
            is.close();

        } catch (FileNotFoundException e) {
            log.error("file not found" + e.getMessage());
        } catch (IOException e) {
            log.error("IOException" + e.getMessage());
        }

        returnOutput();
    }

    @Override
    public void destroy() {
        jsonLDFile.deleteOnExit();
        log.info("stopping the widget");
    }
}
