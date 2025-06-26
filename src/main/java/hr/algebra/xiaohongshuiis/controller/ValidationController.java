package hr.algebra.xiaohongshuiis.controller;

import hr.algebra.xiaohongshuiis.service.JaxbValidationService;
import hr.algebra.xiaohongshuiis.service.XmlGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    @Autowired
    private JaxbValidationService jaxbValidationService;

    @Autowired
    private XmlGeneratorService xmlGeneratorService;

    @GetMapping("/xml")
    public ResponseEntity<String> getGeneratedXml() {
        try {
            String xml = xmlGeneratorService.generateUsersXml();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xml);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating XML: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateXml() {
        try {
            List<String> validationMessages = jaxbValidationService.validateGeneratedXml();
            return ResponseEntity.ok(Map.of("validationMessages", validationMessages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Validation failed: " + e.getMessage()));
        }
    }
}