package hr.algebra.xiaohongshuiis.controller;

import hr.algebra.xiaohongshuiis.model.User;
import hr.algebra.xiaohongshuiis.repository.UserRepository;
import hr.algebra.xiaohongshuiis.service.XmlValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XmlValidationService xmlValidationService;

    @PostMapping(value = "/xml", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createUserFromXml(@RequestBody String xmlContent) {

        List<String> validationErrors = xmlValidationService.validateWithXsd(xmlContent, "xsd/user.xsd");

        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", validationErrors));
        }

        try {
            JAXBContext context = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            User user = (User) unmarshaller.unmarshal(new StringReader(xmlContent));

            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "XML parsing error: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/xml-rng", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> createUserFromXmlRng(@RequestBody String xmlContent) {

        List<String> validationErrors = xmlValidationService.validateWithRng(xmlContent, "rng/user.rng");

        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", validationErrors));
        }

        try {
            JAXBContext context = JAXBContext.newInstance(User.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            User user = (User) unmarshaller.unmarshal(new StringReader(xmlContent));

            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "XML parsing error: " + e.getMessage()));
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
}