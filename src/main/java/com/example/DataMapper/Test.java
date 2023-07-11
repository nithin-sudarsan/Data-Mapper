package com.example.DataMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.json.Json;

public class Test {
    public static void main(String[] args) {
        String inputString = "{\n  \"details\": {\n    \"name\": {\n      \"firstName\": \"nithin\",\n      \"lastName\": \"s\"\n    },\n    \"addresses\": [\n    {\"address1\":{\n      \"phoneNumber\":1234,\n      \"state\":\"karnataka\"\n    }},\n    {\"address2\":{\n      \"phoneNumber\":5678,\n      \"state\":\"Kerala\"\n    }}\n    ]   \n  }\n}\n";
        String formatString = "TestModel/name = details/name/firstName #sum details/name/lastName\nTestModel/details = details/addresses[]/address1/state \nTestModel/age = #default 25";
        try {
//            test1 = JsonTransformer3.getObjectFromInputFile("/home/nithin/Desktop/sample.json", "/home/nithin/Desktop/format.txt", TestModel.class);
//            test2 = JsonTransformer3.getObjectFromInputString(inputString, formatString, TestModel.class);
            TestModel test1 = JsonTransformer3.transformFile("/home/nithin/Desktop/sample.json", "/home/nithin/Desktop/format.txt", TestModel.class);
            TestModel test2= JsonTransformer3.transformString(inputString, formatString,TestModel.class);
//            String test3=JsonTransformer3.getString("/home/nithin/Desktop/sample.json", "/home/nithin/Desktop/format.txt", TestModel.class);
            System.out.println(test1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}