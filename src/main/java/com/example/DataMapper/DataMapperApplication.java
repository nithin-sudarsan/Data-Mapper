package com.example.DataMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DataMapperApplication {

	public static void main(String[] args) {
		ObjectMapper objectMapper = new ObjectMapper();
		String inputFilePath = "/home/nithin/Desktop/sample.xml";
		String formatFilePath = "/home/nithin/Desktop/format.txt";
		String outputFilePath = "/home/nithin/Desktop/output.xml";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new FileReader(inputFilePath)));
			XPath xPath= XPathFactory.newInstance().newXPath();
			NodeList nodeList= (NodeList) xPath.evaluate("/details/name", document, XPathConstants.NODESET);
				NodeList node = nodeList.item(0).getChildNodes();
				for (int j=0; j<node.getLength(); j++){
					System.out.println(node.item(j).getTextContent().trim());
				}
//				System.out.println(nodeList.item(i).getNodeName()+":"+nodeList.item(i));




//			JsonNode rootNode = objectMapper.readTree(new File(inputFilePath));
//			Map<List<String>, List<String>> transformationMap = readTransformationRules(formatFilePath);
//			ObjectNode transformedNode = objectMapper.createObjectNode();
//
//			transformationMap.forEach((inputPaths, outputPaths) -> {
//				JsonNode valueNode = getNodeFromPaths(rootNode, inputPaths);
//				for (String outputPath : outputPaths) {
//					addNode(transformedNode, outputPath, valueNode);
//				}
//			});
//
//			objectMapper.writeValue(new File(outputFilePath), transformedNode);

			System.out.println("Transformation complete. Check 'output.json' file.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	private static void addNode(ObjectNode parentNode, String path, JsonNode valueNode) {
		String[] parts = path.split("/");

		for (int i = 1; i < parts.length - 1; i++) {
			String part = parts[i];
			if (!parentNode.has(part) || !parentNode.get(part).isObject()) {
				ObjectNode newNode = parentNode.objectNode();
				parentNode.set(part, newNode);
			}

			parentNode = (ObjectNode) parentNode.get(part);
		}

		String lastPart = parts[parts.length - 1];
		parentNode.set(lastPart, valueNode);
	}

	private static Map<List<String>, List<String>> readTransformationRules(String changesFilePath) throws IOException {
		Map<List<String>, List<String>> transformationMap = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(changesFilePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("=");
				String inputPath = parts[0].trim();
				String outputPath = parts[1].trim();

				List<String> inputPaths = parseInputPaths(inputPath);
				List<String> outputPaths = parseOutputPaths(outputPath);

				transformationMap.put(inputPaths, outputPaths);
			}
			System.out.println(transformationMap);
		}

		return transformationMap;
	}

	private static List<String> parseInputPaths(String inputPath) {
		List<String> inputPaths = new ArrayList<>();
		if (inputPath.contains("+")) {
			String[] paths = inputPath.split("\\+");
			for (String path : paths) {
				inputPaths.add(path.trim());
			}
		} else {
			inputPaths.add(inputPath);
		}
		return inputPaths;
	}

	private static List<String> parseOutputPaths(String outputPath) {
		List<String> outputPaths = new ArrayList<>();
		outputPaths.add(outputPath);
		return outputPaths;
	}

	private static JsonNode getNodeFromPaths(JsonNode rootNode, List<String> paths) {
		JsonNode currentNode = rootNode;
		for (String path : paths) {
			currentNode = currentNode.at(formatJsonPointer(path));
		}
		return currentNode;
	}

	private static String formatJsonPointer(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return path;
	}
}
