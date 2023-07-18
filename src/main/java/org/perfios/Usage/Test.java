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
        File input = new File("/home/nithin/Desktop/sample.json");
        File rules = new File("/home/nithin/Desktop/format.txt");
        File newRules= new File("/home/nithin/Desktop/formatNew.txt");
        String inputString;
        String rulesString;
        String newRulesString;
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(input.getAbsolutePath()));
            inputString = new String(bytes);
            byte[] bytes2 = Files.readAllBytes(Paths.get(rules.getAbsolutePath()));
            rulesString = new String(bytes2);
            byte[] bytes3 = Files.readAllBytes(Paths.get(newRules.getAbsolutePath()));
            newRulesString = new String(bytes3);
        } catch (IOException e) {
            throw new RuntimeException("Error reading files: " + e.getMessage(), e);
        }
        TestModel test1= dm.transformFile("/home/nithin/Desktop/sample.json","/home/nithin/Desktop/format.txt", TestModel.class);
        TestModel test2= dm.transformFile(input,rules, TestModel.class);
        TestModel test3= dm.transformString(inputString,rulesString, TestModel.class);
        String test4 = dm.getTransformedString(input,rules, DataMapperImpl.Extension.JSON);
        String test5 = dm.getTransformedString(inputString,rulesString, DataMapperImpl.Extension.XML);
        TestModel2 test6=dm.transformBean(test1,newRulesString, TestModel2.class);
        TestModel2 test7=dm.transformBean(test2,newRules,TestModel2.class);

        System.out.println("TransformFile using file path:\n"+test1);
        System.out.println("TransformFile using file:\n"+test2);
        System.out.println("TransformString using file String:\n"+test3);
        System.out.println("getTransformedString using file :\n"+test4);
        System.out.println("getTransformedString using file string:\n"+test5);
        System.out.println("New Rules string from rules String:\n"+test6);
        System.out.println("New Rules string from rules file:\n"+test7);
    }
}
