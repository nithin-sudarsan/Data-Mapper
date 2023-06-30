package com.example.DataMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.System.*;

public class JsonTransformer3 {
    public static void main(String[] args) {
        // Path to the file containing the transformation rules
        String formatFilePath = "/home/nithin/Desktop/format.txt";
        // Read the transformation rules from the file
        Map<List<String>, List<String>> transformationRules = readTransformationRules(formatFilePath);
        // Path to the JSON file to transform
        String jsonFilePath = "/home/nithin/Desktop/sample.json";
        String jsonString;
        try {
            // Read the JSON file contents into a string
            jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Create a Gson instance to parse the JSON string
        Gson gson = new GsonBuilder().create();
        // Convert the JSON string to a Map
        Map<String, Object> json = gson.fromJson(jsonString, Map.class);
        // Transform the JSON using the rules from format.txt
        Map<String, Object> transformedJson = transformJson(transformationRules, json);
        out.println(transformedJson);
    }

    private static Map<String, Object> transformJson(Map<List<String>, List<String>> transformationRules, Map<String, Object> json) {
        // Create a new map to store the transformed JSON
        Map<String, Object> transformedJson = new HashMap<>();
        // Iterate over the transformation rules
        for (Map.Entry<List<String>, List<String>> entry : transformationRules.entrySet()) {
            List<String> lhs = entry.getKey(); // Left-hand side of the rule
            List<String> rhs = entry.getValue(); // Right-hand side of the rule
            String lhsString= lhs.get(0);
            Object value;
            // Check if the right-hand side contains a special keyword '#sum'
            if (rhs.contains("#sum")) {
                // Concatenate values based on the specified paths
                value = concatenateValues(json, rhs);
            } else {
                // Join the elements of the right-hand side list with '/' separator
                String rhsString = String.join("/", rhs);
                // Traverse the JSON using the right-hand side path and retrieve the values
                value = traverseJson(json, rhsString);
            }
            // Update the transformed JSON by setting the value at the specified left-hand side path
            combineMaps(transformedJson, setValueInJson(lhsString,value));
        }
        return transformedJson;
    }

    // Recursive method to combine two maps
    private static void combineMaps(Map<String, Object> targetMap, Map<String, Object> sourceMap) {
        for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // Check if the target map already contains the key and both values are maps
            if (targetMap.containsKey(key) && value instanceof Map && targetMap.get(key) instanceof Map) {
                // Recursively combine the nested maps
                Map<String, Object> nestedTargetMap = (Map<String, Object>) targetMap.get(key);
                Map<String, Object> nestedSourceMap = (Map<String, Object>) value;
                combineMaps(nestedTargetMap, nestedSourceMap);
            } else {
                // Set the value directly in the target map
                targetMap.put(key, value);
            }
        }
    }

    // Method to perform concatenation based on the specified paths
    private static Object concatenateValues(Map<String, Object> json, List<String> rhs) {
        List<String> valuesToConcatenate = new ArrayList<>();
        for (String path : rhs) {
            // Exclude the special keyword '#sum'
            if (!path.equals("#sum")) {
                // Traverse the JSON using the specified path
                Object value = traverseJson(json, path); // Use Object type for the value

                if (value instanceof String) {
                    valuesToConcatenate.add((String) value);
                } else if (value instanceof ArrayList) {
                    List<String> listValue = (ArrayList) value;
                    valuesToConcatenate.addAll(listValue);
                }
            }
        }
        // Join the values with a space separator
        return String.join(" ", valuesToConcatenate);
    }

    // Method to set a value in the JSON based on the specified path
    private static Map<String, Object> setValueInJson(String path, Object value) {
        Map<String, Object> current = new HashMap<>();
        String[] keys = path.split("/");
        Map<String, Object> innerMap = current;

        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            Map<String, Object> newMap = new HashMap<>();
            innerMap.put(key, newMap);
            innerMap = newMap;
        }

        String finalKey = keys[keys.length - 1];
        innerMap.put(finalKey, value);

        return current;
    }

    // Method to traverse the JSON based on the specified path
    private static Object traverseJson(Object json, String path) {
        String[] keys = path.split("/");
        for (int i=0; i<keys.length; i++){
            String key=keys[i];
            if(keys[i].endsWith("[]") ){
                // Handle array traversal
                key = key.substring(0, key.length() - 2);
                json = ((Map<?, ?>) json).get(key);
                // Get a list of items from the array
                List<Object> resultList = getListItems(json);
                List<Object> outputList= new ArrayList<>();
                for (Object item: resultList){
                    // Get the remaining path after the current key
                    String remainingPath = String.join("/", Arrays.copyOfRange(keys, i + 1, keys.length));
                    // Recursively traverse the JSON with the remaining path
                    json=traverseJson(item, remainingPath);
                    if(json!=null){ outputList.add(json);}
                }
                // Check if any non-null values were found in the output list
                if(outputList.size()>0){
                    return outputList;
                }
                else {return null;}
            }
            else {
                // Check if the JSON contains the key
                if(((Map) json).containsKey(key)){
                    // Access the value using the specified key
                    json = ((Map<?, ?>) json).get(key);}
                else {
                    // If the key is not found, return null
                    return null;
                }
            }
        }
        return json;
    }

    // Method to read the transformation rules from the specified file
    private static Map<List<String>, List<String>> readTransformationRules(String formatFilePath) {
        Map<List<String>, List<String>> transformationRules = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(formatFilePath))) {
            stream.forEach(line -> {
                String[] parts = line.split("=");
                String sourceKey = parts[0].trim();
                String targetKey = parts[1].trim();
                List<String> sourceList = Arrays.asList(sourceKey);
                List<String> targetList = Arrays.asList(targetKey.split(" "));

                transformationRules.put(sourceList, targetList);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transformationRules;
    }

    // Method to convert a JSON object to a list of items
    private static List<Object> getListItems(Object json) {
        List<Object> result = new ArrayList<>();
        List<?> list = (List<?>) json;
        for (Object item : list) {
            result.add(item);
        }
        return result;
    }
}
