package org.perfios;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.perfios.Logic.readTransformationRules;
import static org.perfios.Logic.transformJson;
import static org.perfios.LogicHelpers.removeTopmostTag;

public final class DataMapperImpl implements DataMapper{
    /**
     * Transforms a JSON or XML file according to specified rules and returns an instance of the desired class type.
     *
     * @param inputPath  The path to the input file (JSON or XML) to be transformed.
     * @param rulesPath  The path to the rules file containing transformation rules.
     * @param className  The desired class type to be returned after transformation.
     * @param <T>        The generic type representing the desired class type.
     * @return An instance of the desired class type, representing the transformed data.
     * @throws IOException If an I/O error occurs while reading the input or rules file.
     * @throws IllegalArgumentException If the inputPath or rulesPath is null or empty.
     * @throws RuntimeException If there is an error parsing the input file or applying transformation rules.
     * @author S.Nithin
     */
    @Override
    public <T> T transformFile(String inputPath, String rulesPath, Class<T> className) throws IOException {
        if (inputPath == null || inputPath.isEmpty() || rulesPath == null || rulesPath.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: inputPath and rulesPath cannot be null or empty.");
        }
        // Read the transformation rules from the file
        Map<List<String>, List<String>> transformationRules = readTransformationRules(rulesPath, false);
        String fileString;
        try {
            // Read the JSON file contents into a string
            fileString = new String(Files.readAllBytes(Paths.get(inputPath)));
        } catch (IOException e) {
            throw new IOException("Error reading input file: " + e.getMessage());
        }
        // Create a Gson instance to parse the JSON string
        Gson gson = new GsonBuilder().create();
        if (inputPath.endsWith(".json")) {
            try{
                // Convert the JSON string to a Map
                Map<String, Object> json = gson.fromJson(fileString, Map.class);
                // Transform the JSON using the rules from format.txt
                Map<String, Object> transformedJson = transformJson(transformationRules, json);
                return gson.fromJson(gson.toJson(transformedJson.get(className.getSimpleName())), className);
            }catch (JsonSyntaxException e) {
                throw new RuntimeException("Error parsing JSON input: " + e.getMessage());
            }
        } else if (inputPath.endsWith(".xml")) {
            final var xmlMapper = new XmlMapper();
            // Enable XML-specific features (optional)
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try{
                // Convert XML to a Map
                Map<String, Object> xmlMap = xmlMapper.readValue(fileString, HashMap.class);
                Map<String, Object> transformedXml = transformJson(transformationRules, xmlMap);
                return gson.fromJson(gson.toJson(transformedXml.get(className.getSimpleName())), className);
            }
            catch (JsonParseException e) {
                throw new RuntimeException("Error parsing XML input: " + e.getMessage());
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported file format: inputPath must have .json or .xml extension.");
        }
    }
    /**
     * Transforms an input JSON or XML file according to specified rules and returns an instance of the desired class type.
     *
     * @param input     The input JSON or XML file to be transformed.
     * @param rules     The rules file containing transformation rules.
     * @param className The desired class type to be returned after transformation.
     * @param <T>       The generic type representing the desired class type.
     * @return An instance of the desired class type, representing the transformed data.
     * @throws IllegalArgumentException If the input or rules files are empty.
     * @throws RuntimeException If there is an error reading the files or applying transformation rules.
     * @author S.Nithin
     */
    @Override
    public <T> T transformFile(File input, File rules, Class<T> className){
        String inputString;
        String rulesString;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(input.getAbsolutePath()));
            inputString = new String(bytes);
            byte[] bytes2 = Files.readAllBytes(Paths.get(rules.getAbsolutePath()));
            rulesString = new String(bytes2);
        } catch (IOException e) {
            throw new RuntimeException("Error reading files: " + e.getMessage(), e);
        }
        if (inputString.isEmpty()  || rulesString.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: input and rules files cannot be empty.");
        }
        try {
            return transformString(inputString,rulesString,className);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Transforms a JSON or XML string according to specified rules and returns an instance of the desired class type.
     *
     * @param inputString  The JSON or XML string to be transformed.
     * @param rulesString  The string containing transformation rules.
     * @param className    The desired class type to be returned after transformation.
     * @param <T>          The generic type representing the desired class type.
     * @return An instance of the desired class type, representing the transformed data.
     * @throws JsonProcessingException If there is an error processing the JSON or XML string.
     * @throws IllegalArgumentException If the inputString or rulesString is null or empty, or if the inputString is not a valid JSON or XML string.
     * @throws RuntimeException If there is an error applying transformation rules.
     * @author S.Nithin
     */
    @Override
    public <T> T transformString(String inputString, String rulesString, Class<T> className) throws JsonProcessingException {
        if (inputString == null || inputString.isEmpty() || rulesString == null || rulesString.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: inputString and rulesString cannot be null or empty.");
        }
        Map<List<String>, List<String>> transformationRules = readTransformationRules(rulesString, true);
        // Create a Gson instance to parse the JSON string
        Gson gson = new GsonBuilder().create();
        try{
            if (inputString.startsWith("{")){
                // Convert the JSON string to a Map
                Map<String, Object> json = gson.fromJson(inputString, Map.class);
                // Transform the JSON using the rules from format.txt
                Map<String, Object> transformedJson = transformJson(transformationRules, json);
                return gson.fromJson(gson.toJson(transformedJson.get(className.getSimpleName())), className);
            }
            else if (inputString.startsWith("<")) {
                XmlMapper xmlMapper = new XmlMapper();
                // Enable XML-specific features (optional)
                xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                // Convert XML to a Map
                Map<String, Object> xmlMap = xmlMapper.readValue(inputString, HashMap.class);
                Map<String, Object> transformedXml = transformJson(transformationRules, xmlMap);
                return gson.fromJson(gson.toJson(transformedXml.get(className.getSimpleName())), className);
            }
            else {
                throw new IllegalArgumentException("Unsupported input format: inputString must be a JSON or XML string.");
            }
            }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing input: " + e.getMessage(), e);
        }
    }
    public enum Extension{
        JSON, XML
    }

    /**
     * Transforms an input JSON or XML file according to specified rules and returns a string representing the transformed input.
     *
     * @param input  The input JSON or XML file to be transformed.
     * @param rules  The rules file containing transformation rules.
     * @param ext    The extension indicating the format of the input file (JSON or XML).
     * @return A string representing the transformed JSON or XML input.
     * @throws IllegalArgumentException If the input or rules files are empty.
     * @throws RuntimeException If there is an error reading the files or applying transformation rules.
     * @author S.Nithin
     */
    @Override
    public String getTransformedString(File input, File rules, Extension ext) {
        String inputString;
        String rulesString;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(input.getAbsolutePath()));
            inputString = new String(bytes);
            byte[] bytes2 = Files.readAllBytes(Paths.get(rules.getAbsolutePath()));
            rulesString = new String(bytes2);
        } catch (IOException e) {
            throw new RuntimeException("Error reading files: " + e.getMessage(), e);
        }
        if (inputString.isEmpty()  || rulesString.isEmpty()) {
                throw new IllegalArgumentException("Invalid input: input and rules files cannot be empty.");
        }
        return getTransformedString(inputString,rulesString,ext);
    }

    /**
     * Transforms an input JSON or XML string according to specified rules and returns a string representing the transformed input
     * in the desired extension format.
     *
     * @param input The input JSON or XML string to be transformed.
     * @param rules The rules string containing transformation rules.
     * @param ext   The desired extension format for the transformed output (JSON or XML).
     * @return A string representing the transformed JSON or XML input in the desired extension format.
     * @throws IllegalArgumentException If the input or rules strings are null or empty.
     * @throws RuntimeException If there is an error parsing the input, processing the rules, or applying transformation rules.
     * @author S.Nithin
     */
    @Override
    public String getTransformedString(String input, String rules, Extension ext){
        if (input == null || rules == null || rules.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: input and rules strings cannot be null or empty.");
        }

        Gson gson = new GsonBuilder().create();
        XmlMapper xmlMapper = new XmlMapper();
        try {
            if (input.startsWith("{")) {
                Map<String, Object> json = gson.fromJson(input, Map.class);
                Map<String, Object> transformedJson = transformJson(readTransformationRules(rules, true), json);
                if (ext.equals(Extension.JSON)){
                    return gson.toJson(transformedJson);
                }
                else if (ext.equals(Extension.XML)){
                    return removeTopmostTag(xmlMapper.writeValueAsString(transformedJson));
                }
            } else if (input.startsWith("<")) {
                xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                Map<String, Object> xmlMap = xmlMapper.readValue(input, HashMap.class);
                Map<String, Object> transformedXml = transformJson(readTransformationRules(rules, true), xmlMap);
                if (ext.equals(Extension.JSON)){
                    return gson.toJson(transformedXml);
                }
                else if (ext.equals(Extension.XML)){
                    return removeTopmostTag(xmlMapper.writeValueAsString(transformedXml));
                }
            }
        } catch (JsonSyntaxException | JsonProcessingException e) {
            throw new RuntimeException("Error parsing input: " + e.getMessage(), e);
        }
        throw new IllegalArgumentException("Invalid input: unsupported input format or missing input.");
    }

    @Override
    public <T> T transformBean(Object inputBean, File rules,Class<T> className) {
        Gson gson = new GsonBuilder().create();
        String inputString = "{\""+inputBean.getClass().getSimpleName()+"\":"+gson.toJson(inputBean)+"}";
        String rulesString;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(rules.getAbsolutePath()));
            rulesString = new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Error reading files: " + e.getMessage(), e);
        }
        if (inputString.isEmpty()  || rulesString.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: input and rules files cannot be empty.");
        }
        try {
            return transformString(inputString,rulesString,className);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T transformBean(Object inputBean, String rulesString, Class<T> className) throws JsonProcessingException {
        Gson gson = new GsonBuilder().create();
        String jsonString = "{\""+inputBean.getClass().getSimpleName()+"\":"+gson.toJson(inputBean)+"}";
        return transformString(jsonString,rulesString,className);
    }
}
