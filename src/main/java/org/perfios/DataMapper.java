package org.perfios;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface DataMapper {
    <T>T transformFile(String inputPath, String rulesPath, Class<T> className) throws IOException;
    <T>T transformString(String inputString, String rulesString, Class<T> className) throws JsonProcessingException;
    String  getTransformedString(String inputPath, String rulesPath, boolean isString);

}
