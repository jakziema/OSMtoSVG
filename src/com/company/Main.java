package com.company;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Attribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.w3c.dom.*;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class Main {



    public static void main(String[] args) throws IOException{


    createSVGPoints();

    }

    public static List<MyPoint> getListOfPointsFromFile(String filename) throws IOException {
         List<MyPoint> listOfPoints = new ArrayList<>();

        File file = new File(filename);
        Map<String, Object> map = new HashMap<>();
        map.put("url", file.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        System.out.println(dataStore.getInfo().toString());



        FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);

        FeatureIterator<SimpleFeature> features = collection.features();


        while (features.hasNext()) {
            SimpleFeature feature = features.next();
            //System.out.print(feature.getID());
            //System.out.print(": ");
            //System.out.println(feature.getDefaultGeometry().toString());
            double x = feature.getDefaultGeometryProperty().getBounds().getMinX();
            double y = feature.getDefaultGeometryProperty().getBounds().getMinY() * (-1);
            MyPoint point = new MyPoint(x,y);
            listOfPoints.add(point);
        }
        return listOfPoints;
    }








    public static void createSVGPoints() {
        DOMImplementation implementation = SVGDOMImplementation.getDOMImplementation();

        //creating new document
        String svgNS  = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document document = implementation.createDocument(svgNS, "svg", null);

        //Get the root element of SVG
        org.w3c.dom.Element svgRoot = document.getDocumentElement();

        //width and height of
        svgRoot.setAttributeNS(null, "width", "60");
        svgRoot.setAttributeNS(null, "height", "20");


        try {
            List<MyPoint> listOfPoints = getListOfPointsFromFile("ne_10m_ports.shp");

            for (MyPoint point: listOfPoints) {

                org.w3c.dom.Element circle = document.createElementNS(svgNS, "circle");
                circle.setAttributeNS(null, "cx", String.valueOf(point.x));
                circle.setAttributeNS(null, "cy", String.valueOf(point.y));
                //point.x/y powwinno byc doublem
                circle.setAttributeNS(null, "r", "0.5");
                circle.setAttributeNS(null, "stroke", "black");
                circle.setAttributeNS(null, "stroke-width", "0.5");
                circle.setAttributeNS(null, "fill", "red");


                svgRoot.appendChild(circle);


            }
        } catch(IOException e ) {
            e.printStackTrace();
        }

        /**
        List<Way> listOfWays = getListOfWays();

        for (Way way: listOfWays) {
            org.w3c.dom.Element polyline = document.createElementNS(svgNS, "polyline");

            List<Node> listOfNodesInWay = way.getNodes();

            String points = " ";

            for(Node node: listOfNodesInWay) {

                points += String.valueOf(node.getLon()) + "," + String.valueOf(node.getLat()) + " ";

            }

            polyline.setAttributeNS(null,  "points", points);
            polyline.setAttributeNS(null, "style", "fill:none;stroke:black;stroke-width:3");

            svgRoot.appendChild(polyline);

        }
        */



        String svgAsString = toString(document);
        /*try {
            PrintWriter out = new PrintWriter("D:\\Studia\\Semestr 8\\TMC\\TMCProjekt\\map.svg");
            out.println(svgAsString);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        System.out.println(svgAsString);




    }

    public static String toString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }




}
