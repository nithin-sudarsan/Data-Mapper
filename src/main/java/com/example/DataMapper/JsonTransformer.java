package com.example.DataMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class JsonTransformer {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputFilePath = "/home/nithin/Desktop/sample.json";
        String formatFilePath = "/home/nithin/Desktop/format.txt";
        String outputFilePath = "/home/nithin/Desktop/output.json";
        try {
            JsonNode rootNode = objectMapper.readTree(new File(inputFilePath));
            Map<String, String> transformationMap = readTransformationRules(formatFilePath);
            ObjectNode transformedNode = objectMapper.createObjectNode();

            transformationMap.forEach((outputPath, inputPath) -> {
                JsonNode valueNode = rootNode.at(inputPath);
                addNode(transformedNode, outputPath, valueNode);
            });
            objectMapper.writeValue(new File(outputFilePath), transformedNode);

            System.out.println("Transformation complete. Check 'output.json' file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addNode(ObjectNode parentNode, String path, JsonNode valueNode) {
        String[] parts = path.split("/");

        ObjectNode currentNode = parentNode;
//        System.out.println(currentNode);
        System.out.println(valueNode);

        for (int i = 1; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!currentNode.has(part) || !currentNode.get(part).isObject()) {
                ObjectNode newNode = currentNode.objectNode();
                currentNode.set(part, newNode);
            }
            else if(currentNode.has(part) && currentNode.get(part).isObject())
            {
                currentNode.put(part, valueNode);
            }
//            System.out.println(currentNode.get(part));
            currentNode = (ObjectNode) currentNode.get(part);
        }

        String lastPart = parts[parts.length - 1];
        if (lastPart.contains("+")) {
            String[] fieldNames = lastPart.split("\\+");
            StringBuilder concatenatedValue = new StringBuilder();
            for (String fieldName : fieldNames) {
                JsonNode fieldValue = parentNode.at(fieldName.trim());
                if (fieldValue != null && fieldValue.isTextual()) {
                    concatenatedValue.append(fieldValue.asText()).append(" ");
                }
            }
            if (concatenatedValue.length() > 0) {
                currentNode.set(lastPart, parentNode.textNode(concatenatedValue.toString().trim()));
            }
        } else {
            if (valueNode != null) {
                currentNode.set(lastPart, valueNode);
            }
        }
    }


    private static Map<String, String> readTransformationRules(String formatFilePath) {
        Map<String, String> transformationMap = new HashMap<>();

        try (Stream<String> stream = Files.lines(Paths.get(formatFilePath))) {
            stream.forEach(line -> {
                String[] parts = line.split("=");
                String outputPath = parts[0].trim();
                String inputPath = parts[1].trim();
                transformationMap.put(outputPath, inputPath);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transformationMap;
    }
}