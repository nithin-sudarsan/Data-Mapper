package org.perfios;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.perfios.Logic.readTransformationRules;
import static org.perfios.Logic.transformJson;

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
    /**
     * Transforms an input JSON or XML file or string according to specified rules and returns a string representing the transformed input.
     *
     * @param input     The input JSON or XML file path or string to be transformed.
     * @param rules     The rules file path or string containing transformation rules.
     * @param isString  A boolean value indicating whether the input is a string (true) or a file path (false).
     * @return A string representing the transformed JSON or XML input.
     * @throws IllegalArgumentException If the input or rules are null or empty, or if the input is not a valid JSON or XML string (when isString is true).
     * @throws RuntimeException If there is an error parsing or processing the input or applying transformation rules.
     * @author S.Nithin
     */
    @Override
    public String getTransformedString(String input, String rules, boolean isString) {
        if (isString) {
            if (input == null || rules == null || rules.isEmpty()) {
                throw new IllegalArgumentException("Invalid input: input and rules strings cannot be null or empty.");
            }

            Gson gson = new GsonBuilder().create();

            try {
                if (input.startsWith("{")) {
                    Map<String, Object> json = gson.fromJson(input, Map.class);
                    Map<String, Object> transformedJson = transformJson(readTransformationRules(rules, true), json);
                    return gson.toJson(transformedJson);
                } else if (input.startsWith("<")) {
                    XmlMapper xmlMapper = new XmlMapper();
                    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                    Map<String, Object> xmlMap = xmlMapper.readValue(input, HashMap.class);
                    Map<String, Object> transformedXml = transformJson(readTransformationRules(rules, true), xmlMap);
                    return xmlMapper.writeValueAsString(transformedXml);
                }
            } catch (JsonSyntaxException | JsonProcessingException e) {
                throw new RuntimeException("Error parsing input: " + e.getMessage(), e);
            }
        } else {
            if (input == null || input.isEmpty() || rules == null || rules.isEmpty()) {
                throw new IllegalArgumentException("Invalid input: input and rules paths cannot be null or empty.");
            }

            try {
                String fileString = new String(Files.readAllBytes(Paths.get(input)));
                if (input.endsWith(".json")) {
                    Gson gson = new GsonBuilder().create();
                    Map<String, Object> json = gson.fromJson(fileString, Map.class);
                    Map<String, Object> transformedJson = transformJson(readTransformationRules(rules, false), json);
                    return gson.toJson(transformedJson);
                } else if (input.endsWith(".xml")) {
                    XmlMapper xmlMapper = new XmlMapper();
                    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
                    Map<String, Object> xmlMap = xmlMapper.readValue(fileString, HashMap.class);
                    Map<String, Object> transformedXml = transformJson(readTransformationRules(rules, false), xmlMap);
                    return xmlMapper.writeValueAsString(transformedXml);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error processing input: " + e.getMessage(), e);
            }
        }
        throw new IllegalArgumentException("Invalid input: unsupported input format or missing input.");
    }
}
