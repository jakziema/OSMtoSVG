package com.company;



import com.vividsolutions.jts.geom.Point;
import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.*;
import org.xml.sax.SAXException;



import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class Main {

    static OSMParser osmParser = new OSMParser();
    static File osmFile = new File("map.osm");


    public static void main(String[] args) {




        createSVGPoints();
    }

    public static List<Node> getListOfNodes() {



        List<Node> listOfNodes = new ArrayList<>();

        try {

            Map<String,Element> result = osmParser.parse(osmFile);

            for (Map.Entry<String, Element> entry : result.entrySet()) {

                String key = entry.getKey();

                if(key.contains("N")) {

                    Node node = (Node)entry.getValue();
                    listOfNodes.add(node);
                }

            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return listOfNodes;
    }

    public static List<Way> getListOfWays() {

        List<Way> listOfWays = new ArrayList<>();

        try {

            Map<String,Element> result = osmParser.parse(osmFile);

            for (Map.Entry<String, Element> entry : result.entrySet()) {

                String key = entry.getKey();

                if(key.contains("W")) {

                    Way way = (Way)entry.getValue();
                    listOfWays.add(way);
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return listOfWays;
    }

    public static List<Relation> getListOfRelations() {
        List<Relation> listOfRelations = new ArrayList<>();

        try {

            Map<String,Element> result = osmParser.parse(osmFile);

            for (Map.Entry<String, Element> entry : result.entrySet()) {

                String key = entry.getKey();

                if(key.contains("R")) {

                    Relation relation = (Relation)entry.getValue();
                    listOfRelations.add(relation);
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        return listOfRelations;
    }
    

    public static void createSVGPoints() {
        DOMImplementation implementation = SVGDOMImplementation.getDOMImplementation();

        //creating new document
        String svgNS  = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document document = implementation.createDocument(svgNS, "svg", null);

        //Get the root element of SVG
        org.w3c.dom.Element svgRoot = document.getDocumentElement();

        //width and height of
        svgRoot.setAttributeNS(null, "width", "400");
        svgRoot.setAttributeNS(null, "height", "450");


        List<Node> listOfNodes = getListOfNodes();

        for (Node node: listOfNodes) {

            org.w3c.dom.Element circle = document.createElementNS(svgNS, "circle");
            circle.setAttributeNS(null, "cx", String.valueOf(node.getLat()));
            circle.setAttributeNS(null, "cy", String.valueOf(node.getLon()));
            circle.setAttributeNS(null, "r", "2");
            circle.setAttributeNS(null, "stroke", "black");
            circle.setAttributeNS(null, "stroke-width", "3");
            circle.setAttributeNS(null, "fill", "red");

            svgRoot.appendChild(circle);

        }



        String svgAsString = toString(document);

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
