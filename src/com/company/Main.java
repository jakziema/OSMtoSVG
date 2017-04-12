package com.company;



import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import java.io.File;
import java.io.IOException;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.*;

import javax.swing.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class Main {

    static OSMParser osmParser = new OSMParser();
    static File osmFile = new File("map.osm");


    public static void main(String[] args) {

        osmParser = new OSMParser();

        List<Node> listOfNodes = new ArrayList<>();
        List<Way> listOfWays = new ArrayList<>();
        List<Relation> listOfRelations = new ArrayList<>();

        try {

            Map<String,Element> result = osmParser.parse(osmFile);

            for (Map.Entry<String, Element> entry : result.entrySet()) {

                String key = entry.getKey();

                if(key.contains("N")) {

                    Node node = (Node)entry.getValue();
                    listOfNodes.add(node);

                } else if(key.contains("W")) {

                    Way way = (Way)entry.getValue();
                    listOfWays.add(way);

                } else if (key.contains("R")) {

                    Relation relation = (Relation)entry.getValue();
                    listOfRelations.add(relation);
                }





            }



        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }


        createSVGPoint();
    }

    public static void createSVGPoint() {
        DOMImplementation implementation = SVGDOMImplementation.getDOMImplementation();

        //creating new document
        String svgNS  = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document document = implementation.createDocument(svgNS, "svg", null);

        //Get the root element of SVG
        org.w3c.dom.Element svgRoot = document.getDocumentElement();

        //width and height of
        svgRoot.setAttributeNS(null, "width", "400");
        svgRoot.setAttributeNS(null, "height", "450");

        org.w3c.dom.Element rectangle = document.createElementNS(svgNS, "rect");
        rectangle.setAttributeNS(null, "x", "10");
        rectangle.setAttributeNS(null, "y", "20");
        rectangle.setAttributeNS(null, "width", "100");
        rectangle.setAttributeNS(null, "height", "50");
        rectangle.setAttributeNS(null, "fill", "red");

        svgRoot.appendChild(rectangle);

        saveDocumentFile(document);


    }


    public static  void saveDocumentFile (Document document) {


//            TransformerFactory tFactory =
//                    TransformerFactory.newInstance();
//        Transformer transformer =
//                null;
//        try {
//            transformer = tFactory.newTransformer();
//        } catch (TransformerConfigurationException e) {
//            e.printStackTrace();
//        }
//
//        DOMSource source = new DOMSource(document);
//            StreamResult result = new StreamResult(new File("map.xml"));
//        try {
//            transformer.transform(source, result);
//        } catch (TransformerException e) {
//            e.printStackTrace();
//        }

        String filename = "map.xml";

        Source source = new DOMSource(document);

        File file = new File(filename);
        Result result = new StreamResult(file);

        Transformer xformer = null;
        try {
            xformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        try {
            xformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

}
