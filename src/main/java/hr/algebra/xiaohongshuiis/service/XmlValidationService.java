package hr.algebra.xiaohongshuiis.service;

import com.thaiopensource.validate.ValidationDriver;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    errors.add("Warning at line " + exception.getLineNumber() +
                            ", column " + exception.getColumnNumber() + ": " + exception.getMessage());
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    errors.add("Error at line " + exception.getLineNumber() +
                            ", column " + exception.getColumnNumber() + ": " + exception.getMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    errors.add("Fatal error at line " + exception.getLineNumber() +
                            ", column " + exception.getColumnNumber() + ": " + exception.getMessage());
                }
            });

            validator.validate(new StreamSource(new ByteArrayInputStream(xmlContent.getBytes())));

        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                SAXParseException spe = (SAXParseException) e;
                errors.add("XSD Validation Error at line " + spe.getLineNumber() +
                        ", column " + spe.getColumnNumber() + ": " + spe.getMessage());
            } else {
                errors.add("XSD Validation Error: " + e.getMessage());
            }
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
            if (rngStream == null) {
                errors.add("RNG Schema file not found: " + rngPath);
                return errors;
            }

            InputSource rngSource = new InputSource(rngStream);

            if (!driver.loadSchema(rngSource)) {
                errors.add("RNG Schema loading failed - invalid schema file");
                return errors;
            }

            InputSource xmlSource = new InputSource(new StringReader(xmlContent));

            try {
                if (!driver.validate(xmlSource)) {
                    errors.add("RNG Validation failed - XML structure does not match RelaxNG schema. " +
                            "Common issues: wrong element order, missing required elements, or invalid element names.");
                }
            } catch (Exception validationException) {
                String message = validationException.getMessage();
                if (message != null) {
                    if (message.contains("line")) {
                        errors.add("RNG Error: " + message);
                    } else {
                        errors.add("RNG Validation Error: " + message +
                                ". Check that all elements match the RelaxNG schema definition.");
                    }
                } else {
                    errors.add("RNG Validation failed - XML structure does not match RelaxNG schema");
                }
            }

        } catch (SAXException e) {
            if (e instanceof SAXParseException) {
                SAXParseException spe = (SAXParseException) e;
                errors.add("RNG Error at line " + spe.getLineNumber() +
                        ", column " + spe.getColumnNumber() + ": " + spe.getMessage());
            } else {
                errors.add("RNG Validation Error: " + e.getMessage());
            }
        } catch (IOException e) {
            errors.add("RNG IO Error: " + e.getMessage());
        } catch (Exception e) {
            errors.add("RNG Validation Error: " + e.getMessage());
        }

        return errors;
    }
}