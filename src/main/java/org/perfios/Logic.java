package org.perfios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.perfios.LogicHelpers.*;

class Logic {
    static Map<List<String>, List<String>> readTransformationRules(String rules, boolean isString)
    {
        Map<List<String>, List<String>> transformationRules = new HashMap<>();
        if(isString){
            try (BufferedReader reader = new BufferedReader(new StringReader(rules))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    String sourceKey = parts[0].trim();
                    String targetKey = parts[1].trim();
                    List<String> sourceList = Arrays.asList(sourceKey);
                    List<String> targetList = Arrays.asList(targetKey.split(" "));

                    transformationRules.put(sourceList, targetList);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try (Stream<String> stream = Files.lines(Paths.get(rules))) {
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
        }
        return transformationRules;
    }
    static Object traverseJson(Object json, String path) {  //LOGIC
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
                }
                else {
                    throw new RuntimeException("No values found in the input file");
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
    static Map<String, Object> transformJson(Map<List<String>, List<String>> transformationRules, Map<String, Object> json) { //LOGIC
        // Create a new map to store the transformed JSON
        Map<String, Object> transformedJson = new HashMap<>();
        // Iterate over the transformation rules
        for (Map.Entry<List<String>, List<String>> entry : transformationRules.entrySet()) {
            List<String> lhs = entry.getKey(); // Left-hand side of the rule
            List<String> rhs = entry.getValue(); // Right-hand side of the rule
            String lhsString= lhs.get(0);
            Object value;
            // Check if the right-hand side contains a special keyword '#sum'
            if (rhs.contains("#add")) {
                // Concatenate values based on the specified paths
                value = concatenateValues(json, rhs);
            } else if (rhs.contains("#sub")){
                value = findDifference(json, rhs);
            }
            else if (rhs.contains("#mul")){
                value = findProduct(json, rhs);
            }
            else if(rhs.contains("div")){
                value = findQuotient(json, rhs);
            }
            else if (rhs.contains("#default")){
                value= setDefault(rhs);
            }
            else {
                // Join the elements of the right-hand side list with '/' separator
                String rhsString = String.join("/", rhs);
                // Traverse the JSON using the right-hand side path and retrieve the values
                value = traverseJson(json, rhsString);
                if (value instanceof ArrayList<?> && ((ArrayList<?>) value).size()==1){
                    value=((ArrayList<?>) value).get(0);
                }
            }
            // Update the transformed JSON by setting the value at the specified left-hand side path
            combineMaps(transformedJson, setValueInJson(lhsString,value));
        }
        return transformedJson;
    }
    //#SUM
    static Object concatenateValues(Map<String, Object> json, List<String> rhs){
        double sum = 0.0;
        StringBuilder concatenatedString = new StringBuilder();
        boolean hasString = false;

        // Iterate over the paths
        for (String path : rhs) {
            // Exclude the special keyword '#sum'
            if (!path.equals("#add")) {
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
    //#DIFF
    static Object findDifference(Map<String, Object> json, List<String> rhs)
    {
        double diff = 0.0;
        boolean initial=true;
        // Iterate over the paths
        for (String path : rhs) {
            if (!path.equals("#sub")) {
                Object value= traverseJson(json,path);
                if (value instanceof Integer || value instanceof Double) {
                    // Numeric value found, add it to the sum
                    double numericValue = ((Number) value).doubleValue();
                    if(initial){
                        diff+=numericValue;
                        initial=false;
                    }
                    else {
                        diff-=numericValue;
                    }
                }
                else if (value instanceof ArrayList) {
                    // ArrayList value found
                    List<?> listValue = (ArrayList<?>) value;
                    if (areAllNumbers(listValue)) {
                        // Nested list contains only numbers, calculate the nested sum
                        double nestedSum = diffNestedNumbers(listValue,diff);
                        diff -= nestedSum;
                    } else {
                        throw new RuntimeException("All elements in the ArrayList must be numbers");
                    }
                }
                else {
                    throw new RuntimeException("Cannot perform subtraction on values other than Number or ArrayList of numbers");
                }
            }
        }
        return diff;
    }
    //#PROD
    static Object findProduct(Map<String, Object> json, List<String> rhs){
        double prod = 0.0;
        boolean initial=true;
        // Iterate over the paths
        for (String path : rhs) {
            if (!path.equals("#mul")) {
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
                        throw new RuntimeException("All elements in the ArrayList must be numbers");
                    }
                }
                else {
                    throw new RuntimeException("Cannot perform multiplication on values other than Number or ArrayList of numbers");
                }
            }
        }
        return prod;
    }
    static Object findQuotient(Map<String, Object> json, List<String> rhs){
        double quo = 0.0;
        boolean initial=true;
        // Iterate over the paths
        for (String path : rhs) {
            if (!path.equals("#div")) {
                Object value= traverseJson(json,path);
                if (value instanceof Integer || value instanceof Double) {
                    // Numeric value found, add it to the sum
                    double numericValue = ((Number) value).doubleValue();
                    if(initial){
                        quo+=numericValue;
                        initial=false;
                    }
                    else {
                        quo/=numericValue;
                    }
                }
                else if (value instanceof ArrayList) {
                    // ArrayList value found
                    List<?> listValue = (ArrayList<?>) value;
                    if (areAllNumbers(listValue)) {
                        // Nested list contains only numbers, calculate the nested sum
                        quo = divideNestedNumbers(listValue,quo);

                    } else {
                        throw new RuntimeException("All elements in the ArrayList must be numbers");
                    }
                }
                else {
                    throw new RuntimeException("Cannot perform division on values other than Number or ArrayList of numbers");
                }
            }
        }
        return quo;
    }

}
