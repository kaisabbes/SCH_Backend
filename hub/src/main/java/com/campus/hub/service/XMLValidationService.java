// src/main/java/com/campus/hub/service/XMLValidationService.java
package com.campus.hub.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class XMLValidationService {

    private static final String XSD_PATH = "xsd/ServiceRequest.xsd";
    private Schema schema;

    public XMLValidationService() {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source schemaFile = new StreamSource(new ClassPathResource(XSD_PATH).getInputStream());
            schema = factory.newSchema(schemaFile);
        } catch (Exception e) {
            log.error("Failed to load XSD schema", e);
            throw new RuntimeException("XSD schema loading failed", e);
        }
    }

    public ValidationResult validateXML(String xmlContent) {
        ValidationResult result = new ValidationResult();
        result.setValid(false);

        try {
            // Create validator
            Validator validator = schema.newValidator();

            // Validate
            Source source = new StreamSource(new StringReader(xmlContent));
            validator.validate(source);

            result.setValid(true);
            result.setMessage("XML is valid against XSD schema");

            // Extract data using XPath
            extractDataWithXPath(xmlContent, result);

        } catch (SAXException e) {
            result.setMessage("XML validation failed: " + e.getMessage());
            result.getErrors().add(e.getMessage());
        } catch (Exception e) {
            result.setMessage("Error during validation: " + e.getMessage());
            result.getErrors().add(e.getMessage());
        }

        return result;
    }

    private void extractDataWithXPath(String xmlContent, ValidationResult result) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new org.xml.sax.InputSource(new StringReader(xmlContent)));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Extract key information
            Map<String, String> extractedData = new HashMap<>();

            // XPath expressions
            String[] expressions = {
                    "//studentId",
                    "//fullName",
                    "//email",
                    "//serviceType",
                    "//title",
                    "//priority",
                    "//urgency"
            };

            for (String expr : expressions) {
                try {
                    XPathExpression xPathExpr = xpath.compile(expr);
                    String value = (String) xPathExpr.evaluate(doc, XPathConstants.STRING);
                    if (value != null && !value.trim().isEmpty()) {
                        String key = expr.replace("//", "").replace("/", "_");
                        extractedData.put(key, value.trim());
                    }
                } catch (Exception e) {
                    log.debug("Failed to extract with XPath: {}", expr, e);
                }
            }

            result.setExtractedData(extractedData);

        } catch (Exception e) {
            log.error("XPath extraction failed", e);
        }
    }

    public ValidationResult validateXMLFile(File xmlFile) {
        try {
            // Convert file to string for validation
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            // Convert Document back to String for validation
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            java.io.StringWriter writer = new java.io.StringWriter();
            transformer.transform(new javax.xml.transform.dom.DOMSource(doc),
                    new javax.xml.transform.stream.StreamResult(writer));

            return validateXML(writer.toString());

        } catch (Exception e) {
            ValidationResult result = new ValidationResult();
            result.setValid(false);
            result.setMessage("Error reading XML file: " + e.getMessage());
            return result;
        }
    }

    // Inner class for validation result
    @Getter
    @Setter
    public static class ValidationResult {
        private boolean valid;
        private String message;
        private Map<String, String> extractedData = new HashMap<>();
        private List<String> errors = new ArrayList<>();

        public void addError(String error) {
            this.errors.add(error);
        }
    }
}