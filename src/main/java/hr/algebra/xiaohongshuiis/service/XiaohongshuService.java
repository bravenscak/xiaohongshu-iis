package hr.algebra.xiaohongshuiis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.xiaohongshuiis.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class XiaohongshuService {

    @Value("${xiaohongshu.api.key}")
    private String apiKey;

    @Value("${xiaohongshu.api.host}")
    private String apiHost;

    @Value("${xiaohongshu.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<User> searchUsers(String keyword) {
        String url = baseUrl + "/api/xiaohongshu/search-user/v2?keyword=" + keyword + "&sort=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return parseUsers(response.getBody());
    }

    private List<User> parseUsers(String json) {
        List<User> users = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode usersNode = root.get("data").get("users");

            for (JsonNode userNode : usersNode) {
                User user = new User();
                user.setId(userNode.get("id").asText());
                user.setName(userNode.get("name").asText());
                user.setRedId(userNode.get("red_id").asText());
                user.setVerified(userNode.get("red_official_verified").asBoolean());

                String desc = userNode.get("desc").asText();
                user.setFollowersCount(extractFollowers(desc));
                user.setNotesCount(extractNotes(desc));

                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private Integer extractFollowers(String desc) {
        Pattern pattern = Pattern.compile("粉丝·([0-9.]+)万");
        Matcher matcher = pattern.matcher(desc);
        if (matcher.find()) {
            return (int) (Double.parseDouble(matcher.group(1)) * 10000);
        }
        return 0;
    }

    private Integer extractNotes(String desc) {
        Pattern pattern = Pattern.compile("笔记·([0-9]+)");
        Matcher matcher = pattern.matcher(desc);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }
}