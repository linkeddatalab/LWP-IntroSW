package org.linkedwidgets.example.widget.server.rdf.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.lw.serverwidget.core.ServerWidgetJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that contain the function to load and pass a turtle file as a data widget.
 */
public class TurtleLoader extends ServerWidgetJob {

    private static final Logger log = LoggerFactory.getLogger(TurtleLoader.class);

    private File jsonLDFile; // holding the temporary file location

    public TurtleLoader(String filename) {
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

        // variable output will be returned when returnOutput() is called
        output = new JSONObject();

        try {
            // start reading file
            InputStream is = new FileInputStream(jsonLDFile);
            String jsonTxt = IOUtils.toString(is, "UTF-8");

            // finished reading file
            output = new JSONObject(jsonTxt);

            // write JSON file as an output
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
