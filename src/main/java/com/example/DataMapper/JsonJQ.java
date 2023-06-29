package com.example.DataMapper;

import com.google.gson.Gson;

import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonJQ {
    public static void main(String[] args) {
        String jsonFilePath = "/home/nithin/Desktop/sample.json";
        String desiredPath = ".details.addresses[].address";

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("jq", desiredPath, jsonFilePath);
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line);
            }
            String output = outputBuilder.toString();
            // Wait for the process to finish
//            Gson gson = new Gson();
//            Object jsonObject = gson.fromJson(output, Object.class);
            System.out.println(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
