package hr.algebra.xiaohongshuiis.controller;

import hr.algebra.xiaohongshuiis.model.User;
import hr.algebra.xiaohongshuiis.repository.UserRepository;
import hr.algebra.xiaohongshuiis.service.XiaohongshuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private XiaohongshuService xiaohongshuService;

    @GetMapping
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            try {
                users = xiaohongshuService.searchUsers("momo");

                if (!users.isEmpty()) {
                    userRepository.saveAll(users);
                    System.out.println("Saved " + users.size() + " users from API to database");
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch users from API: " + e.getMessage());
            }
        }

        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setName(userDetails.getName());
            user.setRedId(userDetails.getRedId());
            user.setFollowersCount(userDetails.getFollowersCount());
            user.setNotesCount(userDetails.getNotesCount());
            user.setVerified(userDetails.getVerified());

            return ResponseEntity.ok(userRepository.save(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/fetch-from-api")
    public ResponseEntity<?> fetchUsersFromApi(@RequestParam(defaultValue = "momo") String keyword) {
        try {
            List<User> apiUsers = xiaohongshuService.searchUsers(keyword);

            if (!apiUsers.isEmpty()) {
                userRepository.saveAll(apiUsers);
                return ResponseEntity.ok(Map.of(
                        "message", "Successfully fetched and saved users from API",
                        "count", apiUsers.size(),
                        "keyword", keyword
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "message", "No users found for keyword: " + keyword,
                        "count", 0
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to fetch users from API: " + e.getMessage()
            ));
        }
    }
}