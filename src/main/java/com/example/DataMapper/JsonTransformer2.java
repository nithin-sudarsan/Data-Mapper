package com.example.DataMapper;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class JsonTransformer2 {
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

    private static Map<String, Object> transformJson(Map<List<String>, List<String>> transformationRules, Map<String, Object> json) {
        Map<String, Object> transformedJson = new HashMap<>();
        for (Map.Entry<List<String>, List<String>> entry : transformationRules.entrySet()) {
            List<String> outputPath = entry.getKey();
            List<String> inputPaths = entry.getValue();
            Gson gson = new GsonBuilder().create();
            String jsonString = gson.toJson(json);
            Object value = gson.fromJson(jsonString, Object.class);
//            for(String inputPath : inputPaths){
//                System.out.println(inputPath);
//            }
            for (String inputPath : inputPaths) {
                inputPath=inputPath.trim().substring(1);
                value = traverseJson(value, inputPath);
//                System.out.println(transformedJson);
                insertIntoJson(transformedJson, outputPath, value);
                value=gson.fromJson(jsonString, Object.class);
            }

        }
        return transformedJson;
    }

    private static Object traverseJson(Object json, String path) {
        String[] keys = path.split("/");
        for (String key : keys) {
            if (json instanceof Map) {
                json = ((Map<?, ?>) json).get(key);
            } else if (json instanceof List) {
                json = ((List<?>) json).get(Integer.parseInt(key));
            }
        }
        return json;
    }

    private static void insertIntoJson(Map<String, Object> json, List<String> path, Object value) {
        Map<String, Object> currentLevel = json;
        System.out.println(json);
        int lastIndex = path.size() -1;
        for (int i = 0; i < lastIndex; i++) {
            String key = path.get(i);
            if (!currentLevel.containsKey(key)) {
                currentLevel.put(key, new HashMap<String, Object>());
                currentLevel=(Map<String, Object>)currentLevel.get(key);
            }
            currentLevel = (Map<String, Object>) currentLevel.get(key);

        }
        System.out.println(currentLevel);
        currentLevel.put(path.get(lastIndex), value);

    }

    public static void main(String[] args) {
        String formatFilePath = "/home/nithin/Desktop/format.txt";
        Map<List<String>, List<String>> transformationRules = readTransformationRules(formatFilePath);
        System.out.println(transformationRules);

        // Read and parse the original JSON file
        String jsonFilePath = "/home/nithin/Desktop/sample.json";
        String jsonString;
        try {
            jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Gson gson = new GsonBuilder().create();
        Map<String, Object> json = gson.fromJson(jsonString, Map.class);

        // Perform the JSON transformation
        Map<String, Object> transformedJson = transformJson(transformationRules, json);

        // Convert the transformed JSON back to a string
        String transformedJsonString = gson.toJson(transformedJson);
        System.out.println(transformedJsonString);
    }
}
