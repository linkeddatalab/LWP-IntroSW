package org.linkedwidgets.example.widget.server.rdf.merger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONObject;
import org.lw.serverwidget.core.ServerWidgetJob;

public class RDFMerger extends ServerWidgetJob {

    File jsonLDFile;

    @Override
    public void run() {

        if (inputs.length == 0) {
            output = new JSONObject();

        } else {
            Model model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);

            for (JSONObject value : inputs) {
                try {
                    InputStream is = new ByteArrayInputStream(value.toString().getBytes("UTF-8"));
                    RDFDataMgr.read(model, is, Lang.JSONLD);
                    is.close();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                jsonLDFile = new File(System.getProperty("java.io.tmpdir") + "_temp.json");
                System.out.println(jsonLDFile.getAbsolutePath());
                RDFDataMgr.write(new FileOutputStream(jsonLDFile), model, Lang.JSONLD);

                InputStream is = new FileInputStream(jsonLDFile);
                String jsonTxt = IOUtils.toString(is, "UTF-8");
                output = new JSONObject(jsonTxt);
                is.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            model.close();
        }

        returnOutput();
    }

    @Override
    public void destroy() {
        jsonLDFile.deleteOnExit();
    }
}
