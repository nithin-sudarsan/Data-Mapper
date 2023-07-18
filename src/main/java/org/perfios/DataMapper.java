package org.perfios;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.io.IOException;

public interface DataMapper {
    <T>T transformFile(String inputPath, String rulesPath, Class<T> className) throws IOException;
    <T> T transformFile(File input, File rules, Class<T> className);
    <T>T transformString(String inputString, String rulesString, Class<T> className) throws JsonProcessingException;
    String getTransformedString(File input, File rules, DataMapperImpl.Extension ext);
    String getTransformedString(String input, String rules, DataMapperImpl.Extension ext);
    <T> T transformBean(Object inputBean, File rules, Class<T> className);
    <T> T transformBean(Object inputBean, String rulesString, Class<T> className) throws JsonProcessingException;

}
