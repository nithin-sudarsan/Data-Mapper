package org.perfios.Usage;

import org.perfios.DataMapper;
import org.perfios.DataMapperImpl;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        DataMapper dm= new DataMapperImpl();
        TestModel test= dm.transformFile("/home/nithin/Desktop/sample.json","/home/nithin/Desktop/format.txt", TestModel.class);
        System.out.println(test);
    }
}
