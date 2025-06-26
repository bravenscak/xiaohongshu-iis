package hr.algebra.xiaohongshuiis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class JaxbValidationService {

    @Autowired
    private XmlGeneratorService xmlGeneratorService;

    public List<String> validateGeneratedXml() {
        List<String> validationMessages = new ArrayList<>();

        try {
            String generatedXml = xmlGeneratorService.generateUsersXml();

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream xsdStream = getClass().getClassLoader().getResourceAsStream("xsd/users.xsd");

            if (xsdStream == null) {
                validationMessages.add("XSD schema file not found: xsd/users.xsd");
                return validationMessages;
            }

            Schema schema = factory.newSchema(new StreamSource(xsdStream));
            Validator validator = schema.newValidator();

            validator.validate(new StreamSource(new ByteArrayInputStream(generatedXml.getBytes())));

            validationMessages.add("XML is valid according to XSD schema");

        } catch (Exception e) {
            validationMessages.add("Validation error: " + e.getMessage());
        }

        return validationMessages;
    }
}