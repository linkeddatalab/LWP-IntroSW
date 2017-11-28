package org.linkedwidgets.example.widget.server.rdf.maplimit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.json.JSONObject;
import org.lw.serverwidget.core.ServerWidgetJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapPointLimit extends ServerWidgetJob {

    private static final Logger log = LoggerFactory.getLogger(MapPointLimit.class);

    @Override
    public void run() {

        if (inputs.length == 0) {
            output = new JSONObject();
            log.info("output1: " + output);

        } else {
            Integer limit = Integer.parseInt(params.get("limit").toString());

            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
            Model outputModel = ModelFactory.createDefaultModel();

            JSONObject value = inputs[0];
            try {
                InputStream is = new ByteArrayInputStream(value.toString().getBytes("UTF-8"));
                RDFDataMgr.read(model, is, Lang.JSONLD);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Property wgsLocation = model.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#location");
            Property wgsLat = model.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
            Property wgsLong = model.getProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long");
            ResIterator places = model.listSubjectsWithProperty(wgsLocation);

            int i = 0;
            while (places.hasNext() && i < limit) {
                Resource resource = places.next();

                StmtIterator stmtIterator = model.listStatements(resource, null, (RDFNode) null);
                stmtIterator.forEachRemaining(statement -> {
                    RDFNode object = statement.getObject();
                    if (object.isResource()) {
                        outputModel.add(model.listStatements(object.asResource(), wgsLat, (RDFNode) null));
                        outputModel.add(model.listStatements(object.asResource(), wgsLong, (RDFNode) null));
                    }
                    outputModel.add(statement);
                });
                i++;
            }

            StringWriter stringWriter = new StringWriter();
            RDFDataMgr.write(stringWriter, outputModel, Lang.JSONLD);
            String jsonTxt = stringWriter.toString();
            output = new JSONObject(jsonTxt);

            log.info("output2: " + output);

            outputModel.close();
            model.close();
        }

        returnOutput();
    }

    @Override
    public void destroy() {

    }
}
