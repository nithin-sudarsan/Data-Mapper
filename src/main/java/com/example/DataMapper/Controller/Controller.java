package com.example.DataMapper.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@RestController
public class Controller {
    @PostMapping("/transform")
    public String transformXml(@RequestParam("file") MultipartFile file, @RequestParam("file2") MultipartFile file2) throws IOException {
        String xmlString = new String(file.getBytes(), StandardCharsets.UTF_8);
        String xsltString = new String(file2.getBytes(), StandardCharsets.UTF_8);
        String transformedOutput = null;
        try {
            // Create the XML source from the input XML content
            Source xmlSource = new StreamSource(new StringReader(xmlString));

            // Create the XSLT source from the input XSLT content
            Source xsltSource = new StreamSource(new StringReader(xsltString));

            // Create the templates object from the XSLT source
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Templates templates = transformerFactory.newTemplates(xsltSource);

            // Create the result writer
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);

            // Create the transformer from the templates
            Transformer transformer = templates.newTransformer();
            transformer.transform(xmlSource, result);

            // Get the transformed output
            transformedOutput = writer.toString();
            System.out.println(transformedOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transformedOutput;
    }
}
