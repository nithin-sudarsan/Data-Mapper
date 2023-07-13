package org.perfios.Usage;

import org.perfios.DataMapper;
import org.perfios.DataMapperImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws IOException {
        DataMapper dm= new DataMapperImpl();
        File input = new File("/home/nithin/Desktop/DataMapper_ITR_demo/ITR_2022-23.json");
        File rules = new File("/home/nithin/Desktop/DataMapper_ITR_demo/rules.txt");
        String inputString;
        String rulesString;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(input.getAbsolutePath()));
            inputString = new String(bytes);
            byte[] bytes2 = Files.readAllBytes(Paths.get(rules.getAbsolutePath()));
            rulesString = new String(bytes2);
        } catch (IOException e) {
            throw new RuntimeException("Error reading files: " + e.getMessage(), e);
        }
        TestModel test1= dm.transformFile("/home/nithin/Desktop/DataMapper_ITR_demo/ITR_2022-23.json","/home/nithin/Desktop/DataMapper_ITR_demo/rules.txt", TestModel.class);
        TestModel test2= dm.transformFile(input,rules, TestModel.class);
        TestModel test3= dm.transformString(inputString,rulesString, TestModel.class);
        String test4 = dm.getTransformedString(input,rules, DataMapperImpl.Extension.JSON);
        String test5 = dm.getTransformedString(inputString,rulesString, DataMapperImpl.Extension.XML);

        System.out.println("TransformFile using file path:\n"+test1);
        System.out.println("TransformFile using file:\n"+test2);
        System.out.println("TransformString using file String:\n"+test3);
        System.out.println("getTransformedString using file :\n"+test4);
        System.out.println("getTransformedString using file string:\n"+test5);
    }
}
