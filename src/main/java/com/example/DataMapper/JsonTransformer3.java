package com.example.DataMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.json.JsonObject;
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

    public <T>T getObjectFromInput(String inputPath, String formatPath, Class<T> className){
        // Read the transformation rules from the file
        Map<List<String>, List<String>> transformationRules = readTransformationRules(formatPath);
        String jsonString;
        try {
            // Read the JSON file contents into a string
            jsonString = new String(Files.readAllBytes(Paths.get(inputPath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // Create a Gson instance to parse the JSON string
        Gson gson = new GsonBuilder().create();
        // Convert the JSON string to a Map
        Map<String, Object> json = gson.fromJson(jsonString, Map.class);
        // Transform the JSON using the rules from format.txt
        Map<String, Object> transformedJson = transformJson(transformationRules, json);
        return gson.fromJson(gson.toJson(transformedJson.get(className.getSimpleName())), className);
    }

    static Map<String, Object> transformJson(Map<List<String>, List<String>> transformationRules, Map<String, Object> json) {
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
            } else if (rhs.contains("#diff")){
                value = findDifference(json, rhs);
            }
            else if (rhs.contains("#prod")){
                value = findProduct(json, rhs);
            }
            else {
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

    private static Object findProduct(Map<String, Object> json, List<String> rhs) {
        double prod = 0.0;
        boolean initial=true;
        // Iterate over the paths
        for (String path : rhs) {
            if (!path.equals("#prod")) {
                Object value= traverseJson(json,path);
                if (value instanceof Integer || value instanceof Double) {
                    // Numeric value found, add it to the sum
                    double numericValue = ((Number) value).doubleValue();
                    if(initial){
                        prod+=numericValue;
                        initial=false;
                    }
                    else {
                        prod*=numericValue;
                    }
                }
                else if (value instanceof ArrayList) {
                    // ArrayList value found
                    List<?> listValue = (ArrayList<?>) value;
                    if (areAllNumbers(listValue)) {
                        // Nested list contains only numbers, calculate the nested sum
                        prod = prodNestedNumbers(listValue,prod);

                    } else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
        }
        return prod;
    }

    private static double prodNestedNumbers(List<?> list, double prod) {
        for (Object item : list) {
            if (item instanceof Integer || item instanceof Double) {
                // Numeric element found, add it to the sum
                double numericValue = ((Number) item).doubleValue();
                if(prod==0){
                    prod+=numericValue;
                }
                else {
                    prod*=numericValue;
                }
            } else if (item instanceof ArrayList) {
                // Nested list found, recursively calculate the nested sum
                prod = prodNestedNumbers((List<?>) item,prod);
            }
        }
        return prod;
    }

    private static Object findDifference(Map<String, Object> json, List<String> rhs) {
        double sum = 0.0;
        boolean initial=true;
        // Iterate over the paths
        for (String path : rhs) {
            if (!path.equals("#diff")) {
            Object value= traverseJson(json,path);
            if (value instanceof Integer || value instanceof Double) {
                // Numeric value found, add it to the sum
                double numericValue = ((Number) value).doubleValue();
                if(initial){
                    sum+=numericValue;
                    initial=false;
                }
                else {
                    sum-=numericValue;
                }
            }
            else if (value instanceof ArrayList) {
                // ArrayList value found
                List<?> listValue = (ArrayList<?>) value;
                if (areAllNumbers(listValue)) {
                    // Nested list contains only numbers, calculate the nested sum
                    double nestedSum = diffNestedNumbers(listValue,sum);
                    sum -= nestedSum;
                } else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
        }
        return sum;
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
        // Initialize variables
        double sum = 0.0;
        StringBuilder concatenatedString = new StringBuilder();
        boolean hasString = false;

        // Iterate over the paths
        for (String path : rhs) {
            // Exclude the special keyword '#sum'
            if (!path.equals("#sum")) {
                // Traverse the JSON using the specified path
                Object value = traverseJson(json, path);

                // Check the type of the value
                if (value instanceof Integer || value instanceof Double) {
                    // Numeric value found, add it to the sum
                    double numericValue = ((Number) value).doubleValue();
                    sum += numericValue;
                } else if (value instanceof String) {
                    // String value found
                    if (hasString) {
                        // Append a space before appending the value if there is already a string
                        concatenatedString.append(" ");
                    }
                    concatenatedString.append(value);
                    hasString = true;
                } else if (value instanceof ArrayList) {
                    // ArrayList value found
                    List<?> listValue = (ArrayList<?>) value;
                    if (areAllNumbers(listValue)) {
                        // Nested list contains only numbers, calculate the nested sum
                        double nestedSum = sumNestedNumbers(listValue);
                        sum += nestedSum;
                    } else {
                        // Nested list contains non-number elements, concatenate them
                        for (Object listItem : listValue) {
                            if (hasString) {
                                // Append a space before appending the list item if there is already a string
                                concatenatedString.append(" ");
                            }
                            concatenatedString.append(listItem);
                            hasString = true;
                        }
                    }
                }
            }
        }

        // Return the result
        if (hasString) {
            return concatenatedString.toString();
        } else {
            return sum;
        }
    }

    // Helper method to check if all elements in a list (including nested lists) are numbers
    private static boolean areAllNumbers(List<?> list) {
        for (Object item : list) {
            if (item instanceof Integer || item instanceof Double) {
                // Numeric element found, continue checking
                continue;
            } else if (item instanceof List) {
                // Nested list found, recursively check if it contains only numbers
                List<?> nestedList = (List<?>) item;
                if (!areAllNumbers(nestedList)) {
                    return false;
                }
            } else {
                // Non-numeric element found, return false
                return false;
            }
        }
        // All elements are numbers
        return true;
    }

    // Helper method to calculate the sum of all numbers in a list (including nested lists)
    private static double sumNestedNumbers(List<?> list) {
        double sum = 0.0;
        for (Object item : list) {
            if (item instanceof Integer || item instanceof Double) {
                // Numeric element found, add it to the sum
                double numericValue = ((Number) item).doubleValue();
                sum += numericValue;
            } else if (item instanceof ArrayList) {
                // Nested list found, recursively calculate the nested sum
                double nestedSum = sumNestedNumbers((List<?>) item);
                sum += nestedSum;
            }
        }
        return sum;
    }
    private static double diffNestedNumbers(List<?> list, double sum) {
        for (Object item : list) {
            if (item instanceof Integer || item instanceof Double) {
                // Numeric element found, add it to the sum
                double numericValue = ((Number) item).doubleValue();
                if(sum==0){
                    sum+=numericValue;
                }
                else {
                    sum-=numericValue;
                }
            } else if (item instanceof ArrayList) {
                // Nested list found, recursively calculate the nested sum
                double nestedSum = diffNestedNumbers((List<?>) item,sum);
                sum -= nestedSum;
            }
        }
        return sum;
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
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (keys[i].endsWith("[]")) {
                // Handle array traversal
                key = key.substring(0, key.length() - 2);
                json = ((Map<?, ?>) json).get(key);
                // Get a list of items from the array
                List<Object> resultList = getListItems(json);
                List<Object> outputList = new ArrayList<>();
                for (Object item : resultList) {
                    // Get the remaining path after the current key
                    String remainingPath = String.join("/", Arrays.copyOfRange(keys, i + 1, keys.length));
                    // Recursively traverse the JSON with the remaining path
                    Object result = traverseJson(item, remainingPath);
                    if (result != null) {
                        outputList.add(result);
                    }
                }
                // Check if any non-null values were found in the output list
                if (outputList.size() > 0) {
                    return outputList;
                } else {
                    return null;
                }
            } else {
                // Check if the JSON contains the key
                if (((Map<?, ?>) json).containsKey(key)) {
                    // Access the value using the specified key
                    json = ((Map<?, ?>) json).get(key);
                } else {
                    // If the key is not found, return null
                    return null;
                }
            }
        }
        // Handle returning int or double values
        if (json instanceof Integer || json instanceof Double) {
            List<Object> numericList = new ArrayList<>();
            numericList.add(json);
            return numericList;
        }
        return json;
    }


    // Method to read the transformation rules from the specified file
    static Map<List<String>, List<String>> readTransformationRules(String formatFilePath) {
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
