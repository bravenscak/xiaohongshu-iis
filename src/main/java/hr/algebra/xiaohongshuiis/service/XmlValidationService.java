package hr.algebra.xiaohongshuiis.service;

import com.thaiopensource.validate.ValidationDriver;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlValidationService {

    public List<String> validateWithXsd(String xmlContent, String xsdPath) {
        List<String> errors = new ArrayList<>();

        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream xsdStream = getClass().getClassLoader().getResourceAsStream(xsdPath);
            Schema schema = factory.newSchema(new StreamSource(xsdStream));

            javax.xml.validation.Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent.getBytes())));

        } catch (SAXException e) {
            errors.add("XSD Validation Error: " + e.getMessage());
        } catch (IOException e) {
            errors.add("IO Error: " + e.getMessage());
        } catch (Exception e) {
            errors.add("Validation Error: " + e.getMessage());
        }

        return errors;
    }

    public List<String> validateWithRng(String xmlContent, String rngPath) {
        List<String> errors = new ArrayList<>();

        try {
            ValidationDriver driver = new ValidationDriver();

            InputStream rngStream = getClass().getClassLoader().getResourceAsStream(rngPath);
            InputSource rngSource = new InputSource(rngStream);

            if (!driver.loadSchema(rngSource)) {
                errors.add("RNG Schema loading failed");
                return errors;
            }

            InputSource xmlSource = new InputSource(new StringReader(xmlContent));
            if (!driver.validate(xmlSource)) {
                errors.add("RNG Validation failed");
            }

        } catch (Exception e) {
            errors.add("RNG Validation Error: " + e.getMessage());
        }

        return errors;
    }
}