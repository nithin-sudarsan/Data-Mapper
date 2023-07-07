package com.example.DataMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Test {
    public static void main(String[] args) {
        JsonTransformer3 transformer = new JsonTransformer3();
        TestModel test1 = null;
        TestModel test2= null;
        String inputString = "{\n  \"details\": {\n    \"name\": {\n      \"firstName\": \"nithin\",\n      \"lastName\": \"s\"\n    },\n    \"addresses\": [\n    {\"address1\":{\n      \"phoneNumber\":1234,\n      \"state\":\"karnataka\"\n    }},\n    {\"address2\":{\n      \"phoneNumber\":5678,\n      \"state\":\"Kerala\"\n    }}\n    ]   \n  }\n}\n";
        String formatString="TestModel/name = details/name/firstName #sum details/name/lastName\nTestModel/details = details/addresses[]/address1/state \nTestModel/age = #default 25";
        try {
            test1 = transformer.getObjectFromInputFile("/home/nithin/Desktop/sample.json", "/home/nithin/Desktop/format.txt", TestModel.class);
            test2 = transformer.getObjectFromInputString(inputString, formatString, TestModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(test1);
        System.out.println(test2);
    }
}