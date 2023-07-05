package com.example.DataMapper;


public class Test {
    public static void main(String[] args) {
        JsonTransformer3 transformer = new JsonTransformer3();
        TestModel test = transformer.getObjectFromInput("/home/nithin/Desktop/sample.json","/home/nithin/Desktop/format.txt", TestModel.class);
        System.out.println(test);
    }
}