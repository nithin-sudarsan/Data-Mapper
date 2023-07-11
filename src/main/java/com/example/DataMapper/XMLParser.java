//package com.example.DataMapper;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.example.DataMapper.JsonTransformer3.readTransformationRules;
//import static com.example.DataMapper.JsonTransformer3.transformJson;
//import static java.lang.System.out;
//
//public class XMLParser {
//    public static void main(String[] args) {
//        String inputPath = "/home/nithin/Desktop/sample2.xml";
//        String formatPath= "/home/nithin/Desktop/format2.txt";
//        Map<List<String>, List<String>> transformationRules = readTransformationRules(formatPath);
//        String xmlString;
//        try {
//            // Read the XML file contents into a string
//            xmlString = new String(Files.readAllBytes(Paths.get(inputPath)));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//
//        try {
//            // Create an XmlMapper
//            XmlMapper xmlMapper = new XmlMapper();
//
//            // Enable XML-specific features (optional)
//            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//            // Convert XML to a Map
//            Map<String, Object> xmlMap = xmlMapper.readValue(xmlString, HashMap.class);
//
//            // Create a new Map with "settings" key
//            System.out.println(xmlMap);
//            Map<String, Object> transformedXml = transformJson(transformationRules, xmlMap);
//            out.println(transformedXml);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
