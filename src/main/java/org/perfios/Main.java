package org.perfios;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args){
        String filePath=args[0];
        DataMapper dm = new DataMapperImpl();
        try {
            System.out.println(dm.generateMapStructInterfaceString(new File(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
