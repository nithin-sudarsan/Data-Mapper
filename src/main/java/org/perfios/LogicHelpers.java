package org.perfios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LogicHelpers {
    static void combineMaps(Map<String, Object> targetMap, Map<String, Object> sourceMap) {
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
    // Helper method to check if all elements in a list (including nested lists) are numbers
    protected static boolean areAllNumbers(List<?> list) {  //HELPER
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
    protected static double sumNestedNumbers(List<?> list) { //HELPER
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
    protected static double diffNestedNumbers(List<?> list, double sum) { //HELPER
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
    protected static Map<String, Object> setValueInJson(String path, Object value) { //HELPER
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
    protected static List<Object> getListItems(Object json) { //LOGIC HELPERS
        List<Object> result = new ArrayList<>();
        List<?> list = (List<?>) json;
        for (Object item : list) {
            result.add(item);
        }
        return result;
    }
    protected static double prodNestedNumbers(List<?> list, double prod) { //LOGIC
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
    protected static Object setDefault(List<String> rhs) {  //LOGIC
        int defIndex= rhs.indexOf("#default");
        return rhs.get(defIndex+1);
    }
}
